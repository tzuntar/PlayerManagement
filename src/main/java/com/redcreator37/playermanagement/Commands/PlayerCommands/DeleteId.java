package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Unregisters the player from the database
 */
public class DeleteId extends PlayerCommand {

    public DeleteId() {
        super("deleteid", new HashMap<String, Boolean>() {{
            put("player_name", true);
        }}, new ArrayList<String>() {{
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
        ServerPlayer target = PlayerManagement.players.get(PlayerRoutines
                .uuidFromUsername(PlayerManagement.players, args[0]));
        if (PlayerRoutines.checkPlayerNonExistent(player, target, args[0]))
            return;

        try {
            Map<String, Company> ownedCompanies = PlayerManagement.companyDb
                    .getCompaniesByOwner(target.getUuid());
            if (ownedCompanies.size() > 0) {
                player.sendMessage(PlayerManagement.prefix + ChatColor.RED
                        + Localization.lc("cant-unregister-still-owns-companies"));
                return;
            }
        } catch (SQLException e) {
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + Localization.lc("error-retrieving-playerdata-from-db")
                    + ChatColor.RED + e.getMessage());
        }

        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                PlayerManagement.playerDb.remove(target);
                // reload from the database
                PlayerManagement.players = PlayerManagement.playerDb.getAll();
                player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + Localization.lc("the-player")
                        + ChatColor.GREEN + target.getUsername() + ChatColor.GOLD
                        + Localization.lc("has-been-unregistered"));
            } catch (SQLException e) {
                player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + Localization.lc("error-removing-playerdata")
                        + ChatColor.RED + e.getMessage());
            }
        });
    }
}
