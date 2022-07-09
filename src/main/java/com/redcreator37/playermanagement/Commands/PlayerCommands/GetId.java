package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.IdHandling.InfoCards;
import com.redcreator37.playermanagement.IdHandling.PlayerCard;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Gives the player a new ID card or displays the data for another
 * player if that player's name was specified and the command executor
 * has sufficient permissions
 */
public class GetId extends PlayerCommand {

    public GetId() {
        super("getid", new LinkedHashMap<String, Boolean>() {{
            put("player_name", true);
        }}, new ArrayList<String>() {{
            add("management.user");
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
        Optional<ServerPlayer> optTarget = getUserOrAdmin(player, args, 0, 1);
        if (!optTarget.isPresent()) return;
        ServerPlayer target = optTarget.get();

        if (args[0].equalsIgnoreCase(player.getName())) {  // if used by the player themselves, give them a new ID
            double cardPrice = PlayerManagement.prefs.cardPrice;
            if (!PlayerManagement.eco.has(player, cardPrice)) {
                player.sendMessage(PlayerManagement.prefs.prefix + Localization
                        .lc("not-enough-money-for-new-id"));
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {  // make sure the inventory isn't full
                player.sendMessage(PlayerManagement.prefs.prefix + ChatColor.GOLD
                        + Localization.lc("inventory-full"));
                return;
            }

            EconomyResponse trans = PlayerManagement.eco.withdrawPlayer(player, cardPrice);
            if (trans.type != EconomyResponse.ResponseType.SUCCESS) {
                player.sendMessage(PlayerManagement.prefs.prefix
                        + MessageFormat.format(Localization.lc("transaction-failed"), trans.errorMessage));
                return;
            }
            PlayerCard.giveNewCard(player, target);
            player.sendMessage(PlayerManagement.prefs.prefix
                    + MessageFormat.format(Localization.lc("bought-new-id"), cardPrice));
            return;
        }

        // if it came so far, we can assume that args.length >= 1
        if (PlayerRoutines.lacksPermission(player, "management.admin"))
            return;

        InfoCards.displayPlayerInfo(player, target);
    }
}
