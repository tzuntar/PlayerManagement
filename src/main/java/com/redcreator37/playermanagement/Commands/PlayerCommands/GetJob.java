package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Displays the specified player's job
 */
public class GetJob extends PlayerCommand {

    public GetJob() {
        super("getjob", new HashMap<String, Boolean>() {{
            put("player_name", true);
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
        ServerPlayer target = PlayerManagement.players.get(PlayerRoutines
                .uuidFromUsername(PlayerManagement.players, args[0]));
        if (PlayerRoutines.checkPlayerNonExistent(player, target, args[0])) return;
        player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GREEN + target
                + ChatColor.GOLD + Localization.lc("is-employed-as")
                + ChatColor.GREEN + target.getJob() + ChatColor.GOLD + ".");
    }
}
