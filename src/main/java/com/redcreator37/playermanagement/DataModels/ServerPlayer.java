package com.redcreator37.playermanagement.DataModels;

import java.util.Objects;

/**
 * Represents a Minecraft player on the server
 */
public class ServerPlayer {

    /**
     * Unique database id
     */
    private final int id;

    /**
     * Player's username/uuid combination tag
     */
    private final PlayerTag tag;

    /**
     * Player real-world name (or anything else they set
     * by themselves)
     */
    private String name;

    /**
     * Date when the player has first registered for a job at the server
     */
    private String joinDate;

    /**
     * In-game job (optional)
     */
    private Job job;

    /**
     * In-game company (optional)
     */
    private Company company;

    /**
     * Any additional notes in the database (optional)
     */
    private String notes;

    /**
     * The number of received punishments (used for auto-banning)
     */
    private int punishments;

    /**
     * ServerPlayer constructor
     *
     * @param id       the new database id
     * @param username in-game username
     * @param uuid     unique Minecraft player uuid
     */
    public ServerPlayer(int id, String username, String uuid) {
        this.id = id;
        this.tag = new PlayerTag(username, uuid);
    }

    public int getPunishments() {
        return punishments;
    }

    public void setPunishments(int punishments) {
        this.punishments = punishments;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return tag.getUuid();
    }

    public String getUsername() {
        return tag.getUsername();
    }

    public int getId() {
        return id;
    }

    /**
     * Returns the player's username in a string
     *
     * @return the player's username
     */
    @Override
    public String toString() {
        return this.tag.getUsername();
    }

    /**
     * Provides hash code functionality
     *
     * @return the hash code for this ServerPlayer instance
     */
    @Override
    public int hashCode() {
        int result = 17;
        result *= 37 + getId();
        result *= 37 + Objects.hashCode(getUsername());
        result *= 37 + Objects.hashCode(getUuid());
        result *= 37 + Objects.hashCode(getName());
        result *= 37 + Objects.hashCode(getJoinDate());
        result *= 37 + Objects.hashCode(getJob());
        result *= 37 + Objects.hashCode(getCompany());
        result *= 37 + Objects.hashCode(getNotes());
        result *= 37 + getPunishments();
        result *= 37 + getPunishments();
        return result;
    }

}
