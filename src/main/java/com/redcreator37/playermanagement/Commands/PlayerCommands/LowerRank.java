package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.CommandHelper;
import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.redcreator37.playermanagement.Localization.lc;

/**
 * Increases a player's punishment count, takes the amount of money
 * and bans them if their count exceeds the maximum
 */
public class LowerRank extends PlayerCommand {

    public LowerRank() {
        super("punish", new HashMap<String, Boolean>() {{
            put("player_name", true);
            put("Reason...", false);
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

        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                // take the amount of money
                try {
                    PlayerManagement.eco.withdrawPlayer(player.getServer().getPlayer(target
                            .getUsername()), PlayerManagement.prefs.fineAmount);
                } catch (Exception e) {
                    player.sendMessage(prefix + ChatColor.GOLD
                            + lc("the-player")
                            + ChatColor.GREEN + target + ChatColor.GOLD
                            + lc("isnt-online-money-not-taken"));
                }

                target.setPunishments(target.getPunishments() + 1);

                if (args.length > 1) {  // if there's a reason specified
                    String reason = CommandHelper.getFullEntry(args, 1);
                    try {   // tell them if they're online, otherwise ignore it
                        Objects.requireNonNull(player.getServer().getPlayer(target
                                .getUsername())).sendMessage(prefix
                                + ChatColor.GOLD + lc("you-have-been-punished")
                                + ChatColor.GREEN + reason);
                    } catch (NullPointerException ignored) {}
                }

                // limit exceeded, issue the ban
                if (target.getPunishments() > PlayerManagement.prefs.maxFines) {
                    player.getServer().getBannedPlayers().add(player.getServer()
                            .getPlayer(target.getUsername()));
                    player.sendMessage(prefix + ChatColor.GOLD
                            + lc("the-player") + ChatColor.GREEN + target
                            + ChatColor.GOLD + lc("has-exceeded-max-punishments"));
                } else {
                    player.sendMessage(prefix + ChatColor.GOLD
                            + lc("the-player") + ChatColor.GREEN + target
                            + ChatColor.GOLD + lc("has-been-punished"));
                }
                PlayerManagement.players.updatePlayerEntry(target);
            } catch (SQLException e) {
                player.sendMessage(prefix + ChatColor.GOLD
                        + lc("error-updating-playerdata")
                        + ChatColor.RED + e.getMessage());
            }
        });
    }
}
