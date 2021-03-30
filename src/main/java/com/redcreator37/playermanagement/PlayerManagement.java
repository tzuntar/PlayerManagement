package com.redcreator37.playermanagement;

import com.earth2me.essentials.Essentials;
import com.redcreator37.playermanagement.Commands.PlayerCommand;
import com.redcreator37.playermanagement.Commands.PlayerCommands.CompanyManagement;
import com.redcreator37.playermanagement.Commands.PlayerCommands.CompanyPay;
import com.redcreator37.playermanagement.Commands.PlayerCommands.DeleteId;
import com.redcreator37.playermanagement.Commands.PlayerCommands.EstablishCompany;
import com.redcreator37.playermanagement.Commands.PlayerCommands.GetId;
import com.redcreator37.playermanagement.Commands.PlayerCommands.GetJob;
import com.redcreator37.playermanagement.Commands.PlayerCommands.JobAdmin;
import com.redcreator37.playermanagement.Commands.PlayerCommands.LowerRank;
import com.redcreator37.playermanagement.Commands.PlayerCommands.PlayerAdmin;
import com.redcreator37.playermanagement.Commands.PlayerCommands.RegisterId;
import com.redcreator37.playermanagement.Commands.PlayerCommands.SetCompany;
import com.redcreator37.playermanagement.Commands.PlayerCommands.SetJob;
import com.redcreator37.playermanagement.Commands.PlayerCommands.SetNotes;
import com.redcreator37.playermanagement.Containers.PlayerDataContainer;
import com.redcreator37.playermanagement.DataModels.Company;
import com.redcreator37.playermanagement.DataModels.Job;
import com.redcreator37.playermanagement.DataModels.Transaction;
import com.redcreator37.playermanagement.Database.CompanyDb;
import com.redcreator37.playermanagement.Database.JobDb;
import com.redcreator37.playermanagement.Database.PlayerDb;
import com.redcreator37.playermanagement.Database.SharedDb;
import com.redcreator37.playermanagement.Database.TransactionDb;
import com.redcreator37.playermanagement.IdHandling.PlayerCard;
import com.redcreator37.playermanagement.Scoreboards.IdBoard;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A Minecraft Spigot Server plugin that extends native player data
 * handling capabilities and provides realistic economy management
 *
 * @author RedCreator37
 */
public final class PlayerManagement extends JavaPlugin {

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
     * Contains all user-settable preferences of the currently
     * running instance of this plugin
     */
    public static PreferencesHolder prefs;
    /**
     * Contains data for all player on the server
     */
    public static PlayerDataContainer players = null;
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
     * Provides in-game economy-related features
     */
    public static EconomyProvider economyProvider = null;
    /**
     * The global scoreboard which displays basic data for
     * all players
     */
    public static IdBoard globalDataBoard;
    /**
     * The database connection to use for all operations
     */
    private static Connection database = null;

    /**
     * Plugin startup logic
     */
    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("Essentials")) {
            getLogger().severe(Localization.lc("error-essentials-not-detected"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setUpEconomy()) {  // try to detect and enable Vault
            getLogger().severe(Localization.lc("error-vault-not-detected"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // register commands
        HashMap<String, PlayerCommand> commands = new HashMap<String, PlayerCommand>() {{
            put("registerid", new RegisterId());
            put("getid", new GetId());
            put("deleteid", new DeleteId());
            put("setjob", new SetJob());
            put("setcompany", new SetCompany());
            put("setnotes", new SetNotes());
            put("job", new GetJob());
            put("jobadmin", new JobAdmin());
            put("punish", new LowerRank());
            put("company", new CompanyManagement());
            put("establish", new EstablishCompany());
            put("playeradmin", new PlayerAdmin());
            put("cpay", new CompanyPay());
        }};
        commands.forEach(this::registerCommand);

        // register events
        getServer().getPluginManager().registerEvents(new PlayerCard(), this);
        getServer().getPluginManager().registerEvents(new TopPlayerList(), this);

        // load the preferences and initialize other events
        prefs = new PreferencesHolder();
        prefs.loadConfig(getConfig());
        saveConfig();

        if (prefs.rewardsEnabled) getServer().getPluginManager()
                .registerEvents(new AdvancementReward(), this);
        if (prefs.playerListEnabled) enablePlayerList();
        if (!Localization.changeLanguage("Strings", prefs.language))
            getLogger().warning(MessageFormat.format(Localization
                    .lc("switching-lang-failed"), prefs.language));
        if (!initializeDatabase()) {
            getLogger().severe(Localization.lc("error-db-connection-failed"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!this.isEnabled()) return;  // safety check to prevent exceptions if disabled
        if (prefs.autoEcoEnabled) enableAutoEconomy();

        if (prefs.experimentalFeatures) {
            globalDataBoard = new IdBoard(Objects.requireNonNull(Bukkit.getScoreboardManager()),
                    "Players", new ArrayList<>(players.getPlayers().values()));
            enableScoreboards();
        }
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

    /**
     * Registers this {@link PlayerCommand} into the plugin
     *
     * @param name    the name of the command by which it can be invoked
     * @param command the {@link PlayerCommand} object which represents it
     * @param <C>     the command's class
     */
    private <C extends PlayerCommand> void registerCommand(String name, C command) {
        Objects.requireNonNull(this.getCommand(name)).setExecutor(command);
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
    private boolean initializeDatabase() {
        boolean success = true;
        boolean newDb = !new File(prefs.databasePath).exists();
        try {
            database = SharedDb.connect(prefs.databasePath);
        } catch (SQLException e) {
            success = false;
        }

        playerDb = new PlayerDb(database);
        jobDb = new JobDb(database);
        companyDb = new CompanyDb(database);
        transactionDb = new TransactionDb(database);

        if (newDb) // empty database
            try {
                SharedDb.createTables(database, PlayerManagement.class
                        .getClassLoader().getResourceAsStream("GenerateDb.sql"));
                // just insert a blank player, job and company using a bogus id
                jobDb.insert(new Job(4097, "N/A", "N/A"));
                companyDb.insert(new Company(4097, "N/A"));
                getLogger().info(Localization.lc("created-empty-db"));
            } catch (SQLException | IOException e) {
                getLogger().severe(Localization.lc("error-creating-db") + e.getMessage());
                success = false;
            }

        try {
            jobs = jobDb.getAll();   // it has to be done in this exact order!
            companies = companyDb.getAll();
            transactions = transactionDb.getAll();
            players = new PlayerDataContainer(playerDb.getAll());
            getLogger().info(Localization.lc("player-db-loaded-successfully"));
        } catch (SQLException e) {
            getLogger().severe(Localization.lc("error-reading-from-db") + e.getMessage());
            success = false;
        }
        return success;
    }

    /**
     * Sets up the automatic economy
     */
    private void enableAutoEconomy() {
        economyProvider = new EconomyProvider(eco, ess, prefs.minimalWage);
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler()
                .runTask(this, () -> {
                    Bukkit.getOnlinePlayers().forEach(economyProvider::globalPayWage);
                    companies.forEach((s, company) -> {
                        try {
                            companyDb.update(company);
                        } catch (SQLException e) {
                            Bukkit.getLogger().severe(prefs.prefix + ChatColor.GOLD
                                    + Localization.lc("error-updating-playerdata")
                                    + ChatColor.RED + e.getMessage());
                        }
                    });
                }), 1, prefs.autoEcoTimeSeconds * 20L);
    }

    /**
     * Sets up automatic player list updating
     */
    private void enablePlayerList() {
        getServer().getPluginManager().registerEvents(new EnhancedPlayerList(), this);
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler()
                        .runTask(this, () -> getServer().getOnlinePlayers()
                                .forEach(EnhancedPlayerList::updateList)),
                1, prefs.playerListUpdateSeconds);
    }

    /**
     * Sets up updating of the data scoreboard
     */
    private void enableScoreboards() {
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler()
                        .runTask(this, () -> globalDataBoard.refreshData()),
                1, prefs.playerListUpdateSeconds);
    }

}
