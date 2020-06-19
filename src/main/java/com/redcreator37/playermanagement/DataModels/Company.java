package com.redcreator37.playermanagement.DataModels;

import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;

import java.math.BigDecimal;

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
     * The username of the owner
     */
    private String owner;

    /**
     * The date of company establishment
     */
    private final String established;

    /**
     * The amount of money the players can earn
     */
    private BigDecimal paycheck;

    /**
     * Company constructor - <strong>use this one only when
     * establishing a new company!</strong>
     *
     * @param id    the database id
     * @param name  the name
     * @param owner the username of the owner
     */
    public Company(int id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.owner = owner;
        this.balance = new BigDecimal(0);
        this.paycheck = new BigDecimal(10);
        this.employees = 0;
        this.established = PlayerRoutines
                .getCurrentDate(PlayerManagement.dateFormat);
    }

    /**
     * Company constructor
     *
     * @param id          the database id
     * @param name        the name
     * @param description the description
     * @param balance       the amount of money the company has associated
     *                    with it
     * @param employees   the number of employees
     * @param owner       the username of the owner
     * @param established the date of establishment
     * @param paycheck    the amount of money the players can earn
     */
    public Company(int id, String name, String description, String balance,
                   int employees, String owner, String established, String paycheck) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = new BigDecimal(balance);
        this.employees = employees;
        this.owner = owner;
        this.established = established;
        this.paycheck = new BigDecimal(paycheck);
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

    public String getOwner() {
        return owner;
    }

    public String getEstablishedDate() {
        return established;
    }

    public BigDecimal getPaycheck() {
        return paycheck;
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

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setPaycheck(BigDecimal paycheck) {
        this.paycheck = paycheck;
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

}
