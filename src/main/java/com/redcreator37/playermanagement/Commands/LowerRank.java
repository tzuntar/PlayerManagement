package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Increases a player's punishment count, takes the amount of money
 * and bans them if their count exceeds the maximum
 */
public class LowerRank implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null || !PlayerRoutines
                .checkPlayerPermission(p, "management.admin"))
            return true;

        if (args.length < 1) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Usage: "
                    + ChatColor.GREEN + "/punish player_name [Reason]");
            return true;
        }

        ServerPlayer target = PlayerRoutines
                .playerFromUsername(PlayerManagement.players, args[0]);
        if (PlayerRoutines.checkPlayerNonExistent(p, target, args[0]))
            return true;

        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                // take the amount of money
                try {
                    PlayerManagement.eco.withdrawPlayer(p.getServer().getPlayer(target
                            .getUsername()), PlayerManagement.punishmentAmount);
                } catch (Exception e) {
                    p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                            + "The player " + ChatColor.GREEN + target + ChatColor.GOLD
                            + " is not online, money will not be taken!");
                }

                target.setPunishments(target.getPunishments() + 1);

                if (args.length > 1) {  // if there's a reason specified
                    StringBuilder reason = new StringBuilder();
                    for (String arg : Arrays.copyOfRange(args, 1, args.length))
                        reason.append(arg).append(" ");
                    try {   // tell them if they're online, otherwise ignore it
                        Objects.requireNonNull(p.getServer().getPlayer(target
                                .getUsername())).sendMessage(PlayerManagement
                                .prefix + ChatColor.GOLD + "You have been punished: "
                                + ChatColor.GREEN + reason.toString());
                    } catch (NullPointerException ignored) {}
                }

                // limit exceeded, issue ban
                if (target.getPunishments() > PlayerManagement.maxPunishments) {
                    sender.getServer().getBannedPlayers().add(p.getServer()
                            .getPlayer(target.getUsername()));
                    p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                            + "The player " + ChatColor.GREEN + target
                            + ChatColor.GOLD + " has exceeded the max number of" +
                            " punishments and has been banned.");
                } else {
                    p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                            + "The player " + ChatColor.GREEN + target
                            + ChatColor.GOLD + " has been punished.");
                }

                PlayerManagement.playerDb.update(target);
                PlayerManagement.players = PlayerManagement.playerDb.getAll();
            } catch (SQLException e) {
                p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                        + "Error while modifying the playerdata: " + ChatColor.RED
                        + e.getMessage());
            }
        });
        return true;
    }
}
