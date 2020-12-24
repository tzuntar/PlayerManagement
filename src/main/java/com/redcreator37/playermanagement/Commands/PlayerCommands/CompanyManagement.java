package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.CommandHelper;
import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.CompanyMenu;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static com.redcreator37.playermanagement.Localization.lc;
import static com.redcreator37.playermanagement.PlayerManagement.companies;
import static com.redcreator37.playermanagement.PlayerManagement.companyDb;
import static com.redcreator37.playermanagement.PlayerManagement.eco;
import static com.redcreator37.playermanagement.PlayerManagement.getPlugin;
import static com.redcreator37.playermanagement.PlayerManagement.players;

/**
 * Opens the in-game company management UI or changes the values
 * manually
 */
public class CompanyManagement extends PlayerCommand {

    public CompanyManagement() {
        super("company", new HashMap<String, Boolean>() {{
            put("info", false);
            put("transactions", false);
        }}, new ArrayList<String>() {{
            add("management.company");
            add("management.admin");
        }});
    }

    /**
     * Runs this command and performs the actions
     *
     * @param player the {@link Player} who ran the command
     * @param args   the arguments entered by the player
     */
    @Override
    public void execute(Player player, String[] args) {
        Optional<ServerPlayer> optTarget = getUserOrAdmin(player, args, 0, 0);
        if (!optTarget.isPresent()) return;
        ServerPlayer target = optTarget.get();
        String prefix = PlayerManagement.prefs.prefix;

        if (PlayerRoutines.lacksPermission(player, "management.company"))
            return;

        if (args.length == 2 && !args[1].matches("info|transactions")) {
            HashMap<String, Boolean> subArgs = new HashMap<String, Boolean>() {{
                put("name", true);
                put("increase|decrease|deposit|withdraw|setdesc|setowner|transaction|remove", false);
                put("args...", false);
            }};
            player.sendMessage(prefix + CommandHelper.parseCommandUsage(getName(), subArgs));
            return;
        }

        // manage the player's company or someone else's if the
        // player has the admin permissions
        String companyName = player.hasPermission("management.admin") && args.length > 0
                ? args[0] : target.getCompany().getName();
        if (companyName.equals("N/A")) {
            player.sendMessage(prefix + ChatColor.GOLD
                    + lc("player-not-an-owner-of-any-company"));
            return;
        }

        // try to get the company from the database
        Company company = companies.get(companyName);
        if (company == null) {
            player.sendMessage(prefix + ChatColor.GOLD + lc("unknown-company")
                    + ChatColor.GREEN + companyName);
            return;
        }

        // check the ownership
        if (!company.getOwner().getUsername().equals(player.getName())
                && !player.hasPermission("management.admin")) {
            player.sendMessage(prefix + ChatColor.GOLD
                    + lc("you-can-only-manage-your-company"));
            return;
        }

        if (args.length < 2) {  // no arguments, open the menu
            new CompanyMenu(player, company + lc("management"), company);
            return;
        }

        // else process the commands
        BigDecimal amount = null;
        if (args[1].matches("increase|decrease|deposit|withdraw")) {
            amount = PlayerRoutines.getEnteredBigDecimal(player, args[2]);
            if (amount == null) return;
        }

        switch (args[1]) {
            case "info":
                PlayerRoutines.displayCompanyInfo(player, company);
                break;
            case "increase":
                company.setWage(company.getWage().add(amount));
                player.sendMessage(prefix + "§6" + lc("wages-increased-by")
                        + "§a$" + amount + "§6.");
                break;
            case "decrease":
                try {
                    company.setWage(company.getWage().subtract(amount));
                    player.sendMessage(prefix + "§6" + lc("wages-decreased-by")
                            + "§a$" + amount + "§6.");
                } catch (IllegalArgumentException e) {
                    player.sendMessage(prefix + "§6" + lc("wage-cannot-be-negative"));
                }
                break;
            case "deposit":
                eco.withdrawPlayer(player, Objects.requireNonNull(amount)
                        .doubleValue());
                company.setBalance(company.getBalance().add(amount));
                player.sendMessage(prefix + "§a$" + amount + " §6"
                        + lc("has-been-taken-from-your-account"));

                PlayerManagement.transactionDb.addAsync(player, new Transaction(4097,
                        company.getId(), "<-", "Deposit $"
                        + amount, lc("deposit") + " $" + amount
                        + lc("from-the-player") + target, amount));
                break;
            case "withdraw":
                company.setBalance(company.getBalance().subtract(amount));
                eco.depositPlayer(player, Objects.requireNonNull(amount)
                        .doubleValue());
                player.sendMessage(prefix + "§a$" + amount + " §6"
                        + lc("has-been-added-to-your-account"));

                PlayerManagement.transactionDb.addAsync(player, new Transaction(4097,
                        company.getId(), "->", lc("withdraw") + " $"
                        + amount, lc("withdraw") + " $" + amount
                        + lc("to-the-player") + target, amount));
                break;
            case "setdesc":
                company.setDescription(CommandHelper.getFullEntry(args, 2));
                player.sendMessage(prefix + "§6" + lc("description-set"));
                break;
            case "setowner":
                ServerPlayer newOwner = players.get(PlayerRoutines
                        .uuidFromUsername(players, args[2]));
                if (PlayerRoutines.checkPlayerNonExistent(player, newOwner, args[2]))
                    return;

                company.setOwner(new PlayerTag(newOwner.getUsername(), newOwner.getUuid()));
                player.sendMessage(prefix + "§6" + lc("ownership-changed-to") + " §a"
                        + newOwner.getUsername());
                break;
            case "transactions":
                Transaction.listTransactions(player, company);
                break;
            case "remove":
                try {
                    companyDb.remove(company);
                    companies = companyDb.getAll();
                    player.sendMessage(prefix + "§" + lc("removed-company")
                            + " §a" + args[1]);
                } catch (SQLException e) {
                    player.sendMessage(prefix + "§6" + lc("error-removing-company-data")
                            + " §4" + e.getMessage());
                }
            default:
                player.sendMessage(prefix + "§6" + lc("unknown-command")
                        + " §a" + args[1]);
                return;
        }

        if (args[1].matches("info|transactions|remove"))
            return;    // no changes made, don't update the db

        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class), () -> {
            PlayerManagement.companyDb.updateByPlayer(player, company);
            try {
                PlayerManagement.transactions = PlayerManagement
                        .transactionDb.getAll();
            } catch (SQLException e) {
                player.sendMessage(prefix + ChatColor.GOLD
                        + lc("error-saving-transaction-data")
                        + ChatColor.RED + e.getMessage());
            }
        });
    }
}
