package com.redcreator37.playermanagement.Scoreboards;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
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

    private final List<ServerPlayer> players;

    public IdBoard(ScoreboardManager sbManager, String teamName, List<ServerPlayer> players) {
        this.players = new ArrayList<>();
        board = sbManager.getNewScoreboard();
        team = board.registerNewTeam(teamName);
        team.setDisplayName(ChatColor.GREEN + "" + ChatColor.ITALIC + "INFO");
        objectives = new HashMap<String, Objective>() {{
            put("money", board.registerNewObjective("Money", "dummy",
                    ChatColor.GREEN + "Money"));
            put("job", board.registerNewObjective("Job", "dummy",
                    ChatColor.GOLD + "Job"));
        }};
        players.forEach(this::registerPlayer);
        refreshData();
    }

    public void registerPlayer(ServerPlayer player) {
        team.addEntry(player.getUsername());
        players.add(player);
    }

    public void removePlayer(ServerPlayer player) {
        team.removeEntry(player.getUsername());
        players.remove(player);
    }

    public void refreshData() {
        players.forEach(pl -> {
            ServerPlayer newData = PlayerManagement.players.byUuid(pl.getUuid());
            if (newData == null) return;
            Objective money = objectives.get("money");
            money.setDisplaySlot(DisplaySlot.SIDEBAR);

            Player p = Bukkit.getPlayer(newData.getUsername());
            if (p == null) return;
            p.setScoreboard(board);
        });
    }

}
