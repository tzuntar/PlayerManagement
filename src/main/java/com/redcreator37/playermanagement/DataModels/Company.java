package com.redcreator37.playermanagement.DataModels;

import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;

import java.math.BigDecimal;
import java.util.Optional;

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
     * The date of company establishment
     */
    private final String established;
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
     * The amount of money the players can earn
     */
    private BigDecimal wage;

    /**
     * The ID to use when establishing a new company it doesn't exist
     * in the database yet.
     */
    public static final int NEW_COMPANY_ID = -1;

    private Company(Builder builder) {
        id = builder.id;
        name = builder.name;
        established = builder.established;
        description = builder.description;
        balance = builder.balance;
        employees = builder.employees;
        owner = builder.owner;
        wage = builder.wage;
    }

    public void setWage(BigDecimal wage) {
        if (this.wage != null && this.wage.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException(Localization.lc("wage-cannot-be-negative"));
        this.wage = wage;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal money) {
        this.balance = money;
    }

    public int getEmployees() {
        return employees;
    }

    public void setEmployees(int employees) {
        this.employees = employees;
    }

    public Optional<PlayerTag> getOwner() {
        return Optional.ofNullable(owner);
    }

    public void setOwner(PlayerTag owner) {
        this.owner = owner;
    }

    public String getEstablishedDate() {
        return established;
    }

    public BigDecimal getWage() {
        return wage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Company))
            return false;
        Company c = (Company) obj;
        return c.name.equals(this.name)
                && c.description.equals(this.description)
                && c.balance.equals(this.balance)
                && c.employees == this.employees
                && c.owner.equals(this.owner)
                && c.established.equals(this.established)
                && c.wage.equals(this.wage);
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

    @Override
    public int hashCode() {
        int c = Integer.hashCode(this.id);
        c = 31 * c + this.name.hashCode();
        c = 31 * c + this.description.hashCode();
        c = 31 * c + this.balance.hashCode();
        c = 31 * c + Integer.hashCode(this.employees);
        c = 31 * c + this.owner.hashCode();
        c = 31 * c + this.established.hashCode();
        c = 31 * c + this.wage.hashCode();
        return c;
    }

    /**
     * Handles the construction of new {@link Company} objects.
     */
    public static class Builder {

        private final int id;

        private final String name;

        private String established;

        private String description;

        private BigDecimal balance;

        private int employees;

        private PlayerTag owner;

        private BigDecimal wage;

        /**
         * Construct a new {@link Company} builder.
         * <p>
         * <strong>This constructor sets up the builder with default
         * parameters for establishing a new company.</strong>
         *
         * @param id   the company's database ID.
         * @param name the company's name.
         */
        public Builder(int id, String name) {
            this.id = id;
            this.name = name;
            this.established = PlayerRoutines
                    .getCurrentDate(PlayerManagement.prefs.dateFormat);
            this.balance = new BigDecimal(0);
            this.wage = new BigDecimal(10);
            this.employees = 0;
        }

        /**
         * Constructs a new {@link Company} builder.
         *
         * @param id                the company's database ID.
         * @param name              the company's name.
         * @param establishmentDate the company's establishment date.
         */
        public Builder(int id, String name, String establishmentDate) {
            this.id = id;
            this.name = name;
            this.established = establishmentDate;
        }

        public Builder withEstablishmentDate(String establishmentDate) {
            this.established = establishmentDate;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder withEmployees(int employeeCount) {
            this.employees = employeeCount;
            return this;
        }

        public Builder withOwner(PlayerTag owner) {
            this.owner = owner;
            return this;
        }

        public Builder withWage(BigDecimal wage) {
            if (this.wage != null && this.wage.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException(Localization.lc("wage-cannot-be-negative"));
            this.wage = wage;
            return this;
        }

        /**
         * Finishes and builds the {@link Company} object.
         */
        public Company build() {
            return new Company(this);
        }

    }

}
