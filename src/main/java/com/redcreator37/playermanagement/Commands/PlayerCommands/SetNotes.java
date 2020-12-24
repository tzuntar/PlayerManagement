package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.CommandHelper;
import com.redcreator37.playermanagement.Commands.PlayerCommand;
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

/**
 * Sets the player's notes
 */
public class SetNotes extends PlayerCommand {

    public SetNotes() {
        super("setnotes", new HashMap<String, Boolean>() {{
            put("player_name", true);
            put("Any notes...", false);
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
        if (PlayerRoutines.checkPlayerNonExistent(player, target, player.getName()))
            return;

        target.setNotes(CommandHelper.getFullEntry(args, 1));
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {   // set the notes and update the player list
                PlayerManagement.playerDb.update(target);
                PlayerManagement.players = PlayerManagement.playerDb.getAll();
                player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + Localization.lc("notes-updated"));
            } catch (SQLException e) {
                player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + Localization.lc("error-updating-playerdata")
                        + ChatColor.RED + e.getMessage());
            }
        });
    }
}
