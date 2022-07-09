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

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Job))
            return false;
        Job j = (Job) obj;
        return j.id == this.id
                && j.name.equals(this.name)
                && j.description.equals(this.description);
    }

    @Override
    public int hashCode() {
        int c = Integer.hashCode(this.id);
        c = 31 * c + this.name.hashCode();
        c = 31 * c + this.description.hashCode();
        return c;
    }

}
