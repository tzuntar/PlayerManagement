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
        p.setPlayerListName(fillDataFields(p, entry, Objects
                .requireNonNull(PlayerManagement.ess).getUser(p).isAfk()));
        p.setPlayerListHeaderFooter(fillDataFields(p,
                PlayerManagement.prefs.playerListHeader, false),
                fillDataFields(p, PlayerManagement.prefs.playerListFooter, false));
    }

    /**
     * Fills the values of placeholder data fields in the player list
     * output with actual values and formats the output
     *
     * @param player the player to get the data from
     * @param str    the array of lines
     * @param isAfk  uses different formatting if the player is AFK
     * @return the string with data inserted
     */
    private static String fillDataFields(Player player, String[] str, boolean isAfk) {
        String rank = PlayerManagement.prefs.genericPlayerLabel;
        if (player.hasPermission("playerlist.member"))
            rank = PlayerManagement.prefs.memberPlayerLabel;
        if (player.hasPermission("playerlist.vip"))
            rank = PlayerManagement.prefs.vipPlayerLabel;
        if (player.hasPermission("playerlist.admin"))
            rank = PlayerManagement.prefs.adminPlayerLabel;

        StringBuilder builder = new StringBuilder();
        for (String s : str)
            if (!s.matches(".*\\{.*}.*")) {
                builder.append(s).append('\n');
            } else {
                String company = Localization.lc("unknown");
                if (s.contains("{playercompany}")) {    // for performance reasons
                    ServerPlayer target = PlayerManagement.players
                            .byUuid(player.getUniqueId());
                    if (target != null && target.getCompany().isPresent())
                        company = target.getCompany().get().toString();
                }
                builder.append(replacePlaceholders(s, player, company,
                        rank, isAfk)).append('\n');
            }
        return builder.toString();
    }

    /**
     * Replaces the {@code {placeholders}} with real values
     *
     * @param original the original string containing placeholders
     * @param p        the player this instance of the list will be
     *                 displayed to
     * @param company  the player's company
     * @param rank     the player's rank
     * @param isAfk    whether the player is AFK or not
     * @return the formatted string
     */
    private static String replacePlaceholders(String original, Player p, String company,
                                              String rank, boolean isAfk) {
        return original.replace("{playername}", isAfk
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
                .replace("{playergamemode}", p.getGameMode().toString().toLowerCase());
    }

    /**
     * Initially sets the player list for the player on join
     *
     * @param event the player join event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateList(event.getPlayer());
    }

}
