package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
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
public class CompanyPay extends PlayerCommand {

    public CompanyPay() {
        super("cpay", new HashMap<String, Boolean>() {{
            put("from", true);
            put("to", true);
            put("amount", true);
        }}, new ArrayList<String>() {{
            add("management.company");
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
        ServerPlayer serverPlayer = players.get(player.getUniqueId().toString());
        if (PlayerRoutines.checkPlayerNonExistent(player, serverPlayer, player.getName()))
            return;

        // attempt to look up both companies
        Company source = companies.get(args[0]),
                target = companies.get(args[1]);
        if (source == null || target == null) {
            player.sendMessage(prefix + ChatColor.GOLD + lc("unknown-company"));
            return;
        }

        // check the ownership
        if (!source.getOwner().getUsername().equals(player.getName()) && !player
                .hasPermission("management.admin")) {
            player.sendMessage(prefix + ChatColor.GOLD
                    + lc("you-can-only-manage-your-company"));
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + ChatColor.GOLD + lc("invalid-number")
                    + ChatColor.GREEN + args[2]);
            return;
        }

        String formattedAmount = PlayerRoutines.formatDecimal(amount);
        if (source.getBalance().doubleValue() < amount.doubleValue()) {
            player.sendMessage(prefix + ChatColor.GOLD
                    + lc("the-company") + ChatColor.GREEN + source
                    + ChatColor.GOLD + lc("cant-afford-to-pay")
                    + ChatColor.GREEN + formattedAmount + ChatColor.GOLD + "!");
            return;
        }

        // withdraw the amount from the source
        source.setBalance(source.getBalance().subtract(amount));
        transactionDb.addAsync(player, new Transaction(4097,
                source.getId(), "->", lc("pay")
                + formattedAmount, lc("pay") + formattedAmount
                + lc("to") + target, amount));

        // add to the target
        target.setBalance(target.getBalance().add(amount));
        transactionDb.addAsync(player, new Transaction(4097,
                target.getId(), "<-", lc("receive")
                + formattedAmount, lc("receive") + formattedAmount
                + lc("from") + source, amount));

        // update and re-read the data
        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class), () -> {
            companyDb.updateByPlayer(player, source);
            companyDb.updateByPlayer(player, target);
            try {
                transactions = transactionDb.getAll();
            } catch (SQLException e) {
                player.sendMessage(prefix + ChatColor.GOLD
                        + lc("error-saving-transaction-data")
                        + ChatColor.RED + e.getMessage());
            }
        });
    }
}
