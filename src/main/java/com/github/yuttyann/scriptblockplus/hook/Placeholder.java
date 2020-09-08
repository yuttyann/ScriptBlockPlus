package com.github.yuttyann.scriptblockplus.hook;

import com.github.yuttyann.scriptblockplus.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Placeholder クラス
 * @author yuttyann44581
 */
public class Placeholder {

    public static final boolean HAS = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    public static final Placeholder INSTANCE = new Placeholder();

    private Placeholder() { }

    @NotNull
    public String set(@NotNull Player player, @NotNull String source) {
        String version = PlaceholderAPIPlugin.getInstance().getDescription().getVersion();
        if (Utils.isUpperVersion("2.8.8", version)) {
            source = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, source);
        } else {
            @SuppressWarnings("deprecation")
            String result = PlaceholderAPI.setPlaceholders(player, source);
            source = result;
        }
        return source;
    }
}