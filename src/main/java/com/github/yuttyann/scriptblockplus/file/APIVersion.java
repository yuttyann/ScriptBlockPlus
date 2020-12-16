package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * ScriptBlockPlus APIVersion クラス
 * @author yuttyann44581
 */
public final class APIVersion {

    private static final String[] FIELD_NAMES = { "apiVersion" };

    private static final String DEFAULT_API_VERSION;

    static {
        String[] version = StringUtils.split(Utils.getServerVersion(), ".");
        DEFAULT_API_VERSION = version.length > 2 ? version[0] + "." + version[1] : Utils.getServerVersion();
    }

    private Plugin plugin;
    private String apiVersion = DEFAULT_API_VERSION;

    public APIVersion(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public void update() {
        PluginDescriptionFile description = plugin.getDescription();
        for (Field field : description.getClass().getDeclaredFields()) {
            if (!StreamUtils.anyMatch(FIELD_NAMES, s -> s.equals(field.getName()))) {
                continue;
            }
            try {
                field.setAccessible(true);
                field.set(description, apiVersion);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}