package com.redcreator37.playermanagement.DataModels;

import java.util.Objects;

/**
 * Represents an in-game job
 */
public class Job {

    /**
     * Unique database id
     */
    private final int id;

    /**
     * Job name
     */
    private final String name;

    /**
     * Job description
     */
    private String description;

    /**
     * Job constructor
     *
     * @param id          the new database id
     * @param name        job name
     * @param description job description
     */
    public Job(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    /**
     * Returns the job name in a string
     *
     * @return the job name
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Provides hash code functionality
     *
     * @return the hash code for this Job instance
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = (37 * result) + getId();
        result = (37 * result) + Objects.hashCode(getName());
        result = (37 * result) + Objects.hashCode(getDescription());
        return result;
    }

}
