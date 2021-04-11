package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.CommandHelper;
import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Job;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Manages the job database
 */
public class JobAdmin extends PlayerCommand {

    public JobAdmin() {
        super("jobadmin", new LinkedHashMap<String, Boolean>() {{
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
            String enteredJob = args[1];
            switch (args[0]) {
                case "add":
                    String jobDesc = CommandHelper.getFullEntry(args, 2);
                    PlayerManagement.jobDb.insert(new Job(4097, enteredJob, jobDesc));
                    break;
                case "remove":
                    Job j = PlayerManagement.jobs.get(enteredJob);
                    if (j == null) {
                        player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat
                                .format(Localization.lc("unknown-job"), enteredJob));
                        return;
                    }
                    PlayerManagement.jobDb.remove(j);
                    break;
                case "update":
                    Job job = PlayerManagement.jobs.get(enteredJob);
                    if (job == null) {
                        player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat
                                .format(Localization.lc("unknown-job"), enteredJob));
                        return;
                    }
                    job.setDescription(CommandHelper.getFullEntry(args, 2));
                    PlayerManagement.jobDb.update(job);
                    break;
            }

            // update the job list to reflect the changes
            PlayerManagement.jobs = PlayerManagement.jobDb.getAll();
            player.sendMessage(PlayerManagement.prefs.prefix + Localization.lc("job-data-saved"));
        } catch (SQLException e) {
            player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat
                    .format(Localization.lc("error-saving-job-data"), e.getMessage()));
        }
    }
}
