package com.redcreator37.playermanagement;

import com.earth2me.essentials.Essentials;
import com.redcreator37.playermanagement.Commands.CompanyManagement;
import com.redcreator37.playermanagement.Commands.CompanyPay;
import com.redcreator37.playermanagement.Commands.DeleteId;
import com.redcreator37.playermanagement.Commands.EstablishCompany;
import com.redcreator37.playermanagement.Commands.GetId;
import com.redcreator37.playermanagement.Commands.GetJob;
import com.redcreator37.playermanagement.Commands.JobAdmin;
import com.redcreator37.playermanagement.Commands.LowerRank;
import com.redcreator37.playermanagement.Commands.PlayerAdmin;
import com.redcreator37.playermanagement.Commands.RegisterId;
import com.redcreator37.playermanagement.Commands.SetCompany;
import com.redcreator37.playermanagement.Commands.SetJob;
import com.redcreator37.playermanagement.Commands.SetNotes;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.Job;
import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.Database.CompanyDb;
import com.redcreator37.playermanagement.Database.JobDb;
import com.redcreator37.playermanagement.Database.PlayerDb;
import com.redcreator37.playermanagement.Database.SharedDb;
import com.redcreator37.playermanagement.Database.TransactionDb;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * A Minecraft Spigot Server plugin that extends native player data
 * handling capabilities and provides realistic economy management
 *
 * @author RedCreator37
 */
public final class PlayerManagement extends JavaPlugin {

    /**
     * The language to use for all strings on this server
     */
    public static String language = "en_US";

    /**
     * The resource bundle to use for retrieving localized strings
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static ResourceBundle strings = getBundleFromLangCode("Strings", language).get();

    /**
     * Any in-game console output will get prefixed by this
     */
    public static String prefix = getDefaultPrefix();

    /**
     * The currently loaded Essentials plugin object
     */
    static final Essentials ess = (Essentials) Bukkit.getPluginManager()
            .getPlugin("Essentials");

    /**
     * The currently loaded Vault plugin object
     */
    public static Economy eco = null;

    /**
     * The database to use to store the player data
     */
    private static String databasePath = "PlayerData.db";

    /**
     * The database connection to use for all operations
     */
    private static Connection database = null;

    /**
     * The date format to be used, <strong>must</strong>
     * be a valid standard date format
     */
    public static String dateFormat = "yyyy-MM-dd";

    /**
     * Contains the data for all players on the server
     * The key is the player UUID, the value is the matching
     * ServerPlayer object
     */
    public static Map<String, ServerPlayer> players = null;

    /**
     * Contains the data for all jobs on the server
     * The key is the job name, the value is the matching Job object
     */
    public static Map<String, Job> jobs = null;

    /**
     * Contains the data for all companies on the server
     * The key is the company name, the value is the matching Company
     * object
     */
    public static Map<String, Company> companies = null;

    /**
     * Contains all transactions on the server
     */
    public static List<Transaction> transactions = null;

    /**
     * Provides access to the player database
     */
    public static PlayerDb playerDb = null;

    /**
     * Provides access to the job database
     */
    public static JobDb jobDb = null;

    /**
     * Provides access to the company database
     */
    public static CompanyDb companyDb = null;

    /**
     * Provides access to the transaction database
     */
    public static TransactionDb transactionDb = null;

    /**
     * Plugin startup logic
     */
    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("Essentials")) {
            getLogger().severe(strings.getString("error-essentials-not-detected"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setUpEconomy()) {  // try to detect and enable Vault
            getLogger().severe(strings.getString("error-vault-not-detected"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // register commands
        Objects.requireNonNull(this.getCommand("registerid"))
                .setExecutor(new RegisterId());
        Objects.requireNonNull(this.getCommand("getid"))
                .setExecutor(new GetId());
        Objects.requireNonNull(this.getCommand("deleteid"))
                .setExecutor(new DeleteId());
        Objects.requireNonNull(this.getCommand("setjob"))
                .setExecutor(new SetJob());
        Objects.requireNonNull(this.getCommand("setcompany"))
                .setExecutor(new SetCompany());
        Objects.requireNonNull(this.getCommand("setnotes"))
                .setExecutor(new SetNotes());
        Objects.requireNonNull(this.getCommand("job"))
                .setExecutor(new GetJob());
        Objects.requireNonNull(this.getCommand("punish"))
                .setExecutor(new LowerRank());
        Objects.requireNonNull(this.getCommand("jobadmin"))
                .setExecutor(new JobAdmin());
        Objects.requireNonNull(this.getCommand("company"))
                .setExecutor(new CompanyManagement());
        Objects.requireNonNull(this.getCommand("establish"))
                .setExecutor(new EstablishCompany());
        Objects.requireNonNull(this.getCommand("playeradmin"))
                .setExecutor(new PlayerAdmin());
        Objects.requireNonNull(this.getCommand("cpay"))
                .setExecutor(new CompanyPay());

        // register events
        getServer().getPluginManager().registerEvents(new PlayerCard(), this);
        getServer().getPluginManager().registerEvents(new TopPlayerList(), this);
        if (rewardsEnabled) getServer().getPluginManager()
                .registerEvents(new AdvancementReward(), this);
        if (playerListEnabled) setUpAdvancedPlayerList();

        loadConfig();
        getBundleFromLangCode("Strings", language)
                .ifPresent(bundle -> {
                    strings = bundle;
                    prefix = getDefaultPrefix();
                });
        if (!setUpDatabase()) {
            getLogger().severe(strings.getString("error-db-connection-failed"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (autoEcoEnabled && this.isEnabled()) setUpAutoEconomy();
    }

    /**
     * Plugin shutdown logic
     */
    @Override
    public void onDisable() {
        try {
            database.close();
        } catch (SQLException ignored) { }
    }

    // all other settings
    public static double cardPrice = 2500;
    public static String cardItemLore = "Unique Player ID Card";
    public static String scoreboardSignText = "§8[§9TopPlayers§8]";

    public static double companyEstablishPrice = 3000;
    public static double punishmentAmount = 3000;
    public static int maxPunishments = 5;

    public static boolean autoEcoEnabled = true;
    public static int autoEcoTimeSeconds = 1200;
    public static double autoEcoDefaultThreshold = 1000;
    public static double autoEcoDefaultAmount = 250;

    public static boolean rewardsEnabled = true;
    public static double rewardRank1 = 10;
    public static double rewardRank2 = 25;
    public static double rewardRank3 = 50;
    public static List<String> advRank1 = AdvancementReward.getAdvancements(1);
    public static List<String> advRank2 = AdvancementReward.getAdvancements(2);
    public static List<String> advRank3 = AdvancementReward.getAdvancements(3);

    private static boolean playerListEnabled = true;
    private static int playerListUpdateSeconds = 300;
    public static String[] playerListHeader = {
            "§b§oA Minecraft Server                                   §r§a{playercount}§8/§a{maxplayers}",
            "§8--------------------------------------------"
    };
    public static String[] playerListFooter = {
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
    public static String genericPlayerEntry = "§8[ §f{playername} §8]";
    public static String memberPlayerEntry = "§8[ §a{playername} §8]";
    public static String vipPlayerEntry = "§8[ §b{playername} §8]";
    public static String adminPlayerEntry = "§8[ §c{playername} §8]";

    public static String genericPlayerLabel = "§fNone";
    public static String memberPlayerLabel = "§aMembers";
    public static String vipPlayerLabel = "§bVIP";
    public static String adminPlayerLabel = "§cAdmins";

    /**
     * Loads the values from the config file or generates them
     * if they don't exist yet
     */
    @SuppressWarnings("unchecked")
    private void loadConfig() {
        FileConfiguration conf = getConfig();
        // set up the default values
        conf.options().header("PlayerManagement Config File\n----------------------------\n");
        conf.addDefault("General.Language", language);
        conf.addDefault("General.OutputPrefix", prefix.substring(0, prefix.length() - 1));
        conf.addDefault("General.DatabasePath", databasePath);
        conf.addDefault("General.DateFormat", dateFormat);
        conf.addDefault("General.CardPrice", cardPrice);
        conf.addDefault("General.CardItemLore", cardItemLore);
        conf.addDefault("General.ScoreboardSignText", scoreboardSignText);
        conf.addDefault("Company.EstablishPrice", companyEstablishPrice);
        conf.addDefault("Punishments.Amount", punishmentAmount);
        conf.addDefault("Punishments.MaxBeforeBan", maxPunishments);
        conf.addDefault("AutomaticEconomy.Enabled", autoEcoEnabled);
        conf.addDefault("AutomaticEconomy.TimeInSeconds", autoEcoTimeSeconds);
        conf.addDefault("AutomaticEconomy.Threshold", autoEcoDefaultThreshold);
        conf.addDefault("AutomaticEconomy.MoneyAmount", autoEcoDefaultAmount);
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

        companyEstablishPrice = conf.getInt("Company.EstablishPrice");
        punishmentAmount = conf.getInt("Punishments.Price");
        maxPunishments = conf.getInt("Punishments.MaxBeforeBan");

        autoEcoEnabled = conf.getBoolean("AutomaticEconomy.Enabled");
        autoEcoTimeSeconds = conf.getInt("AutomaticEconomy.TimeInSeconds");
        autoEcoDefaultThreshold = conf.getDouble("AutomaticEconomy.Threshold");
        autoEcoDefaultAmount = conf.getDouble("AutomaticEconomy.MoneyAmount");

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
        saveConfig();
    }

    /**
     * Detects and enables the Vault plugin
     *
     * @return true if connecting to the plugin was successful
     */
    private boolean setUpEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> provider = getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (provider == null) return false;

        eco = provider.getProvider();
        return true;
    }

    /**
     * Sets up the database connection
     */
    private boolean setUpDatabase() {
        boolean success = true;
        boolean newDb = !new File(databasePath).exists();
        try {
            database = SharedDb.connect(databasePath);
        } catch (SQLException e) {
            success = false;
        }

        playerDb = new PlayerDb(database);
        jobDb = new JobDb(database);
        companyDb = new CompanyDb(database);
        transactionDb = new TransactionDb(database);

        players = new HashMap<>();
        jobs = new HashMap<>();
        companies = new HashMap<>();
        transactions = new ArrayList<>();

        if (newDb) // empty database
            try {
                SharedDb.createTables(database);
                // just insert a blank player, job and company using a bogus id
                jobDb.insert(new Job(4097, "N/A", "N/A"));
                companyDb.insert(new Company(4097, "N/A"));
                // BUG! CHECK! playerDb.insert(new ServerPlayer(4097, "N/A", "N/A"));
                getLogger().info(strings.getString("created-empty-db"));
            } catch (SQLException e) {
                getLogger().severe(strings.getString("error-creating-db") + e.getMessage());
                success = false;
            }

        try {
            jobs = jobDb.getAll();   // it has to be done in this exact order!
            companies = companyDb.getAll();
            transactions = transactionDb.getAll();
            players = playerDb.getAll();
            getLogger().info(strings.getString("player-db-loaded-successfully"));
        } catch (SQLException e) {
            getLogger().severe(strings.getString("error-reading-from-db") + e.getMessage());
            success = false;
        }
        return success;
    }

    /**
     * Sets up the automatic economy
     */
    private void setUpAutoEconomy() {
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler()
                .runTask(this, () -> {
                    Bukkit.getOnlinePlayers().forEach(player ->
                            PlayerRoutines.autoEconomyPlayer(player,
                                    autoEcoDefaultAmount,
                                    autoEcoDefaultThreshold));
                    companies.forEach((s, company) -> {
                        try {
                            companyDb.update(company);
                        } catch (SQLException e) {
                            Bukkit.getLogger().severe(prefix + ChatColor.GOLD
                                    + strings.getString("error-updating-playerdata")
                                    + ChatColor.RED + e.getMessage());
                        }
                    });
                }), 1, autoEcoTimeSeconds * 20);
    }

    /**
     * Sets up automatic player list updating
     */
    private void setUpAdvancedPlayerList() {
        getServer().getPluginManager().registerEvents(new EnhancedPlayerList(), this);
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler()
                        .runTask(this, () -> getServer().getOnlinePlayers()
                                .forEach(EnhancedPlayerList::updateList)),
                1, playerListUpdateSeconds);
    }

    /**
     * Returns the resource bundle with the matching name for this
     * language code
     *
     * @param baseName the full name of the bundle to retrieve
     * @param langCode a code in the language_country format
     *                 (ex. <code>en_US</code>)
     * @return the matching resource bundle or an empty optional
     * if not found
     */
    @SuppressWarnings("SameParameterValue")
    private static Optional<ResourceBundle> getBundleFromLangCode(String baseName, String langCode) {
        String[] locale = langCode.split("_");
        try {
            return Optional.of(ResourceBundle.getBundle(baseName, new Locale(locale[0]
                    .toLowerCase(), locale[1].toUpperCase())));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the plugin's localized default chat output prefix
     *
     * @return the formatted string
     */
    private static String getDefaultPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.BLUE
                + strings.getString("server") + ChatColor.DARK_GRAY + "] ";
    }

}
