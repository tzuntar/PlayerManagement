package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.redcreator37.playermanagement.PlayerRoutines.truncate;

/**
 * Displays all player data from the database
 */
public class PlayerAdmin implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null) return true;

        if (!PlayerRoutines.checkPermission(p, "management.admin"))
            return true;

        p.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
        p.sendMessage("§bID §9|   §bUSERNAME   §9|   §bJOINED   §9| §bJOB NAME §9| §bCOMPANY §9|   §bPT.");
        p.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");

        PlayerManagement.players.forEach((s, pl) -> {
            StringBuilder b = new StringBuilder();
            String id = String.valueOf(pl.getId());
            if (id.length() < 2) id += " ";
            b.append(id).append(" | ");
            b.append(truncate(pl.getUsername(), 14)).append(" | ");
            b.append(truncate(pl.getJoinDate(), 10)).append(" | ");
            b.append(truncate(pl.getJob().getName(), 14)).append(" | ");
            b.append(truncate(pl.getCompany().getName(), 10)).append(" | ");
            b.append(pl.getPunishments());
            p.sendMessage(b.toString());
        });

        p.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
        return true;
    }

}
