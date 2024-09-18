package org.temporal.entities;

/**
 * Temporal edge and vertex representation.
 */
public class TemporalEdge extends TemporalProperty {
    private final int source;
    private final int destination;

    public TemporalEdge(int source, int destination, int startTime, int endTime) {
        super(startTime, endTime);
        this.source = source;
        this.destination = destination;
    }

    /**
     * Get the source vertex id.
     * @return Source vertex id.
     */
    public int getSource() {
        return this.source;
    }

    /**
     * Get the destination vertex id.
     * @return Destination vertex id.
     */
    public int getDestination() {
        return this.destination;
    }
}