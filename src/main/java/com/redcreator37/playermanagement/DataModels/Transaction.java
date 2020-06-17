package com.redcreator37.playermanagement.DataModels;

import com.redcreator37.playermanagement.PlayerCard;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
     * Displays a list of all transactions
     *
     * @param p the player that'll see the list
     * @param c the company to get the data from
     */
    public static void listTransactions(Player p, Company c) {
        List<Transaction> transactions = PlayerManagement
                .transactions.stream().filter(t -> t.getCompanyId() == c.getId())
                .collect(Collectors.toList());

        if (transactions.size() < 1) {
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "The company " + ChatColor.GREEN + c.getName()
                    + ChatColor.GOLD + " has no transactions.");
            return;
        }

        List<String> pages = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            StringBuilder sb = new StringBuilder("§1§lDIR §r§1|" +
                    " §1§lTEXT §r§1| §1§lAMOUNT§r\n");
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
        PlayerCard.openBook(p, pages, "N/A", "N/A");
    }

}
