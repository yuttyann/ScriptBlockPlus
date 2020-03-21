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

public class CustomKey {

    private final String key;

    private String value;
    private String def;
    private String[] keyNames = {};

    CustomKey(@NotNull String key, @Nullable String def) {
        this.key = key;
        this.def = def;
        this.value = def;
    }

    @NotNull
    public CustomKey set() {
        synchronized(this) {
            this.value = ConfigKeys.getConfigData(key).getValue(def);
        }
        return this;
    }

    @NotNull
    public CustomKey setReplaceKeys(@NotNull String... keyNames) {
        this.keyNames = keyNames;
        return this;
    }

    @NotNull
    public CustomKey replace(@NotNull Object... replaces) {
        if (replaces.length != keyNames.length) {
            throw new IllegalArgumentException("Size are not equal.");
        }
        if (Objects.equals(value, def)) {
            set();
        }
        for (int i = 0; i < replaces.length; i++) {
            this.value = StringUtils.replace(this.value, keyNames[i], replaces[i]);
        }
        return this;
    }

    @NotNull
    public CustomKey stripColor() {
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
        String temp = isReplaceColor ?  StringUtils.setColor(value, true) : value;
        set();
        return temp;
    }

    public void send() {
        send(Bukkit.getConsoleSender());
    }

    public void send(@NotNull CommandSender sender) {
        Utils.sendColorMessage(sender, toString());
    }

    public void console() {
        if (SBConfig.CONSOLE_LOG.toBool()) {
            send();
        }
    }
}