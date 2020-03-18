package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.github.yuttyann.scriptblockplus.file.config.ConfigKeys.getConfigData;
import static com.github.yuttyann.scriptblockplus.utils.StringUtils.replaceColor;

public class CustomText {

    private final String key;

    private String value;
    private String def;
    private String[] keyNames = {};

    CustomText(@NotNull String key, @Nullable String def) {
        this.key = key;
        this.def = def;
        this.value = def;
    }

    public CustomText set() {
        synchronized(this) {
            this.value = getConfigData(key).getValue(def);
        }
        return this;
    }

    @NotNull
    public CustomText setReplaceKeys(@NotNull String... keyNames) {
        this.keyNames = keyNames;
        return this;
    }

    @NotNull
    public CustomText replace(@NotNull Object... replaces) {
        if (replaces.length != keyNames.length) {
            throw new IllegalArgumentException("Size are not equal.");
        }
        if (Objects.equals(value, def)) {
            set();
        }
        for (int i = 0; i < replaces.length; i++) {
            this.value = StringUtils.replace(this.value, keyNames[i], String.valueOf(replaces[i]));
        }
        return this;
    }

    @NotNull
    public CustomText stripColor() {
        if (Objects.equals(value, def)) {
            set();
        }
        this.value = ChatColor.stripColor(value);
        return this;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @NotNull
    public String toString(boolean isReplaceColor) {
        String temp = isReplaceColor ?  replaceColor(value, true) : value;
        set();
        return temp;
    }

    public void send(boolean isReplaceColor) {
        send(Bukkit.getConsoleSender(), isReplaceColor);
    }

    public void send(@NotNull CommandSender sender, boolean isReplaceColor) {
        Utils.sendMessage(sender, toString(isReplaceColor));
    }

    public void console(boolean isReplaceColor) {
        if (SBConfig.CONSOLE_LOG.toBool()) {
            send(isReplaceColor);
        }
    }
}