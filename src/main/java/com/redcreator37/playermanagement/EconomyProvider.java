package com.redcreator37.playermanagement;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static com.redcreator37.playermanagement.Localization.lc;

/**
 * Provides in-game economy-related features
 */
public class EconomyProvider {

    /**
     * The currently loaded Vault plugin object
     */
    private final Economy eco;

    /**
     * The currently loaded Essentials plugin object
     */
    private final Essentials ess;

    /**
     * The currently set minimal wage
     */
    private final MinimalWage minimalWage;

    /**
     * Represents an in-game minimal wage
     */
    public static class MinimalWage {

        /**
         * The amount of money paid as the minimal wage
         */
        private final double amount;

        /**
         * The balance threshold after which a player will no longer
         * be eligible to receive the minimal wage
         */
        private final double threshold;

        /**
         * Constructs a new MinimalWage objects
         *
         * @param amount    the amount of money paid as the minimal wage
         * @param threshold the balance threshold after which a player
         *                  will no longer be eligible to receive the
         *                  minimal wage
         */
        public MinimalWage(double amount, double threshold) {
            this.amount = amount;
            this.threshold = threshold;
        }

        /**
         * Calculates the minimal wage for this balance
         *
         * @param balance the player's current balance
         * @return the calculated minimal wage, or 0 if the threshold
         * was already reached
         */
        private double calculateMinimalWage(double balance) {
            assert threshold != 0;
            double d = balance / threshold;
            if (d > 1) return 0;
            return amount * (1 - d);
        }

        public double getAmount() {
            return amount;
        }

        public double getThreshold() {
            return threshold;
        }
    }

    /**
     * Constructs a new EconomyProvider object
     *
     * @param eco         the currently used Vault plugin object
     * @param ess         the currently used Essentials player object
     * @param minimalWage the currently set minimal wage
     */
    public EconomyProvider(Economy eco, Essentials ess, MinimalWage minimalWage) {
        this.eco = eco;
        this.ess = ess;
        this.minimalWage = minimalWage;
    }

    /**
     * Pays the wage to this player
     *
     * @param player the player to which the wage will be paid to
     */
    public void payWage(Player player) {
        ServerPlayer target = PlayerManagement.players.get(player
                .getUniqueId().toString());
        if (target == null || Objects.requireNonNull(ess)
                .getUser(player).isAfk()) return;   // unknown player or AFK

        double amount;
        if (isPlayerUnemployed(target)) {
            amount = minimalWage.calculateMinimalWage(eco.getBalance(player));
        } else if (!isPlayerUnemployed(target)) {
            Company targetCompany = target.getCompany();
            BigDecimal wage = targetCompany.getWage();
            if (targetCompany.getBalance().doubleValue() < wage.doubleValue()) {
                // get the owner player handle
                User owner = ess.getOfflineUser(targetCompany.getOwner().getUuid());

                // get the OfflinePlayer object from the UUID
                OfflinePlayer ownerPl;
                try {
                    ownerPl = Bukkit.getOfflinePlayer(UUID
                            .fromString(targetCompany.getOwner().getUuid()));
                } catch (NullPointerException e) {
                    player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.RED
                            + lc("db-company-invalid"));
                    return; // failsafe in case an invalid player is specified in the db
                }

                if (owner.canAfford(wage)) {
                    eco.withdrawPlayer(ownerPl, wage.doubleValue());
                    owner.addMail(lc("money-taken-to-pay-wages"));
                } else {
                    player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GREEN
                            + targetCompany + ChatColor.GOLD
                            + lc("cant-afford-to-pay-your-wage"));
                    owner.addMail(lc("unable-to-pay-wage-for-player")
                            + player.getName() + "!");
                    return;
                }
            } else {
                Company company = PlayerManagement.companies.get(targetCompany.getName());
                if (company == null) {
                    player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                            + lc("unknown-company")
                            + ChatColor.GREEN + targetCompany);
                    return;
                }

                // update the database Company object
                PlayerManagement.companies.get(company.getName()).setBalance(company
                        .getBalance().subtract(wage));
            }
            amount = wage.doubleValue();
        } else return;
        eco.depositPlayer(player, amount);

        if ((int) amount < 1) return;    // don't display on small / negative values
        player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GREEN
                + "$" + amount + ChatColor.GOLD
                + lc("has-been-added-to-your-account"));
    }

    /**
     * Checks if this player is unemployed
     *
     * @param player the player to check
     * @return true if the player is found to be unemployed (ie. the
     * company's name is the same as the placeholder value) or false
     * otherwise
     */
    public static boolean isPlayerUnemployed(ServerPlayer player) {
        return player.getCompany().getName().equals("N/A");
    }

}
