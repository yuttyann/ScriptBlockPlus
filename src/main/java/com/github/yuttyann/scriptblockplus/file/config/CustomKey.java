package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomKey implements Cloneable {

    private final String key;

    private String value;
    private String def;
    private String[] keyNames = {};
    private boolean isClone = false;

    CustomKey(@NotNull String key, @Nullable String def) {
        this.key = key;
        this.def = def;
        this.value = def;
    }

    public CustomKey set() {
        CustomKey customKey = clone();
        customKey.value = ConfigKeys.getConfigData(customKey.key).getValue(customKey.def);
        return customKey;
    }

    public CustomKey setReplaceKeys(@NotNull String... keyNames) {
        this.keyNames = keyNames;
        return this;
    }

    @NotNull
    public CustomKey replace(@NotNull Object... replaces) {
        if (replaces.length != keyNames.length) {
            throw new IllegalArgumentException("Size are not equal.");
        }
        CustomKey customKey = set();
        for (int i = 0; i < replaces.length; i++) {
            customKey.value = StringUtils.replace(customKey.value, customKey.keyNames[i], replaces[i]);
        }
        return customKey;
    }

    @Override
    public String toString() {
        return isClone ? value : set().value;
    }

    @NotNull
    public String toStringAddColor() {
        return StringUtils.setColor(toString(), true);
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

    @Override
    public CustomKey clone() {
        CustomKey customKey = new CustomKey(key, def);
        customKey.value = value;
        customKey.keyNames = keyNames;
        customKey.isClone = true;
        return customKey;
    }
}