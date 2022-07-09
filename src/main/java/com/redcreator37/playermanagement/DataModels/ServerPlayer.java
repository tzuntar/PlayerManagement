package com.redcreator37.playermanagement.DataModels;

import java.util.Optional;
import java.util.UUID;

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
     * @param id  the new database id
     * @param tag the player's username / uuid combination tag
     */
    public ServerPlayer(int id, PlayerTag tag) {
        this.id = id;
        this.tag = tag;
    }

    public int getPunishments() {
        return punishments;
    }

    public void setPunishments(int punishments) {
        this.punishments = punishments;
    }

    public Optional<String> getNotes() {
        return Optional.ofNullable(notes);
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Optional<Company> getCompany() {
        return Optional.ofNullable(company);
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Optional<Job> getJob() {
        return Optional.ofNullable(job);
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

    public UUID getUuid() {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof ServerPlayer))
            return false;
        ServerPlayer p = (ServerPlayer) obj;
        return p.id == this.id
                && p.tag.equals(this.tag)
                && p.name.equals(this.name)
                && p.joinDate.equals(this.joinDate)
                && p.job.equals(this.job)
                && p.company.equals(this.company)
                && p.notes.equals(this.notes)
                && p.punishments == this.punishments;
    }

    @Override
    public int hashCode() {
        int c = Integer.hashCode(this.id);
        c = 31 * c + this.tag.hashCode();
        c = 31 * c + this.name.hashCode();
        c = 31 * c + this.joinDate.hashCode();
        c = 31 * c + this.job.hashCode();
        c = 31 * c + this.company.hashCode();
        c = 31 * c + this.notes.hashCode();
        c = 31 * c + Integer.hashCode(this.punishments);
        return c;
    }

}
