package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.CommandHelper;
import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerCard;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Registers a new player to the database
 */
public class RegisterId extends PlayerCommand {

    public RegisterId() {
        super("registerid", new HashMap<String, Boolean>() {{
            put("Real Name", true);
        }}, new ArrayList<String>() {{
            add("management.user");
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
        ServerPlayer test = PlayerManagement.players.get(player.getUniqueId().toString());
        if (test != null) {
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + Localization.lc("already-registered"));
            return;
        }

        // their inventory should not be full because we're going
        // to give them an ID card after registration
        if (PlayerRoutines.checkInventoryFull(player)) return;
        ServerPlayer target = new ServerPlayer(4097,    // register with a dummy id
                new PlayerTag(player.getName(), player.getUniqueId().toString()));

        target.setName(CommandHelper.getFullEntry(args, 0));
        target.setJoinDate(PlayerRoutines
                .getCurrentDate(PlayerManagement.dateFormat));
        target.setJob(PlayerManagement.jobs.get("N/A"));
        target.setCompany(PlayerManagement.companies.get("N/A"));
        target.setNotes("");

        try {
            PlayerManagement.playerDb.insert(target);
            // reload from the database
            PlayerManagement.players = PlayerManagement.playerDb.getNewlyRegistered();

            ServerPlayer registeredPlayer = PlayerManagement.players
                    .get(player.getUniqueId().toString());
            if (registeredPlayer == null) {
                player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + Localization.lc("failed-to-give-id"));
                return;
            }

            PlayerCard.giveNewCard(player, registeredPlayer);
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + Localization.lc("registration-successful"));
        } catch (SQLException e) {
            player.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + Localization.lc("error-updating-playerdata")
                    + ChatColor.RED + e.getMessage());
        }
    }
}
