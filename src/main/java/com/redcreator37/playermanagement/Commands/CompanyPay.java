package com.redcreator37.playermanagement.Commands;

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

import static com.redcreator37.playermanagement.PlayerManagement.companies;
import static com.redcreator37.playermanagement.PlayerManagement.getPlugin;
import static com.redcreator37.playermanagement.PlayerManagement.players;
import static com.redcreator37.playermanagement.PlayerManagement.prefix;

/**
 * A simple /pay command for companies
 */
public class CompanyPay implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null) return true;

        if (!PlayerRoutines.checkPlayerPermission(p, "management.company"))
            return true;

        if (args.length < 3) {
            p.sendMessage(prefix + CommandHelper.parseCommandUsage("cpay",
                    new String[]{"from", "to", "amount"}));
            return true;
        }

        ServerPlayer serverPlayer = PlayerRoutines
                .playerFromUsername(players, p.getName());
        if (PlayerRoutines.checkPlayerNonExistent(p, serverPlayer, p.getName()))
            return true;

        // attempt to look up both companies
        Company source = companies.get(args[0]),
                target = companies.get(args[1]);
        if (source == null || target == null) {
            p.sendMessage(prefix + ChatColor.GOLD + "Unknown company name!");
            return true;
        }

        // check the ownership
        if (!source.getOwner().equals(p.getName()) && !p
                .hasPermission("management.admin")) {
            p.sendMessage(prefix + ChatColor.GOLD
                    + "You can only manage your own company!");
            return true;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(args[2]);
        } catch (NumberFormatException e) {
            p.sendMessage(prefix + ChatColor.GOLD
                    + "Invalid number: " + ChatColor.GREEN + args[2]);
            return true;
        }

        String formattedAmount = PlayerRoutines.formatDecimal(amount);
        if (source.getBalance().doubleValue() < amount.doubleValue()) {
            p.sendMessage(prefix + ChatColor.GOLD
                    + "The company " + ChatColor.GREEN + source + ChatColor.GOLD
                    + " can't afford to pay " + ChatColor.GREEN + formattedAmount
                    + ChatColor.GOLD + "!");
            return true;
        }

        // withdraw the amount from the source
        source.setBalance(source.getBalance().subtract(amount));
        PlayerManagement.transactionDb.addAsync(p, new Transaction(4097,
                source.getId(), "->", "Pay " + formattedAmount,
                "Pay " + formattedAmount + " to " + target, amount));

        // add to the target
        target.setBalance(target.getBalance().add(amount));
        PlayerManagement.transactionDb.addAsync(p, new Transaction(4097,
                target.getId(), "<-", "Receive " + formattedAmount,
                "Receive " + formattedAmount + " from " + source, amount));

        // update and re-read the data
        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class), () -> {
            PlayerManagement.companyDb.updateByPlayer(p, source);
            PlayerManagement.companyDb.updateByPlayer(p, target);
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
