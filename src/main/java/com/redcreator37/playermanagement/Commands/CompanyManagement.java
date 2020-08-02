package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.CompanyMenu;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;

import static com.redcreator37.playermanagement.PlayerManagement.companies;
import static com.redcreator37.playermanagement.PlayerManagement.eco;
import static com.redcreator37.playermanagement.PlayerManagement.getPlugin;
import static com.redcreator37.playermanagement.PlayerManagement.players;
import static com.redcreator37.playermanagement.PlayerManagement.prefix;

/**
 * Opens the in-game company management UI or changes the values
 * manually
 */
public class CompanyManagement implements CommandExecutor {

    /**
     * Main command process
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = PlayerRoutines.getPlayerFromSender(sender);
        if (p == null) return true;

        if (!PlayerRoutines.checkPlayerPermissions(p, "management.company"))
            return true;

        if (args.length == 2 && !args[1].matches("info|transactions")) {
            p.sendMessage(prefix + ChatColor.GOLD + "Usage: "
                    + ChatColor.GREEN + "/company [name] [info|increase|decrease" +
                    "deposit|withdraw|setdesc|setowner|transactions] [args]");
            return true;
        }

        ServerPlayer target = PlayerRoutines
                .getPlayerFromUsername(players, p.getName());
        if (PlayerRoutines.checkPlayerNonExistent(p, target, p.getName()))
            return true;

        // manage the player's company or someone else's if the
        // player has the admin permissions
        String companyName = p.hasPermission("management.admin") && args.length > 0
                ? args[0] : target.getCompany().getName();
        if (companyName.equals("N/A")) {
            p.sendMessage(prefix + ChatColor.GOLD
                    + "The player is not the owner of any company");
            return true;
        }

        // try to get the company from the database
        Company company = companies.get(companyName);
        if (company == null) {
            p.sendMessage(prefix + ChatColor.GOLD
                    + "Unknown company: " + ChatColor.GREEN + companyName);
            return true;
        }

        // check the ownership
        if (!company.getOwner().equals(p.getName()) && !p
                .hasPermission("management.admin")) {
            p.sendMessage(prefix + ChatColor.GOLD
                    + "You can only manage your own company!");
            return true;
        }

        if (args.length < 2) {  // no arguments, open the menu
            new CompanyMenu(p, company + " Management", company);
            return true;
        }

        // else process the commands
        BigDecimal amount = null;
        if (args[1].matches("increase|decrease|deposit|withdraw")) {
            amount = PlayerRoutines.getEnteredBigDecimal(p, args[2]);
            if (amount == null) return true;
        }

        switch (args[1]) {
            case "info":
                PlayerRoutines.displayCompanyInfo(p, company);
                break;
            case "increase":
                company.setPaycheck(company.getPaycheck().add(amount));
                p.sendMessage(prefix + "§6The salary has been increased by §a$"
                        + amount + "§6.");
                break;
            case "decrease":
                company.setPaycheck(company.getPaycheck().subtract(amount));
                p.sendMessage(prefix + "§6The salary has been decreased by §a$"
                        + amount + "§6.");
                break;
            case "deposit":
                eco.withdrawPlayer(p, amount.doubleValue());
                company.setBalance(company.getBalance().add(amount));
                p.sendMessage(prefix + "§a$" + amount
                        + " §6has been taken from your account.");

                PlayerManagement.transactionDb.addAsync(p, new Transaction(4097,
                        company.getId(), "<-", "Deposit $"
                        + amount, "Deposit $" + amount
                        + " from the player " + target, amount));
                break;
            case "withdraw":
                company.setBalance(company.getBalance().subtract(amount));
                eco.depositPlayer(p, amount.doubleValue());
                p.sendMessage(prefix + "§a$" + amount
                        + " §6has been added to your account.");

                PlayerManagement.transactionDb.addAsync(p, new Transaction(4097,
                        company.getId(), "->", "Withdraw $"
                        + amount, "Withdraw $" + amount
                        + " to the player " + target, amount));
                break;
            case "setdesc":
                StringBuilder desc = new StringBuilder();
                for (String arg : Arrays.copyOfRange(args, 2, args.length))
                    desc.append(arg).append(" ");
                company.setDescription(desc.toString());
                p.sendMessage(prefix + "§6Description set.");
                break;
            case "setowner":
                ServerPlayer newOwner = PlayerRoutines
                        .getPlayerFromUsername(players, args[2]);
                if (PlayerRoutines.checkPlayerNonExistent(p, newOwner, args[2]))
                    return true;

                company.setOwner(newOwner.getUsername());
                p.sendMessage(prefix + "§6The ownership has been transferred to §a"
                        + newOwner.getUsername());
                break;
            case "transactions":
                Transaction.listTransactions(p, company);
                break;
            default:
                p.sendMessage(prefix + "§6Unknown command: §a" + args[1]);
                return true;
        }

        if (args[1].matches("info|transactions"))
            return true;    // no changes made, don't update the db

        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class), () -> {
            PlayerManagement.companyDb.updateByPlayer(p, company);
            try {
                PlayerManagement.transactions = PlayerManagement.transactionDb.getAll();
            } catch (SQLException e) {
                p.sendMessage(prefix + ChatColor.GOLD
                        + "Error while saving transaction data: "
                        + ChatColor.RED + e.getMessage());
            }
        });
        return true;
    }

}
