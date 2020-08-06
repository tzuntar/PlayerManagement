package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.Job;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Manages the job database
 */
public class JobAdmin implements CommandExecutor {

    /**
     * Main command process
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = PlayerRoutines.playerFromSender(sender);
        if (p == null) return true;

        if (!PlayerRoutines.checkPlayerPermission(p, "management.admin"))
            return true;

        if (args.length < 2 || args.length < 3 && args[0].matches("add|update")) {
            p.sendMessage(PlayerManagement.prefix + CommandHelper
                    .parseCommandUsage("jobadmin", new String[]{"add|update|remove",
                            "*job_name", "Job Description"}));
            return true;
        }

        try {
            switch (args[0]) {
                case "add": {
                    // get the job description
                    StringBuilder jobDesc = new StringBuilder();
                    for (String arg : Arrays.copyOfRange(args, 2, args.length))
                        jobDesc.append(arg).append(" ");

                    PlayerManagement.jobDb.insert(new Job(4097, args[1], jobDesc.toString()));
                    break;
                }
                case "remove": {
                    Job j = PlayerManagement.jobs.get(args[1]);
                    if (j == null) {
                        p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                                + "Unknown job " + ChatColor.GREEN + args[1]);
                        return true;
                    }
                    PlayerManagement.jobDb.remove(j.getId());
                    break;
                }
                case "update": {
                    Job j = PlayerManagement.jobs.get(args[1]);
                    if (j == null) {
                        p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                                + "Unknown job " + ChatColor.GREEN + args[1]);
                        return true;
                    }
                    StringBuilder jobDesc = new StringBuilder();
                    for (String arg : Arrays.copyOfRange(args, 2, args.length))
                        jobDesc.append(arg).append(" ");

                    j.setDescription(jobDesc.toString());
                    PlayerManagement.jobDb.update(j);
                    break;
                }
            }

            // update the job list to reflect the changes
            PlayerManagement.jobs = PlayerManagement.jobDb.getAll();
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Job data saved.");
        } catch (SQLException e) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Error while accessing the database: " + ChatColor.RED
                    + e.getMessage());
        }
        return true;
    }

}
