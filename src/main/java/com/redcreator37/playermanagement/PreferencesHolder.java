package com.redcreator37.playermanagement;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Contains all user-settable preferences which apply to the currently
 * running instance of the plugin
 */
public class PreferencesHolder {

    /**
     * The language to use for all strings on this server
     */
    public String language = "en_US";

    /**
     * Any in-game console output will get prefixed by this
     */
    public String prefix = getDefaultPrefix();

    /**
     * The database to use to store the player data
     */
    String databasePath = "PlayerData.db";

    /**
     * The date format to be used, <strong>must</strong>
     * be a valid standard date format
     */
    public String dateFormat = "yyyy-MM-dd";

    public double cardPrice = 2500;
    public String cardItemLore = "Unique Player ID Card";
    public String scoreboardSignText = "§8[§9TopPlayers§8]";

    public double establishPrice = 3000;
    public double punishmentAmount = 3000;
    public int maxPunishments = 5;

    public boolean autoEcoEnabled = true;
    public int autoEcoTimeSeconds = 1200;
    public EconomyProvider.MinimalWage minimalWage =
            new EconomyProvider.MinimalWage(1000, 250);

    public boolean rewardsEnabled = true;
    public double rewardRank1 = 10;
    public double rewardRank2 = 25;
    public double rewardRank3 = 50;
    public List<String> advRank1 = AdvancementReward.getAdvancements(1);
    public List<String> advRank2 = AdvancementReward.getAdvancements(2);
    public List<String> advRank3 = AdvancementReward.getAdvancements(3);

    boolean playerListEnabled = true;
    int playerListUpdateSeconds = 300;
    public String[] playerListHeader = {
            "§b§oA Minecraft Server                                   §r§a{playercount}§8/§a{maxplayers}",
            "§8--------------------------------------------"
    };
    public String[] playerListFooter = {
            "§8--------------------------------------------",
            "§6Money: §f{playerbalance}    §r§8: :    §6Messages: §f{playermail}",
            "§6Gamemode: §f{playergamemode}    §r§8: :    §6World: §f{playerworld}",
            "§6Rank: §f{playerrank}    §r§8: :    §6Company: §f{playercompany}",
            "§8--------------------------------------------",
            "§7§oStatistics are updated every 5 min",
            "§8--------------------------------------------",
            "§4YouTube:   §r§f§nlink§r      §r§8: :      §9Discord:   §r§f§nlink§r             ",
            "\n§8§oPowered by PlayerManagement v1.6 by RedCreator37"
    };
    public String genericPlayerEntry = "§8[ §f{playername} §8]";
    public String memberPlayerEntry = "§8[ §a{playername} §8]";
    public String vipPlayerEntry = "§8[ §b{playername} §8]";
    public String adminPlayerEntry = "§8[ §c{playername} §8]";

    public String genericPlayerLabel = "§fNone";
    public String memberPlayerLabel = "§aMembers";
    public String vipPlayerLabel = "§bVIP";
    public String adminPlayerLabel = "§cAdmins";

    /**
     * Loads the values from the config file or generates them if they
     * don't exist yet
     */
    @SuppressWarnings("unchecked")
    public void loadConfig(FileConfiguration conf) {
        // set up the default values
        conf.options().header("PlayerManagement Config File\n----------------------------\n");
        conf.addDefault("General.Language", language);
        conf.addDefault("General.OutputPrefix", prefix.substring(0, prefix.length() - 1));
        conf.addDefault("General.DatabasePath", databasePath);
        conf.addDefault("General.DateFormat", dateFormat);
        conf.addDefault("General.CardPrice", cardPrice);
        conf.addDefault("General.CardItemLore", cardItemLore);
        conf.addDefault("General.ScoreboardSignText", scoreboardSignText);
        conf.addDefault("Company.EstablishPrice", establishPrice);
        conf.addDefault("Punishments.Amount", punishmentAmount);
        conf.addDefault("Punishments.MaxBeforeBan", maxPunishments);
        conf.addDefault("AutomaticEconomy.Enabled", autoEcoEnabled);
        conf.addDefault("AutomaticEconomy.TimeInSeconds", autoEcoTimeSeconds);
        conf.addDefault("AutomaticEconomy.Threshold", minimalWage.getThreshold());
        conf.addDefault("AutomaticEconomy.MoneyAmount", minimalWage.getAmount());
        conf.addDefault("Rewards.Enabled", rewardsEnabled);
        conf.addDefault("Rewards.Reward.Rank1", rewardRank1);
        conf.addDefault("Rewards.Reward.Rank2", rewardRank2);
        conf.addDefault("Rewards.Reward.Rank3", rewardRank3);
        conf.addDefault("Rewards.Advancements.Rank1", advRank1);
        conf.addDefault("Rewards.Advancements.Rank2", advRank2);
        conf.addDefault("Rewards.Advancements.Rank3", advRank3);
        conf.addDefault("PlayerList.Enabled", playerListEnabled);
        conf.addDefault("PlayerList.UpdateIntervalSeconds", playerListUpdateSeconds);
        conf.addDefault("PlayerList.Header", Arrays.asList(playerListHeader));
        conf.addDefault("PlayerList.Footer", Arrays.asList(playerListFooter));
        conf.addDefault("PlayerList.Display.Generic", genericPlayerEntry);
        conf.addDefault("PlayerList.Display.Members", memberPlayerEntry);
        conf.addDefault("PlayerList.Display.VIP", vipPlayerEntry);
        conf.addDefault("PlayerList.Display.Admins", adminPlayerEntry);
        conf.addDefault("PlayerList.Label.Generic", genericPlayerLabel);
        conf.addDefault("PlayerList.Label.Members", memberPlayerLabel);
        conf.addDefault("PlayerList.Label.VIP", vipPlayerLabel);
        conf.addDefault("PlayerList.Label.Admins", adminPlayerLabel);
        conf.options().copyHeader(true);
        conf.options().copyDefaults(true);

        // load the values from config file
        language = conf.getString("General.Language");
        prefix = conf.getString("General.OutputPrefix") + " ";
        String newPath = conf.getString("General.DatabasePath");
        databasePath = newPath != null && newPath.trim().equals("")
                ? databasePath : newPath;
        String newDateFormat = conf.getString("General.DateFormat");
        dateFormat = newDateFormat != null && newDateFormat.trim().equals("")
                ? dateFormat : newDateFormat;
        cardPrice = conf.getInt("General.CardPrice");
        String newLore = conf.getString("General.CardItemLore");
        cardItemLore = Objects.requireNonNull(newLore).trim()
                .equals("") ? cardItemLore : newLore;
        scoreboardSignText = conf.getString("General.ScoreboardSignText");

        establishPrice = conf.getInt("Company.EstablishPrice");
        punishmentAmount = conf.getInt("Punishments.Price");
        maxPunishments = conf.getInt("Punishments.MaxBeforeBan");

        autoEcoEnabled = conf.getBoolean("AutomaticEconomy.Enabled");
        autoEcoTimeSeconds = conf.getInt("AutomaticEconomy.TimeInSeconds");
        minimalWage = new EconomyProvider.MinimalWage(
                conf.getDouble("AutomaticEconomy.MoneyAmount"),
                conf.getDouble("AutomaticEconomy.Threshold"));

        rewardsEnabled = conf.getBoolean("Rewards.Enabled");
        rewardRank1 = conf.getDouble("Rewards.Reward.Rank1");
        rewardRank2 = conf.getDouble("Rewards.Reward.Rank2");
        rewardRank3 = conf.getDouble("Rewards.Reward.Rank3");
        advRank1 = (List<String>) conf.getList("Rewards.Advancements.Rank1");
        advRank2 = (List<String>) conf.getList("Rewards.Advancements.Rank2");
        advRank3 = (List<String>) conf.getList("Rewards.Advancements.Rank3");

        playerListEnabled = conf.getBoolean("PlayerList.Enabled");
        playerListUpdateSeconds = conf.getInt("PlayerList.UpdateIntervalSeconds");
        playerListHeader = PlayerRoutines.stringListToArray((List<String>) Objects
                .requireNonNull(conf.getList("PlayerList.Header")));
        playerListFooter = PlayerRoutines.stringListToArray((List<String>) Objects
                .requireNonNull(conf.getList("PlayerList.Footer")));
        genericPlayerEntry = conf.getString("PlayerList.Display.Generic");
        memberPlayerEntry = conf.getString("PlayerList.Display.Members");
        vipPlayerEntry = conf.getString("PlayerList.Display.VIP");
        adminPlayerEntry = conf.getString("PlayerList.Display.Admins");
        genericPlayerLabel = conf.getString("PlayerList.Label.Generic");
        memberPlayerLabel = conf.getString("PlayerList.Label.Members");
        vipPlayerLabel = conf.getString("PlayerList.Label.VIP");
        adminPlayerLabel = conf.getString("PlayerList.Label.Admins");
    }

    /**
     * Returns the plugin's localized default chat output prefix
     *
     * @return the formatted string
     */
    private static String getDefaultPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.BLUE
                + Localization.lc("server") + ChatColor.DARK_GRAY + "] ";
    }

}
