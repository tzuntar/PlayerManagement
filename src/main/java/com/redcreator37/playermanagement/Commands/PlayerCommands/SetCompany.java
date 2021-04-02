package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.EconomyProvider;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Sets the player's company
 */
public class SetCompany extends PlayerCommand {

    public SetCompany() {
        super("setcompany", new HashMap<String, Boolean>() {{
            put("company_name", true);
            put("player_name", false);
        }}, new ArrayList<String>() {{
            add("management.user");
            add("management.admin");
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
        Optional<ServerPlayer> optTarget = getUserOrAdmin(player, args, 1, 1);
        if (!optTarget.isPresent()) return;
        ServerPlayer target = optTarget.get();

        Company newCompany = PlayerManagement.companies.get(args[0]);
        if (newCompany == null) {
            player.sendMessage(PlayerManagement.prefs.prefix
                    + MessageFormat.format(Localization.lc("unknown-company"), args[0]));
            return;
        }

        if (!newCompany.getOwner().getUsername().equals(target.getUsername()) &&
                !player.hasPermission("management.company.employ")) {
            player.sendMessage(PlayerManagement.prefs.prefix
                    + Localization.lc("cant-employ-yourself"));
            return;
        }

        target.setCompany(newCompany);
        newCompany.setEmployees(newCompany.getEmployees() + 1);
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {   // set the job and update the player list
                PlayerManagement.players.updatePlayerEntry(target);
                player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat
                        .format(Localization.lc("now-employed-at"), target, args[0]));

                // decrease the employee count when switching to a different company
                Company prevCompany = target.getCompany();
                if (!EconomyProvider.isPlayerUnemployed(target))
                    prevCompany.setEmployees(prevCompany.getEmployees() - 1);

                PlayerManagement.companyDb.update(newCompany);
                PlayerManagement.companyDb.update(prevCompany);
                PlayerManagement.companies = PlayerManagement.companyDb.getAll();
            } catch (SQLException e) {
                player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat
                        .format(Localization.lc("error-updating-playerdata"), e.getMessage()));
            }
        });
    }
}
