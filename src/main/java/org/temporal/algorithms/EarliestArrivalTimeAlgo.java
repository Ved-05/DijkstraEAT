package org.temporal.algorithms;

import org.temporal.entities.TemporalEdge;
import org.temporal.entities.TemporalGraph;
import org.temporal.entities.TemporalVertex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Earliest Arrival Time Algorithm for Temporal Graphs using Dijkstra's Algorithm
 */
public class EarliestArrivalTimeAlgo {
    private static final Pattern SEPARATOR = Pattern.compile("[\t ]");
    private static TemporalGraph temporalGraph;
    private static String tinkInputPath;

    /**
     * Read and applu mutations from a file to the temporal graph.
     *
     * @param pathToFile Path to the file containing mutations.
     *                   Each line in the file is of the form:
     *                   <timeStep> <mutationType> <vertexId1> <vertexId2> ...
     *                   where mutationType is one of {0, 1, 2, 3} representing {add vertex, add edge, del edge, del vertex}
     *                   and vertexId1, vertexId2, ... are the vertices involved in the mutation.
     *                   The file is sorted by timeStep.
     */
    private static void applyMutation(Path pathToFile) {
        try {
            Files.walk(pathToFile)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().equals("part-00000.txt"))
                    .flatMap(path -> {
                        try {
                            return Files.readAllLines(path).stream();
                        } catch (IOException e) {
                            System.err.println("Error reading file: " + path + " " + e.getMessage());
                        }
                        return null;
                    })
                    .map(str -> SEPARATOR.split(str.replace("inf", String.valueOf(Integer.MAX_VALUE))))
                    .sorted(Comparator.comparingInt(tokens -> Integer.parseInt(tokens[1])))
                    .forEach(tokens -> {
                        int timeStep = Integer.parseInt(tokens[0]);
                        int mutationType = Integer.parseInt(tokens[1]);
                        int i = 0;

                        try {
                            switch (mutationType) {
                                case 0:
                                    // add vertex
                                    for (i = 2; i < tokens.length; i++)
                                        temporalGraph.addVertex(Integer.parseInt(tokens[i]), timeStep, Integer.MAX_VALUE);
                                    break;
                                case 1:
                                    // add edge
                                    for (i = 2; i < tokens.length; i += 2) {
                                        temporalGraph.addEdge(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1]),
                                                timeStep, Integer.MAX_VALUE);
                                    }
                                    break;
                                case 2:
                                    // del edge
                                    for (i = 2; i < tokens.length; i += 2)
                                        temporalGraph.removeEdge(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1]), timeStep);
                                    break;
                                case 3:
                                    // del vertex
                                    for (i = 2; i < tokens.length; i++)
                                        temporalGraph.removeVertex(Integer.parseInt(tokens[i]), timeStep);
                                    break;
                                default:
                                    throw new RuntimeException("Invalid mutation type: " + mutationType);
                            }
                        } catch (NullPointerException e) {
                            throw new RuntimeException(String.format("Operation %s, VID %s\nMsg %s", tokens[1], tokens[i], e.getMessage()));
                        }
                    });
        } catch (IOException e) {
            System.out.println("ERROR: Processing file - " + pathToFile);
        }
    }

    /**
     * Apply mutations for a given time step.
     *
     * @param timeStep Time step for which mutations are to be applied.
     * @return Time taken to apply mutations.
     */
    public static long applyMutation(int timeStep) {
        long start = System.currentTimeMillis();
        File mutationsDirectory = new File(tinkInputPath + "/time=" + timeStep);
        File[] files = mutationsDirectory.listFiles(); // List all worker directories
        if (files != null)
            Arrays.stream(files).sorted().forEach(file -> applyMutation(file.toPath()));
        return System.currentTimeMillis() - start;
    }

    /**
     * Compute the earliest arrival time for all vertices from a given source vertex.
     *
     * @param srcVertexId Source vertex id.
     * @param timeStep    Time step for which the computation is to be done.
     */
    public static void compute(int srcVertexId, int timeStep) {
        int size = temporalGraph.getVertices().size();
        PriorityQueue<TemporalVertex> pq = new PriorityQueue<>(size,
                Comparator.comparingInt(TemporalVertex::getArrivedAt));
        for (TemporalVertex vertex : temporalGraph.getVertices().values()) vertex.setArrivedAt(Integer.MAX_VALUE);

        TemporalVertex sourceVertex = temporalGraph.getVertices().get(srcVertexId);
        if (sourceVertex == null) {
            System.out.println("ERROR: Error processing compute. Source vetex not present.");
            return;
        }
        sourceVertex.setArrivedAt(0);
        pq.add(sourceVertex);

        Set<Integer> seen = new HashSet<>();

        while (!pq.isEmpty()) {
            TemporalVertex u = pq.poll();
            if (seen.contains(u.getId())) continue;
            seen.add(u.getId());

            for (Map.Entry<Integer, TemporalEdge> v : temporalGraph.getEdges().get(u.getId()).entrySet()) {
                int reachingTime = Math.max(u.getArrivedAt(), v.getValue().getStartTime()) + 1;
                // Update the arrival time of the vertex if the new time is less than the current arrival time of the vertex
                // and the reaching time is within the time step and the edge is active at the reaching time.
                if (reachingTime <= timeStep && reachingTime < v.getValue().getEndTime() &&
                        reachingTime > v.getValue().getStartTime()) {
                    if (temporalGraph.getVertices().get(v.getKey()).getArrivedAt() > reachingTime) {
                        temporalGraph.getVertices().get(v.getKey()).setArrivedAt(reachingTime);
                        pq.add(temporalGraph.getVertices().get(v.getKey()));
                    }
                }
            }
        }
    }

    /**
     * Main method to run the program.
     *
     * @param args Command line arguments. Expected arguments are: <inputDirectoryPath> <startTimeStep> <endTimeStep> <srcVertex>
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Usage: javac EarliestArrivalTimeAlgo \n " +
                    "java EarliestArrivalTimeAlgo <inputDirectoryPath> <startTimeStep> <endTimeStep> <srcVertex>");
            return;
        }

        tinkInputPath = args[0];
        int startTimeStep = Integer.parseInt(args[1]);
        int endTimeStep = Integer.parseInt(args[2]);
        int srcVertex = Integer.parseInt(args[3]);

        System.out.println("INFO: Processing mutations from " + tinkInputPath + " for TS range (" + startTimeStep +
                ", " + endTimeStep + ").");
        temporalGraph = new TemporalGraph();
        while (startTimeStep < endTimeStep) {
            // apply mutations
            long applyMS = applyMutation(startTimeStep);

            // call compute
            long computeMS = System.currentTimeMillis();
            compute(srcVertex, startTimeStep);
            computeMS = System.currentTimeMillis() - computeMS;

            // write results
            long writeMS = 0;
            if (startTimeStep != 0 && startTimeStep % 10 == 0) {
                writeMS = System.currentTimeMillis();
                StringBuilder sb = new StringBuilder();
                temporalGraph.getVertices().values().forEach(temporalVertex -> sb.append(temporalVertex.getId())
                        .append(',').append(temporalVertex.getArrivedAt()).append('\n'));
                File outputFile = new File("output/vertices-" + startTimeStep + ".csv");
                BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile, false));
                outputWriter.write(sb.toString());
                outputWriter.close();
                writeMS = System.currentTimeMillis() - writeMS;
            }
            System.out.println(startTimeStep + ", " + temporalGraph.getVertices().size() + ", " + temporalGraph.getNEdges() + ", "
                    + applyMS + ", " + computeMS + ", " + writeMS);
            startTimeStep++;
        }
    }
}
