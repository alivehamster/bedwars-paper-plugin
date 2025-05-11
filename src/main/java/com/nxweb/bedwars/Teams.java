package com.nxweb.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Teams {
    public NamedTextColor[] aliveTeams;
    
    private final Scoreboard scoreboard;
    private final Map<UUID, Team> playerTeams = new HashMap<>();
    
    // Array of colors to cycle through for player teams
    private final NamedTextColor[] teamColors = {
            NamedTextColor.RED,
            NamedTextColor.BLUE,
            NamedTextColor.GREEN,
            NamedTextColor.YELLOW,
            NamedTextColor.AQUA,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.WHITE,
            NamedTextColor.BLACK,
    };

    // Map to match bed colors to team colors
    private final Map<String, NamedTextColor> bedColorToTeamColor = new HashMap<>();

    private int colorIndex = 0;

    public Teams() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        // Use a new scoreboard instead of the main scoreboard
        scoreboard = manager.getMainScoreboard();
        
        // Initialize bed color to team color mapping
        bedColorToTeamColor.put("RED", NamedTextColor.RED);
        bedColorToTeamColor.put("BLUE", NamedTextColor.BLUE);
        bedColorToTeamColor.put("Lime", NamedTextColor.GREEN);
        bedColorToTeamColor.put("YELLOW", NamedTextColor.YELLOW);
        bedColorToTeamColor.put("LIGHT_BLUE", NamedTextColor.AQUA);
        bedColorToTeamColor.put("PURPLE", NamedTextColor.DARK_PURPLE);
        bedColorToTeamColor.put("WHITE", NamedTextColor.WHITE);
        bedColorToTeamColor.put("BLACK", NamedTextColor.BLACK);

    }

    // Get team color from bed color string
    public NamedTextColor getTeamColorFromBedColor(String bedColor) {
        return bedColorToTeamColor.get(bedColor);
    }

    public void createPlayerTeam(Player player) {

        // Clean up any existing team for this player
        removePlayerTeam(player);
        
        // Create a unique team name for the player using part of UUID for stability
        String teamName = "bw_" + player.getUniqueId().toString().substring(0, 8);
        
        // Check if team already exists and unregister it
        Team existingTeam = scoreboard.getTeam(teamName);
        if (existingTeam != null) {
            existingTeam.unregister();
        }
        
        // Create a new team for the player
        Team team = scoreboard.registerNewTeam(teamName);
        
        // Assign the next color in rotation
        NamedTextColor color = teamColors[colorIndex];
        colorIndex = (colorIndex + 1) % teamColors.length;
        
        // Apply color using the Adventure API
        team.prefix(Component.text().color(color).build());
        team.color(color);
        team.addEntry(player.getName());
        
        // Ensure player sees the scoreboard
        player.setScoreboard(scoreboard);
        
        // Store the team
        playerTeams.put(player.getUniqueId(), team);
    }

    public void removePlayerTeam(Player player) {
        Team team = playerTeams.get(player.getUniqueId());
        if (team != null) {
            team.unregister();
            playerTeams.remove(player.getUniqueId());
        }
    }

    public TextColor getPlayerTeamColor(Player player) {
        Team team = playerTeams.get(player.getUniqueId());
        if (team != null) {
            return team.color();
        }
        return null;
    }

    public void createTeamsForAllPlayers() {
        // Clear and reinitialize aliveTeams
        aliveTeams = teamColors.clone();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            createPlayerTeam(player);
        }
    }

    public void deleteAllTeams() {
        // First unregister each team in playerTeams
        for (Team team : playerTeams.values()) {
            if (team != null) {
                team.unregister();
            }
        }

        // Clear the playerTeams map
        playerTeams.clear();

        // Reset color index for future team creation
        colorIndex = 0;
    }
}
