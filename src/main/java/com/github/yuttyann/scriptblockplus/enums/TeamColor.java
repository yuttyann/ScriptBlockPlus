package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;

public enum TeamColor {
    BLACK(ChatColor.BLACK),
    DARK_BLUE(ChatColor.DARK_BLUE),
    DARK_GREEN(ChatColor.DARK_GREEN),
    DARK_AQUA(ChatColor.DARK_AQUA),
    DARK_RED(ChatColor.DARK_RED),
    DARK_PURPLE(ChatColor.DARK_PURPLE),
    GOLD(ChatColor.GOLD),
    GRAY(ChatColor.GRAY),
    DARK_GRAY(ChatColor.DARK_GRAY),
    BLUE(ChatColor.BLUE),
    GREEN(ChatColor.GREEN),
    AQUA(ChatColor.AQUA),
    RED(ChatColor.RED),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE),
    YELLOW(ChatColor.YELLOW),
    WHITE(ChatColor.WHITE);

    private static final String PREFIX = "SBP_";
    private static final Scoreboard SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();

    private final ChatColor chatColor;

    TeamColor(@NotNull ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    @NotNull
    public String getName() {
        return PREFIX + chatColor.name();
    }

    @NotNull
    public Team getTeam() {
        var name = getName();
        var team = SCOREBOARD.getTeam(name);
        if (team == null) {
            team = SCOREBOARD.registerNewTeam(name);
            if (Utils.isCBXXXorLater("1.12")) {
                team.setColor(chatColor);
            }
            team.setPrefix(chatColor.toString());
            team.setSuffix(chatColor.toString());
            team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        }
        return team;
    }
}