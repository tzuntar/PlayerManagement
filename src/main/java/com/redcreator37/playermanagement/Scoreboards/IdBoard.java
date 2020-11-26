package com.redcreator37.playermanagement.Scoreboards;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IdBoard {

    private final Scoreboard board;

    private final Team team;

    private final HashMap<String, Objective> objectives;

    private final List<String> playerNames;

    public IdBoard(ScoreboardManager sbManager, String teamName, List<String> playerNames) {
        this.playerNames = new ArrayList<>();
        board = sbManager.getNewScoreboard();
        team = board.registerNewTeam(teamName);
        team.setDisplayName(ChatColor.GREEN + "" + ChatColor.ITALIC + "INFO");
        objectives = new HashMap<String, Objective>() {{
            put("money", board.registerNewObjective("Money", "dummy",
                    ChatColor.GREEN + "Money"));
            put("job", board.registerNewObjective("Job", "dummy",
                    ChatColor.GOLD + "Job"));
        }};
        playerNames.forEach(this::registerPlayer);
        refreshData();
    }

    public void registerPlayer(String playerName) {
        team.addEntry(playerName);
        playerNames.add(playerName);
    }

    public void removePlayer(String playerName) {
        team.removeEntry(playerName);
        playerNames.remove(playerName);
    }

    public void refreshData() {
        playerNames.forEach(name -> {
            ServerPlayer pl = PlayerManagement.players.get(PlayerRoutines
                    .uuidFromUsername(PlayerManagement.players, name.trim()));
            if (pl == null) return;
            Objective money = objectives.get("money");
            money.setDisplaySlot(DisplaySlot.SIDEBAR);

            Player p = Bukkit.getPlayer(pl.getUsername());
            if (p == null) return;
            p.setScoreboard(board);
        });
    }

}
