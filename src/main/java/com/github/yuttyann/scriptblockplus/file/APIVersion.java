package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class APIVersion {

    private static final String[] FIELD_NAMES = { "apiVersion" };

    private Plugin plugin;
    private String apiVersion = null;

    public APIVersion(@NotNull Plugin plugin) {
        this.plugin = plugin;
        if (SBConfig.isSBPAPIVersion()) {
            String[] version = StringUtils.split(Utils.getServerVersion(), ".");
            this.apiVersion = version.length > 2 ? version[0] + "." + version[1] : Utils.getServerVersion();
        }
    }

    @NotNull
    public String getAPIVersion() {
        return apiVersion;
    }

    public void update() {
        PluginDescriptionFile description = plugin.getDescription();
        for (Field field : description.getClass().getDeclaredFields()) {
            if (StreamUtils.anyMatch(FIELD_NAMES, s -> s.equals(field.getName()))) {
                field.setAccessible(true);
                try {
                    field.set(description, apiVersion);
                } catch (IllegalAccessException e) {
                    break;
                }
            }
        }
        Utils.sendMessage("[ScriptBlockPlus] API version " + description.getAPIVersion());
    }
}