package com.redcreator37.playermanagement.DataModels;

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
}
