package com.redcreator37.playermanagement.DataModels;

import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an in-game company
 */
public class Company {

    /**
     * Unique database id
     */
    private final int id;

    /**
     * Company name
     */
    private final String name;

    /**
     * Company description
     */
    private String description;

    /**
     * Money the company has assigned to it
     */
    private BigDecimal balance;

    /**
     * The number of employees
     */
    private int employees;

    /**
     * The username/uuid tag of the company owner
     */
    private PlayerTag owner;

    /**
     * The date of company establishment
     */
    private final String established;

    /**
     * The amount of money the players can earn
     */
    private BigDecimal wage;

    /**
     * Company constructor - <strong>use this one only when
     * establishing a new company!</strong>
     *
     * @param id   the database id
     * @param name the name
     */
    public Company(int id, String name) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.balance = new BigDecimal(0);
        this.wage = new BigDecimal(10);
        this.employees = 0;
        this.owner = new PlayerTag("N/A", "N/A");
        this.established = PlayerRoutines
                .getCurrentDate(PlayerManagement.prefs.dateFormat);
    }

    /**
     * Company constructor
     *
     * @param id          the database id
     * @param name        the name
     * @param description the description
     * @param balance     the amount of money the company has associated
     *                    with it
     * @param employees   the number of employees
     * @param owner       the owner player
     * @param established the date of establishment
     * @param wage        the amount of money the players can earn
     */
    public Company(int id, String name, String description, String balance,
                   int employees, PlayerTag owner, String established, String wage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = new BigDecimal(balance);
        this.employees = employees;
        this.owner = owner;
        this.established = established;
        this.wage = new BigDecimal(wage);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public int getEmployees() {
        return employees;
    }

    public PlayerTag getOwner() {
        return owner;
    }

    public String getEstablishedDate() {
        return established;
    }

    public BigDecimal getWage() {
        return wage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBalance(BigDecimal money) {
        this.balance = money;
    }

    public void setEmployees(int employees) {
        this.employees = employees;
    }

    public void setOwner(PlayerTag owner) {
        this.owner = owner;
    }

    /**
     * Sets the wage for this company
     *
     * @param wage the new wage
     * @throws IllegalArgumentException if the wage is negative
     */
    public void setWage(BigDecimal wage) {
        if (wage.intValue() < 0)
            throw new IllegalArgumentException(Localization
                    .lc("wage-cannot-be-negative"));
        this.wage = wage;
    }

    /**
     * Returns the company name in a string
     *
     * @return the company name
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Provides hash code functionality
     *
     * @return the hash code for this Company instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getBalance(),
                getEmployees(), getOwner(), getEstablishedDate(), getWage());
    }

}
