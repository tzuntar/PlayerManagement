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

import static com.redcreator37.playermanagement.Localization.lc;
import static com.redcreator37.playermanagement.PlayerManagement.companies;
import static com.redcreator37.playermanagement.PlayerManagement.companyDb;
import static com.redcreator37.playermanagement.PlayerManagement.getPlugin;
import static com.redcreator37.playermanagement.PlayerManagement.players;
import static com.redcreator37.playermanagement.PlayerManagement.prefix;
import static com.redcreator37.playermanagement.PlayerManagement.transactionDb;
import static com.redcreator37.playermanagement.PlayerManagement.transactions;

/**
 * A simple /pay command for companies
 */
public class CompanyPay implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null) return true;

        if (!PlayerRoutines.checkPermission(p, "management.company"))
            return true;

        if (args.length < 3) {
            p.sendMessage(prefix + CommandHelper.parseCommandUsage("cpay",
                    new String[]{"from", "to", "amount"}));
            return true;
        }

        ServerPlayer serverPlayer = players.get(p.getUniqueId().toString());
        if (PlayerRoutines.checkPlayerNonExistent(p, serverPlayer, p.getName()))
            return true;

        // attempt to look up both companies
        Company source = companies.get(args[0]),
                target = companies.get(args[1]);
        if (source == null || target == null) {
            p.sendMessage(prefix + ChatColor.GOLD + lc("unknown-company"));
            return true;
        }

        // check the ownership
        if (!source.getOwner().getUsername().equals(p.getName()) && !p
                .hasPermission("management.admin")) {
            p.sendMessage(prefix + ChatColor.GOLD
                    + lc("you-can-only-manage-your-company"));
            return true;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(args[2]);
        } catch (NumberFormatException e) {
            p.sendMessage(prefix + ChatColor.GOLD + lc("invalid-number")
                    + ChatColor.GREEN + args[2]);
            return true;
        }

        String formattedAmount = PlayerRoutines.formatDecimal(amount);
        if (source.getBalance().doubleValue() < amount.doubleValue()) {
            p.sendMessage(prefix + ChatColor.GOLD
                    + lc("the-company") + ChatColor.GREEN + source
                    + ChatColor.GOLD + lc("cant-afford-to-pay")
                    + ChatColor.GREEN + formattedAmount + ChatColor.GOLD + "!");
            return true;
        }

        // withdraw the amount from the source
        source.setBalance(source.getBalance().subtract(amount));
        transactionDb.addAsync(p, new Transaction(4097,
                source.getId(), "->", lc("pay")
                + formattedAmount, lc("pay") + formattedAmount
                + lc("to") + target, amount));

        // add to the target
        target.setBalance(target.getBalance().add(amount));
        transactionDb.addAsync(p, new Transaction(4097,
                target.getId(), "<-", lc("receive")
                + formattedAmount, lc("receive") + formattedAmount
                + lc("from") + source, amount));

        // update and re-read the data
        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class), () -> {
            companyDb.updateByPlayer(p, source);
            companyDb.updateByPlayer(p, target);
            try {
                transactions = transactionDb.getAll();
            } catch (SQLException e) {
                p.sendMessage(prefix + ChatColor.GOLD
                        + lc("error-saving-transaction-data")
                        + ChatColor.RED + e.getMessage());
            }
        });
        return true;
    }

}
