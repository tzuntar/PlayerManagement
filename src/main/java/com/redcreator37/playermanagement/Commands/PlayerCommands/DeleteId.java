package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
     * @param player   the {@link Player} who ran the command
     * @param args     the arguments entered by the player
     * @param executor the UUID of the executing player
     */
    @Override
    public void execute(Player player, String[] args, UUID executor) {
        String prefix = PlayerManagement.prefs.prefix;
        ServerPlayer target = PlayerManagement.players.byUsername(args[0]);
        if (PlayerRoutines.checkPlayerNonExistent(player, target, args[0]))
            return;

        try {
            Map<String, Company> ownedCompanies = PlayerManagement.companyDb
                    .getCompaniesByOwner(target.getUuid());
            if (ownedCompanies.size() > 0) {
                player.sendMessage(prefix + Localization.lc("cant-unregister-still-owns-companies"));
                return;
            }
        } catch (SQLException e) {
            player.sendMessage(prefix + MessageFormat.format(Localization
                    .lc("error-retrieving-playerdata-from-db"), e.getMessage()));
        }

        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                PlayerManagement.playerDb.remove(target);
                PlayerManagement.players.removeByUuid(target.getUuid());
                player.sendMessage(prefix + MessageFormat.format(Localization
                        .lc("player-unregistered"), target));
            } catch (SQLException e) {
                player.sendMessage(prefix + MessageFormat.format(Localization
                        .lc("error-removing-playerdata"), e.getMessage()));
            }
        });
    }
}
