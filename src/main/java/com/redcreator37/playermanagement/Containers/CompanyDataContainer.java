package com.redcreator37.playermanagement.Containers;

import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

/**
 * Contains containerized global server company data
 */
public class CompanyDataContainer {

    /**
     * Contains the data for all companies on the server
     * The key is the company name, the value is the matching
     * Company object
     */
    private final Map<String, Company> companies;

    /**
     * Constructs a new CompanyDataContainer object
     *
     * @param companies a filled company map
     */
    public CompanyDataContainer(Map<String, Company> companies) {
        this.companies = companies;
    }

    /**
     * Updates this company in both the internal data set and
     * the database
     *
     * @param company the {@link Company} object to update
     * @throws SQLException on errors
     */
    public void updateCompanyEntry(Company company) throws SQLException {
        Company updated = PlayerManagement.companyDb.updateAndGet(company);
        this.companies.remove(company.toString());
        this.companies.put(updated.toString(), updated);
    }

    /**
     * Checks whether the company with this name doesn't exist
     *
     * @param name the company name to look for
     * @return {@code true} if the company doesn't exist, {@code false}
     * otherwise
     */
    public boolean doesNotExist(String name) {
        return companies.get(name) == null;
    }

    public Company byName(String name) {
        return companies.get(name);
    }

    /**
     * Inserts this company into both the data set and the database
     *
     * @param company the {@link Company} to insert
     * @throws SQLException on errors
     */
    public void setByName(Company company) throws SQLException {
        PlayerManagement.companyDb.insert(company);
        companies.put(company.toString(), company);
    }

    /**
     * Updates the data for this company in the database
     * (a convenience method which emits error messages)
     *
     * @param player  the player who will get any possible output
     * @param company the company to update
     */
    public void updateByPlayer(Player player, Company company) {
        try {
            updateCompanyEntry(company);
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + Localization.lc("company-data-saved"));
        } catch (SQLException ex) {
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + Localization.lc("error-saving-company-data")
                    + ChatColor.RED + ex.getMessage());
        }
    }

    /**
     * Removes this company from the database and the data set
     *
     * @param company the {@link Company} to remove
     * @throws SQLException on errors
     */
    public void remove(Company company) throws SQLException {
        PlayerManagement.companyDb.remove(company);
        companies.remove(company.toString());
    }

    public Map<String, Company> getCompanies() {
        return companies;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(companies);
    }
}
