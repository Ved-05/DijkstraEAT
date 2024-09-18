package org.temporal.entities;

/**
 * Temporal graph representation.
 */
abstract class TemporalProperty {
    private final int startTime;

    private int endTime;

    public TemporalProperty(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Get the start time of the temporal property.
     * @return Start time of the temporal property.
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Get the end time of the temporal property.
     * @return End time of the temporal property.
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Set the end time of the temporal property.
     * @param endTime End time of the temporal property.
     */
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

}
