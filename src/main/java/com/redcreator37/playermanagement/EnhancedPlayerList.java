package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An enhanced TAB player list that displays data about the server
 */
public class EnhancedPlayerList implements Listener {

    /**
     * Initially sets the player list for the player on join
     *
     * @param event the player join event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateList(event.getPlayer());
    }

    /**
     * Updates / sets the advanced player list
     *
     * @param p the player that'll see the updated player list
     */
    public static void updateList(Player p) {
        String[] entry = {PlayerManagement.prefs.genericPlayerEntry};
        if (p.hasPermission("playerlist.member"))
            entry[0] = PlayerManagement.prefs.memberPlayerEntry;
        if (p.hasPermission("playerlist.vip"))
            entry[0] = PlayerManagement.prefs.vipPlayerEntry;
        if (p.hasPermission("playerlist.admin"))
            entry[0] = PlayerManagement.prefs.adminPlayerEntry;

        // replace the placeholders and set the name, header and footer
        p.setPlayerListName(replacePlaceholders(p, entry, Objects
                .requireNonNull(PlayerManagement.ess).getUser(p).isAfk()));
        p.setPlayerListHeaderFooter(replacePlaceholders(p,
                PlayerManagement.prefs.playerListHeader, false),
                replacePlaceholders(p, PlayerManagement.prefs.playerListFooter, false));
    }

    /**
     * Replaces the <code>{placeholders}</code> with real values
     *
     * @param p         the player to get the data from
     * @param str       the array of lines
     * @param playerAfk uses different formatting if the player is AFK
     * @return the string with data inserted
     */
    private static String replacePlaceholders(Player p, String[] str, boolean playerAfk) {
        String rank = PlayerManagement.prefs.genericPlayerLabel;
        if (p.hasPermission("playerlist.member"))
            rank = PlayerManagement.prefs.memberPlayerLabel;
        if (p.hasPermission("playerlist.vip"))
            rank = PlayerManagement.prefs.vipPlayerLabel;
        if (p.hasPermission("playerlist.admin"))
            rank = PlayerManagement.prefs.adminPlayerLabel;

        StringBuilder builder = new StringBuilder();
        for (String s : str)
            if (!s.matches("(.*)\\{(.*)}(.*)")) {
                builder.append(s).append('\n');
            } else {
                String company = "N/A";
                if (s.contains("{playercompany}")) {    // for performance reasons
                    ServerPlayer target = PlayerManagement.players
                            .get(p.getUniqueId().toString());
                    if (target != null)
                        company = target.getCompany().getName();
                }

                builder.append(s.replace("{playername}", playerAfk
                        ? "§o" + p.getName() + "§r" : p.getName())
                        .replace("{playercount}", String.valueOf(p.getServer()
                                .getOnlinePlayers().size()))
                        .replace("{maxplayers}", String.valueOf(p.getServer()
                                .getMaxPlayers()))
                        .replace("{playerbalance}", PlayerRoutines.formatDecimal(BigDecimal
                                .valueOf(PlayerManagement.eco.getBalance(p))))
                        .replace("{playermail}", String.valueOf(Objects
                                .requireNonNull(PlayerManagement.ess)
                                .getUser(p).getMails().size()))
                        .replace("{playerworld}", p.getWorld().getName())
                        .replace("{playerrank}", rank)
                        .replace("{playercompany}", company)
                        .replace("{playergamemode}", p.getGameMode().toString().toLowerCase()))
                        .append('\n');
            }
        return builder.toString();
    }

}
