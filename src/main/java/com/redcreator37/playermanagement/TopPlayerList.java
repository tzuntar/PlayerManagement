package com.redcreator37.playermanagement;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.redcreator37.playermanagement.PlayerManagement.eco;

/**
 * Handles the sign placement event, generates the
 * Top Players signs
 */
public class TopPlayerList implements Listener {

    /**
     * Handles the sign text change event
     *
     * @param event the sign change event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void eventSignChanged(SignChangeEvent event) {
        if (!event.getLines()[0].equalsIgnoreCase("[topplayers]")) return;

        event.setLine(0, PlayerManagement.scoreboardSignText);
        List<String> topPlayers = getTopPlayers(Arrays
                .asList(Bukkit.getOfflinePlayers())).stream()
                .distinct().collect(Collectors.toList());

        for (int i = 0, size = topPlayers.size(); i < size; i++) {
            String p = topPlayers.get(i);
            String text = "§4" + (i + 1) + ".§r ";
            text += p != null ? p : "§oN/A";
            if (i > 3) break;   // in-game sign entity constraint
            event.setLine(i + 1, text);
        }
    }

    /**
     * Returns a sorted list of usernames of top players
     * from this list
     *
     * @param players the player list to use
     * @return the sorted list of top players' usernames
     */
    private static List<String> getTopPlayers(List<OfflinePlayer> players) {
        return sortPlayerList(players).stream()
                .map(OfflinePlayer::getName).collect(Collectors.toList());
    }

    /**
     * Sorts this player list by player balance
     *
     * @param players the player list to sort
     * @return the sorted list
     */
    private static List<OfflinePlayer> sortPlayerList(List<OfflinePlayer> players) {
        players.sort((lhs, rhs) -> Double
                .compare(eco.getBalance(rhs), eco.getBalance(lhs)));
        return players;
    }

}
