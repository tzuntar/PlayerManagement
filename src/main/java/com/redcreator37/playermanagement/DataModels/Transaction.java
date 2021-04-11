package com.redcreator37.playermanagement.DataModels;

import com.redcreator37.playermanagement.IdHandling.InfoCards;
import com.redcreator37.playermanagement.Localization;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents an in-game transaction
 */
public class Transaction {

    /**
     * Unique database id
     */
    private final int id;

    /**
     * The company id <strong>(must match an existing company!)</strong>
     */
    private final int companyId;

    /**
     * Can be anything, used to format the in-game output
     */
    private final String direction;

    /**
     * Represents the transaction title
     */
    private final String title;

    /**
     * Represents the transaction description
     */
    private final String description;

    /**
     * The transaction amount
     */
    private final BigDecimal amount;

    /**
     * Transaction constructor
     *
     * @param id          the database id
     * @param companyId   the company id
     * @param direction   can be anything, used when formatting output
     * @param title       transaction title
     * @param description transaction description
     * @param amount      transaction amount
     */
    public Transaction(int id, int companyId, String direction,
                       String title, String description, BigDecimal amount) {
        this.id = id;
        this.companyId = companyId;
        this.direction = direction;
        this.title = title;
        this.description = description;
        this.amount = amount;
    }

    /**
     * Displays a list of all transactions
     *
     * @param player  the {@link ServerPlayer} receiving the list
     * @param company the {@link Company} with the data
     */
    public static void listTransactions(Player player, Company company) {
        List<Transaction> transactions = PlayerManagement
                .transactions.stream().filter(t -> t.getCompanyId() == company.getId())
                .collect(Collectors.toList());

        if (transactions.size() < 1) {
            player.sendMessage(PlayerManagement.prefs.prefix + MessageFormat
                    .format(Localization.lc("company-has-no-transactions"), company));
            return;
        }

        List<String> pages = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            StringBuilder sb = new StringBuilder(Localization.lc("transactions-header"));
            Transaction t = transactions.get(i);
            sb.append(t);
            for (int j = 0; j < 3; j++) {
                i++;
                if (i < transactions.size()) {
                    t = transactions.get(i);
                    sb.append(t);
                }
            }
            pages.add(sb.toString());
        }
        InfoCards.openBook(player, pages, "N/A", "N/A");
    }

    public int getId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getDirection() {
        return direction;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the formatted data about this transaction
     *
     * @return the formatted string
     */
    @Override
    public String toString() {
        String formattedDirection = this.direction.equals("<-")
                ? "§2§l<-§r" : "§4§l->§r";
        String formattedAmount = this.direction.equals("<-")
                ? PlayerRoutines.formatDecimal(this.amount)
                : PlayerRoutines.formatDecimal(this.amount) + "-";
        return "\n" + formattedDirection + " | " + this.title
                + " | §1§l" + formattedAmount + "\n";
    }

    /**
     * Provides hash code functionality
     *
     * @return the hash code for this Transaction instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCompanyId(), getDirection(),
                getTitle(), getDescription(), getAmount());
    }

}
