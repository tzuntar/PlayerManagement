package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Establishes a new in-game company
 */
public class EstablishCompany extends PlayerCommand {

    public EstablishCompany() {
        super("establish", new LinkedHashMap<String, Boolean>() {{
            put("company_name", true);
        }}, new ArrayList<String>() {{
            add("company.management.establish");
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
        if (!PlayerManagement.companies.doesNotExist(args[0])) {
            player.sendMessage(PlayerManagement.prefs.prefix + Localization
                    .lc("already-exists"));
            return;
        }

        if (!PlayerManagement.eco.has(player, PlayerManagement.prefs.establishPrice)) {
            player.sendMessage(PlayerManagement.prefs.prefix + Localization
                    .lc("not-enough-money-to-establish"));
            return;
        }

        Company newCompany = new Company(4097, args[0]);
        newCompany.setOwner(new PlayerTag(player.getName(), player.getUniqueId()));
        Bukkit.getScheduler().runTask(PlayerManagement
                .getPlugin(PlayerManagement.class), () -> {
            try {
                PlayerManagement.eco.withdrawPlayer(player, PlayerManagement
                        .prefs.establishPrice);
                newCompany.setBalance(new BigDecimal(PlayerManagement
                        .prefs.establishPrice / 2));
                PlayerManagement.companies.setByName(newCompany);
                player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat.format(Localization
                        .lc("company-registration-successful"), newCompany));
            } catch (SQLException e) {
                player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat.format(Localization
                        .lc("error-saving-company-data"), e.getMessage()));
            }
        });
    }
}
