package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.Job;
import com.redcreator37.playermanagement.Database.JobDb;
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
        Player p = PlayerRoutines.getPlayerFromSender(sender);
        if (p == null) return true;

        if (!PlayerRoutines.checkPlayerPermissions(p, "management.admin"))
            return true;

        if (args.length < 2) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Usage: "
                    + ChatColor.GREEN + "/jobadmin [add|remove] job_name [Job Description]");
            return true;
        }

        try {
            if (args[0].equals("add")) {
                // get the job description
                StringBuilder jobDesc = new StringBuilder();
                for (String arg : Arrays.copyOfRange(args, 1, args.length))
                    jobDesc.append(arg).append(" ");

                JobDb.insertJob(new Job(4097, args[1], jobDesc.toString()),
                        PlayerManagement.databasePath);
            } else if (args[0].equals("delete")) {
                Job j = JobDb.getJobFromString(PlayerManagement.jobs, args[1]);
                if (j == null) {
                    p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                            + "Unknown job " + ChatColor.GREEN + args[1]);
                    return true;
                }
                JobDb.removeJob(j.getId(), PlayerManagement.databasePath);
            }

            // update the job list to reflect the changes
            PlayerManagement.jobs = JobDb.getAllJobs(PlayerManagement.databasePath);
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD + "Job data saved.");
        } catch (SQLException e) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Error while accessing the database: " + ChatColor.RED
                    + e.getMessage());
        }
        return true;
    }

}
