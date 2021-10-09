package de.spaceai.disvoice.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerScoreboard {

    private Player player;
    private Scoreboard scoreboard;
    private Objective objective;

    public PlayerScoreboard(Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective(player.getName(), player.getName(), player.getName());

        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName("§cDisVoice §7| §c" + this.player.getName());

        this.objective.getScore("§a ").setScore(10);
        this.objective.getScore("§7Currently Talking").setScore(9);
        this.objective.getScore("§7§o").setScore(8);
        this.objective.getScore("§a§b ").setScore(7);

        this.updateTeam("talk", "§7§o", "§cNobody");

        this.player.setScoreboard(this.scoreboard);
    }

    public void updateTeam(String teamName, String entry, String suffix) {
        Team team = (this.scoreboard.getTeam(teamName) != null) ? this.scoreboard.getTeam(teamName) :
                this.scoreboard.registerNewTeam(teamName);
        team.addEntry(entry);
        team.setSuffix(suffix);
    }

}
