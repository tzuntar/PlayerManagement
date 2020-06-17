package com.redcreator37.playermanagement.DataModels;

/**
 * Represents a Minecraft player on the server
 */
public class ServerPlayer {

    /**
     * Unique database id
     */
    private final int id;

    /**
     * In-game player name
     */
    private final String username;

    /**
     * Unique Minecraft player id
     */
    private final String uuid;

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
        this.username = username;
        this.uuid = uuid;
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
        return uuid;
    }

    public String getUsername() {
        return username;
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
        return this.username;
    }

}
