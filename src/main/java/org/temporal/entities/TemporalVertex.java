package org.temporal.entities;

/**
 * Temporal vertex representation.
 */
public class TemporalVertex extends TemporalProperty {
    private final int id;

    private int arrivedAt;

    public TemporalVertex(int id, int startTime, int endTime) {
        super(startTime, endTime);
        this.id = id;
    }

    /**
     * Get the vertex id.
     * @return Vertex id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Set the time at which the vertex is reached.
     * @param value Time at which the vertex is reached.
     */
    public void setArrivedAt(int value) {
        this.arrivedAt = value;
    }

    /**
     * Get the time at which the vertex is reached.
     * @return Time at which the vertex is reached.
     */
    public int getArrivedAt() {
        return this.arrivedAt;
    }
}
