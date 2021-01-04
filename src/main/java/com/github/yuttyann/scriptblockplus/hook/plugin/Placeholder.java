package com.github.yuttyann.scriptblockplus.hook.plugin;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Placeholder クラス
 * @author yuttyann44581
 */
public final class Placeholder extends HookPlugin {

    public static final Placeholder INSTANCE = new Placeholder();

    @Override
    @NotNull
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public String set(@NotNull Player player, @NotNull String source) {
        var version = PlaceholderAPIPlugin.getInstance().getDescription().getVersion();
        if (Utils.isUpperVersion("2.8.8", version)) {
            source = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, source);
        } else {
            source = PlaceholderAPI.setPlaceholders(player, source);
        }
        return source;
    }

    @NotNull
    public String replace(@NotNull Player player, @NotNull String source) {
        source = StringUtils.replace(source, "<player>", player.getName());
        source = StringUtils.replace(source, "<world>", player.getWorld().getName());
        return has() ? set(player, source) : source;
    }
}