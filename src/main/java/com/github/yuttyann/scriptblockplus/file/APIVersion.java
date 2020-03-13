package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.yaml.UTF8Config;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.base.Charsets;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import jdk.internal.util.xml.impl.Input;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class APIVersion {

    private static final String[] FIELD_NAMES = { "apiVersion" };

    private Plugin plugin;
    private String apiVersion;

    public APIVersion(@NotNull Plugin plugin) {
        this.plugin = plugin;
        String[] version = StringUtils.split(Utils.getServerVersion(), ".");
        this.apiVersion = version.length > 2 ? version[0] + "." + version[1] : Utils.getServerVersion();
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
    }
}