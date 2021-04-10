package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
     * @param player   the {@link Player} who ran the command
     * @param args     the arguments entered by the player
     * @param executor the UUID of the executing player
     */
    @Override
    public void execute(Player player, String[] args, UUID executor) {
        ServerPlayer target = PlayerManagement.players.byUsername(args[0]);
        if (PlayerRoutines.checkPlayerNonExistent(player, target, args[0])) return;
        String message = target.getJob().isPresent()
                ? MessageFormat.format(Localization
                .lc("player-is-employed-as"), target, target.getJob())
                : MessageFormat.format(Localization.lc("player-is-unemployed"), target);
        player.sendMessage(PlayerManagement.prefs.prefix + message);
    }
}
