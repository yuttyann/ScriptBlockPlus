package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/*
　| 今後"api-version"の互換性が無くなった場合に実装する機能です。
　| 互換性を保つことが困難になった場合は最新以外を非対応としてこのクラスを削除します。
　|
　| <下記は実装予定の設定>
　|　　## ===== SBP-API-Versionの設定 ===== ##
　|　　# [true -> 有効 | false -> 無効]
　|　　# "plugin.yml"の"api-version"の設定を自動で行います。
　|　　# 1.13以降のサーバーにて正常な動作を行わせる為の設定です。
　|　　# ※無効にした場合は一度サーバーをリロードしてください。
　|　　# SBP-API-Version: true
　|　　
　|　　@author yuttyann44581
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

    public void set(@Nullable String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @NotNull
    public String get() {
        return apiVersion;
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
            break;
        }
    }
}