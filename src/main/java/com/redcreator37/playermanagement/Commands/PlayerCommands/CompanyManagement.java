package com.redcreator37.playermanagement.Commands.PlayerCommands;

import com.redcreator37.playermanagement.Commands.CommandHelper;
import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.PlayerTag;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.IdHandling.CompanyMenu;
import com.redcreator37.playermanagement.IdHandling.InfoCards;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.redcreator37.playermanagement.Localization.lc;
import static com.redcreator37.playermanagement.PlayerManagement.companies;
import static com.redcreator37.playermanagement.PlayerManagement.companyDb;
import static com.redcreator37.playermanagement.PlayerManagement.eco;
import static com.redcreator37.playermanagement.PlayerManagement.getPlugin;
import static com.redcreator37.playermanagement.PlayerManagement.players;

/**
 * Opens the in-game company management UI or changes the values
 * manually
 */
public class CompanyManagement extends PlayerCommand {

    public CompanyManagement() {
        super("company", new HashMap<String, Boolean>() {{
            put("info", false);
            put("transactions", false);
        }}, new ArrayList<String>() {{
            add("management.company");
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
        Optional<ServerPlayer> optTarget = getUserOrAdmin(player, args, 0, 0);
        if (!optTarget.isPresent()) return;
        ServerPlayer target = optTarget.get();
        String prefix = PlayerManagement.prefs.prefix;

        if (PlayerRoutines.lacksPermission(player, "management.company"))
            return;

        if (args.length == 2 && !args[1].matches("info|transactions")) {
            HashMap<String, Boolean> subArgs = new HashMap<String, Boolean>() {{
                put("name", true);
                put("increase|decrease|deposit|withdraw|setdesc|setowner|transaction|remove", false);
                put("args...", false);
            }};
            player.sendMessage(prefix + CommandHelper.parseCommandUsage(getName(), subArgs));
            return;
        }

        // manage the player's company or someone else's if the
        // player has the admin permissions
        Optional<Company> comp = player.hasPermission("management.admin") && args.length > 0
                ? Optional.of(companies.get(args[0])) : target.getCompany();
        if (!comp.isPresent()) {
            player.sendMessage(prefix + lc("player-not-an-owner-of-any-company"));
            return;
        }
        Company company = comp.get();

        // check the ownership
        Optional<PlayerTag> ownerTag = company.getOwner();
        if ((!ownerTag.isPresent() || !ownerTag.get().getUsername().equals(player.getName()))
                && !player.hasPermission("management.admin")) {
            player.sendMessage(prefix + lc("you-can-only-manage-your-company"));
            return;
        }

        if (args.length < 2) {  // no arguments, open the menu
            new CompanyMenu(player, company + lc("management"), company);
            return;
        }

        // else process the commands
        BigDecimal amount = null;
        if (args[1].matches("increase|decrease|deposit|withdraw")) {
            amount = PlayerRoutines.getEnteredBigDecimal(player, args[2]);
            if (amount == null) return;
        }

        switch (args[1]) {
            case "info":
                InfoCards.displayCompanyInfo(player, company);
                break;
            case "increase":
                company.setWage(company.getWage().add(amount));
                player.sendMessage(prefix + MessageFormat.format(lc("wages-increased-by"), amount));
                break;
            case "decrease":
                try {
                    company.setWage(company.getWage().subtract(amount));
                    player.sendMessage(prefix + MessageFormat.format(lc("wages-decreased-by"),
                            amount));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(prefix + lc("wage-cannot-be-negative"));
                }
                break;
            case "deposit":
                eco.withdrawPlayer(player, Objects.requireNonNull(amount)
                        .doubleValue());
                company.setBalance(company.getBalance().add(amount));
                player.sendMessage(prefix + MessageFormat.format(lc("has-been-taken-from-your-account"),
                        amount));

                PlayerManagement.transactionDb.addAsync(player, new Transaction(4097,
                        company.getId(), "<-",
                        MessageFormat.format(lc("deposit-amount"), amount),
                        MessageFormat.format(lc("deposit-to-player"), amount, target), amount));
                break;
            case "withdraw":
                company.setBalance(company.getBalance().subtract(amount));
                eco.depositPlayer(player, Objects.requireNonNull(amount).doubleValue());
                player.sendMessage(prefix + MessageFormat.format(lc("has-been-added-to-your-account"),
                        amount));

                PlayerManagement.transactionDb.addAsync(player, new Transaction(4097,
                        company.getId(), "->",
                        MessageFormat.format(lc("withdraw-amount"), amount),
                        MessageFormat.format(lc("withdraw-from-player"), amount, target), amount));
                break;
            case "setdesc":
                company.setDescription(CommandHelper.getFullEntry(args, 2));
                player.sendMessage(prefix + lc("description-set"));
                break;
            case "setowner":
                ServerPlayer newOwner = players.byUsername(args[2]);
                if (PlayerRoutines.checkPlayerNonExistent(player, newOwner, args[2]))
                    return;

                company.setOwner(new PlayerTag(newOwner.getUsername(), newOwner.getUuid()));
                player.sendMessage(prefix + MessageFormat.format(lc("ownership-changed-to"),
                        target));
                break;
            case "transactions":
                Transaction.listTransactions(player, company);
                break;
            case "remove":
                try {
                    companyDb.remove(company);
                    companies = companyDb.getAll();
                    player.sendMessage(prefix + MessageFormat.format(lc("removed-company"),
                            args[1]));
                } catch (SQLException e) {
                    player.sendMessage(prefix + MessageFormat.format(lc("error-removing-company-data"),
                            e.getMessage()));
                }
            default:
                player.sendMessage(prefix + MessageFormat.format(lc("unknown-company"),
                        args[1]));
                return;
        }

        if (args[1].matches("info|transactions|remove"))
            return;    // no changes made, don't update the db

        Bukkit.getScheduler().runTask(getPlugin(PlayerManagement.class), () -> {
            PlayerManagement.companyDb.updateByPlayer(player, company);
            try {
                PlayerManagement.transactions = PlayerManagement
                        .transactionDb.getAll();
            } catch (SQLException e) {
                player.sendMessage(prefix + MessageFormat.format(lc("error-saving-transaction-data"),
                        e.getMessage()));
            }
        });
    }
}
