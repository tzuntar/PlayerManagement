package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.redcreator37.playermanagement.Localization.lc;
import static com.redcreator37.playermanagement.PlayerManagement.companies;
import static com.redcreator37.playermanagement.PlayerManagement.companyDb;
import static com.redcreator37.playermanagement.PlayerManagement.getPlugin;
import static com.redcreator37.playermanagement.PlayerManagement.players;
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
     * @param player   the {@link Player} who ran the command
     * @param args     the arguments entered by the player
     * @param executor the UUID of the executing player
     */
    @Override
    public void execute(Player player, String[] args, UUID executor) {
        String prefix = PlayerManagement.prefs.prefix;
        ServerPlayer serverPlayer = players.byUuid(player.getUniqueId());
        if (PlayerRoutines.checkPlayerNonExistent(player, serverPlayer, player.getName()))
            return;

        // attempt to look up both companies
        Company source = companies.get(args[0]),
                target = companies.get(args[1]);
        if (source == null || target == null) {
            player.sendMessage(prefix + MessageFormat.format(lc("unknown-company"),
                    source == null ? args[0] : args[1]));
            return;
        }

        // check the ownership
        if (!source.getOwner().getUsername().equals(player.getName()) && !player
                .hasPermission("management.admin")) {
            player.sendMessage(prefix + lc("you-can-only-manage-your-company"));
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + MessageFormat.format(lc("invalid-number"), args[2]));
            return;
        }

        String formattedAmount = PlayerRoutines.formatDecimal(amount);
        if (source.getBalance().doubleValue() < amount.doubleValue()) {
            player.sendMessage(prefix + MessageFormat.format(lc("company-cannot-afford-to-pay"),
                    source, formattedAmount));
            return;
        }

        // withdraw the amount from the source
        source.setBalance(source.getBalance().subtract(amount));
        transactionDb.addAsync(player, new Transaction(4097,
                source.getId(), "->",
                MessageFormat.format(lc("pay-amount"), formattedAmount),
                MessageFormat.format(lc("pay-company"), formattedAmount, target), amount));

        // add to the target
        target.setBalance(target.getBalance().add(amount));
        transactionDb.addAsync(player, new Transaction(4097,
                target.getId(), "<-",
                MessageFormat.format(lc("receive-amount"), formattedAmount),
                MessageFormat.format(lc("receive-company"), formattedAmount, target), amount));

        // update and re-read the data
        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class), () -> {
            companyDb.updateByPlayer(player, source);
            companyDb.updateByPlayer(player, target);
            try {
                transactions = transactionDb.getAll();
            } catch (SQLException e) {
                player.sendMessage(prefix + MessageFormat.format(lc("error-saving-transaction-data"),
                        e.getMessage()));
            }
        });
    }
}
