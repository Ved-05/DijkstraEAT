package org.temporal.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Temporal graph representation.
 */
public class TemporalGraph {
    final Map<Integer, TemporalVertex> vertices;
    final Map<Integer, Map<Integer, TemporalEdge>> edges;


    public TemporalGraph() {
        this.vertices = new HashMap<>();
        this.edges = new HashMap<>();
    }

    /**
     * Add a vertex to the graph.
     *
     * @param vertexId  Vertex id.
     * @param startTime Start time of the vertex.
     * @param endTime   End time of the vertex.
     */
    public void addVertex(int vertexId, int startTime, int endTime) {
        // Add vertex
        TemporalVertex vertex = new TemporalVertex(vertexId, startTime, endTime);
        this.vertices.put(vertex.getId(), vertex);
        this.edges.put(vertex.getId(), new HashMap<>());
    }

    /**
     * Add an edge to the graph.
     *
     * @param source      Source vertex id.
     * @param destination Destination vertex id.
     * @param startTime   Start time of the edge.
     * @param endTime     End time of the edge.
     */
    public void addEdge(int source, int destination, int startTime, int endTime) {
        TemporalEdge edge = new TemporalEdge(source, destination, startTime, endTime);
        this.edges.get(source).put(destination, edge);
    }

    /**
     * Remove a vertex from the graph.
     *
     * @param vertexId Vertex id.
     * @param endTime  End time of the vertex.
     */
    public void removeVertex(int vertexId, int endTime) {
        this.vertices.get(vertexId).setEndTime(endTime);
    }

    /**
     * Remove an edge from the graph.
     *
     * @param source      Source vertex id.
     * @param destination Destination vertex id.
     * @param endTime     End time of the edge.
     */
    public void removeEdge(int source, int destination, int endTime) {
        this.edges.get(source).get(destination).setEndTime(endTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.vertices.forEach((vertexId, vertex) -> {
            sb.append(vertexId).append("\t").append(vertex.getStartTime()).append("\t").append(vertex.getEndTime());
            this.edges.get(vertexId).forEach((destination, edge) -> sb.append("\t").append(destination).append("\t")
                    .append(edge.getStartTime()).append("\t").append(edge.getEndTime()));
            sb.append("\n");
        });

        return sb.toString();
    }

    /**
     * Get the number of edges in the graph.
     *
     * @return Number of edges in the graph.
     */
    public int getNEdges() {
        int size = 0;
        for (Map<Integer, TemporalEdge> innerMap : this.edges.values()) size += innerMap.size();
        return size;
    }

    /**
     * Get the edges in the graph.
     * @return Edges in the graph.
     */
    public Map<Integer, Map<Integer, TemporalEdge>> getEdges() {
        return edges;
    }

    /**
     * Get the vertices in the graph.
     * @return Vertices in the graph.
     */
    public Map<Integer, TemporalVertex> getVertices() {
        return vertices;
    }
}