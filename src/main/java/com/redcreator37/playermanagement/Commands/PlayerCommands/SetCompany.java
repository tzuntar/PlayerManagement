package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Sets the player's company
 */
public class SetCompany extends PlayerCommand {

    public SetCompany() {
        super("setcompany", new LinkedHashMap<String, Boolean>() {{
            put("player_name", true);
            put("company_name", true);
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
        Optional<ServerPlayer> optTarget = getUserOrAdmin(player, args, 0, 1);
        if (!optTarget.isPresent()) return;
        ServerPlayer target = optTarget.get();

        Company newCompany = PlayerManagement.companies.byName(args[1]);
        if (newCompany == null) {
            player.sendMessage(PlayerManagement.prefs.prefix
                    + MessageFormat.format(Localization.lc("unknown-company"), args[1]));
            return;
        }

        Optional<PlayerTag> newOwnerTag = newCompany.getOwner();
        if ((!newOwnerTag.isPresent() || !newOwnerTag.get().getUsername().equals(target.getUsername()))
                && !player.hasPermission("management.company.employ")) {
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
                        .format(Localization.lc("now-employed-at"), target, args[1]));

                // decrease the employee count when switching to a different company
                Optional<Company> prevCompany = target.getCompany();
                if (prevCompany.isPresent()) {
                    prevCompany.get().setEmployees(prevCompany.get().getEmployees() - 1);
                    PlayerManagement.companyDb.update(prevCompany.get());
                }

                PlayerManagement.companies.updateCompanyEntry(newCompany);
            } catch (SQLException e) {
                player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat
                        .format(Localization.lc("error-updating-playerdata"), e.getMessage()));
            }
        });
    }
}
