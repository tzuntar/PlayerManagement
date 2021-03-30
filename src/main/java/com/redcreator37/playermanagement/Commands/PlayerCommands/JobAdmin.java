package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.CommandHelper;
import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Job;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Manages the job database
 */
public class JobAdmin extends PlayerCommand {

    public JobAdmin() {
        super("jobadmin", new HashMap<String, Boolean>() {{
            put("add|update|remove", true);
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
        if (args.length < 2 || args.length < 3 && args[0].matches("add|update")) {
            player.sendMessage(PlayerManagement.prefs.prefix + CommandHelper
                    .parseCommandUsage("jobadmin", new HashMap<String, Boolean>() {{
                        put("add|update|remove", true);
                        put("job_name", true);
                        put("Job Description", true);
                    }}));
            return;
        }

        try {
            switch (args[0]) {
                case "add":
                    String jobDesc = CommandHelper.getFullEntry(args, 2);
                    PlayerManagement.jobDb.insert(new Job(4097, args[1], jobDesc));
                    break;
                case "remove":
                    Job j = PlayerManagement.jobs.get(args[1]);
                    if (j == null) {
                        player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                                + Localization.lc("unknown-job")
                                + ChatColor.GREEN + args[1]);
                        return;
                    }
                    PlayerManagement.jobDb.remove(j);
                    break;
                case "update":
                    Job job = PlayerManagement.jobs.get(args[1]);
                    if (job == null) {
                        player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                                + Localization.lc("unknown-job")
                                + ChatColor.GREEN + args[1]);
                        return;
                    }
                    job.setDescription(CommandHelper.getFullEntry(args, 2));
                    PlayerManagement.jobDb.update(job);
                    break;
            }

            // update the job list to reflect the changes
            PlayerManagement.jobs = PlayerManagement.jobDb.getAll();
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + Localization.lc("job-data-saved"));
        } catch (SQLException e) {
            player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                    + Localization.lc("error-accessing-db")
                    + ChatColor.RED + e.getMessage());
        }
    }
}
