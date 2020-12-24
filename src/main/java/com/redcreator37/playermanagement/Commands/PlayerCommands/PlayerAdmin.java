package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static com.redcreator37.playermanagement.PlayerRoutines.truncate;

/**
 * Displays all player data from the database
 */
public class PlayerAdmin extends PlayerCommand {

    public PlayerAdmin() {
        super("playeradmin", new HashMap<>(), new ArrayList<String>() {{
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
        player.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
        player.sendMessage("§bID §9|   §bUSERNAME   §9|   §bJOINED   §9| §bJOB NAME §9| §bCOMPANY §9|   §bPT.");
        player.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
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
            player.sendMessage(b.toString());
        });
        player.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
    }
}
