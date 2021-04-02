package com.redcreator37.playermanagement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Grants rewards to players on advancements
 */
public class AdvancementReward implements Listener {

    /**
     * Returns the list of default advancements for this rank
     *
     * @param rank the rank to use (1, 2 or 3)
     * @return the filled list of advancements
     */
    static List<String> getAdvancements(int rank) {
        String source = getDefaultRank(rank);
        return Arrays.asList(source.split("\n"));
    }

    /**
     * Attempts to read the default advancement list for this rank
     * from the corresponding resources file
     *
     * @param rank the rank ID
     * @return the text in the file or a blank string if there were
     * errors reading the file
     */
    private static String getDefaultRank(int rank) {
        String result = "";
        try {
            result = PlayerRoutines.readResourcesFile(PlayerManagement.class.getClassLoader()
                    .getResourceAsStream("DefaultAdvancements/Rank" + rank + ".txt"));
        } catch (IOException e) {
            System.err.println(MessageFormat.format(Localization
                    .lc("loading-default-rank-failed"), e.getMessage()));
        }
        return result;
    }

    /**
     * Handles the advancement done event
     *
     * @param event the advancement done event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player p = event.getPlayer();
        if (PlayerManagement.players.doesNotExist(event.getPlayer().getName()))
            return;

        Bukkit.getScheduler().runTask(PlayerManagement.getPlugin(PlayerManagement.class), () -> {
            // get the reward per rank
            double reward;
            String adv = event.getAdvancement().getKey().getKey();
            if (adv.contains("recipes/")) return;   // skip the recipes
            if (PlayerRoutines.checkIfContains(PlayerManagement.prefs.advRank1, adv))
                reward = PlayerManagement.prefs.rewardRank1;
            else if (PlayerRoutines.checkIfContains(PlayerManagement.prefs.advRank2, adv))
                reward = PlayerManagement.prefs.rewardRank2;
            else if (PlayerRoutines.checkIfContains(PlayerManagement.prefs.advRank3, adv))
                reward = PlayerManagement.prefs.rewardRank3;
            else return;

            PlayerManagement.eco.depositPlayer(p, reward);
            p.sendMessage(PlayerManagement.prefs.prefix + MessageFormat.format(Localization
                    .lc("advancement-completed-reward"), reward));
        });
    }

}
