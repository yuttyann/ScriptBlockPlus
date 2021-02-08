/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.utils;

import com.google.common.base.Charsets;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ScriptBlockPlus FileUtils クラス
 * @author yuttyann44581
 */
public final class FileUtils {

    @Nullable
    public static InputStream getResource(@NotNull Plugin plugin, @NotNull String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }
        if (File.separatorChar != '/') {
            filePath = filePath.replace(File.separatorChar, '/');
        }
        try {
            var url = plugin.getClass().getClassLoader().getResource(filePath);
            if (url == null) {
                return null;
            }
            var connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public static void copyFileFromPlugin(@NotNull Plugin plugin, @NotNull File targetFile, @NotNull String sourceFilePath) throws IOException {
        if (StringUtils.isEmpty(sourceFilePath)) {
            return;
        }
        var parent = targetFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        var resource = getResource(plugin, sourceFilePath);
        if (resource == null) {
            return;
        }
        try (
            var reader = new BufferedReader(new InputStreamReader(resource, Charsets.UTF_8));
            var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), Charsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        } finally {
            resource.close();
        }
    }

    public static void downloadFile(@NotNull String url, @NotNull File file) throws IOException {
        var parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (var is = getWebFile(url); var fos = new FileOutputStream(file)) {
            if (is == null) {
                return;
            }
            var bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) != -1) {
                fos.write(bytes, 0, length);
            }
        }
    }

    @Nullable
    public static InputStream getWebFile(@NotNull String url) throws IOException {
        var connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        int httpStatusCode = connection.getResponseCode();
        if (httpStatusCode == HttpURLConnection.HTTP_OK) {
            return connection.getInputStream();
        }
        connection.disconnect();
        return null;
    }

    public static boolean isEmpty(@NotNull File file) {
        var files = file.list();
        return files == null || files.length == 0;
    }
}