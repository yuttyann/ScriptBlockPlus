package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.github.yuttyann.scriptblockplus.file.config.ConfigKeys.getConfigData;
import static com.github.yuttyann.scriptblockplus.utils.StringUtils.replaceColor;

public class ConfigKey<T> {

    private final String key;

    private T def;

    public ConfigKey(@NotNull String key, @Nullable T def) {
        this.key = key;
        this.def = def;
    }

    @Nullable
    public T get() {
        return getConfigData(key).getValue(def);
    }

    @NotNull
    public String toString() {
        return String.valueOf(get());
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public List<String> toList() {
        T t = get();
        return t instanceof List ? (List<String>) t : new ArrayList<>();
    }

    @NotNull
    public List<String> toListAddColor() {
        List<String> list = toList();
        for (int i = 0; i < list.size(); i++) {
            list.set(i, replaceColor(list.get(i), true));
        }
        return list;
    }

    public int toInt() {
        return Integer.parseInt(String.valueOf(get()));
    }

    public boolean toBool() {
        return Boolean.parseBoolean(String.valueOf(get()));
    }

    public void send() {
        send(Bukkit.getConsoleSender());
    }

    public void send(@NotNull CommandSender sender) {
        Utils.sendColorMessage(sender, toString());
    }
}