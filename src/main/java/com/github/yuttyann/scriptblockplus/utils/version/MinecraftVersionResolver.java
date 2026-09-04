package com.github.yuttyann.scriptblockplus.utils.version;

import java.util.Objects;
import java.util.regex.Pattern;

import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** McVersion や NMS の初期化に依存せず、ゲームバージョンだけを取得します。 */
final class MinecraftVersionResolver {

    private static final String GAME_ID = "[0-9]+\\.[0-9]+(?:\\.[0-9]+)?(?:-(?:pre[0-9]+|rc[0-9]+|snapshot-[0-9]+))?";
    private static final Pattern GAME_VERSION = Pattern.compile(GAME_ID);
    private static final Pattern BUKKIT_VERSION = Pattern.compile("(" + GAME_ID + ")"
        + "(?:-R[0-9]+(?:\\.[0-9]+)*(?:-SNAPSHOT)?|\\.build\\.[0-9]+(?:-[A-Za-z][A-Za-z0-9._-]*)?)?");

    private MinecraftVersionResolver() { }

    @NotNull
    static Version resolve(@NotNull Server server) {
        Objects.requireNonNull(server, "Bukkit server has not been initialized");
        Class<?> buildInfoType = null;
        try {
            buildInfoType = Class.forName("io.papermc.paper.ServerBuildInfo", false, server.getClass().getClassLoader());
        } catch (ClassNotFoundException | LinkageError | SecurityException ex) {
            // Spigot や古い Paper には存在しないため、次の取得方法を使います。
        }
        return resolve(server, buildInfoType);
    }

    @NotNull
    static Version resolve(@NotNull Server server, @Nullable Class<?> buildInfoType) {
        var version = readPaperVersion(buildInfoType);
        if (version != null) {
            return parseGameVersion(version, "ServerBuildInfo.minecraftVersionId()");
        }
        version = readMinecraftVersion(server);
        if (version != null) {
            return parseGameVersion(version, "Server.getMinecraftVersion()");
        }
        return parseBukkitVersion(server.getBukkitVersion());
    }

    @Nullable
    private static String readPaperVersion(@Nullable Class<?> buildInfoType) {
        if (buildInfoType == null) return null;
        try {
            var info = buildInfoType.getMethod("buildInfo").invoke(null);
            if (info == null) return null;
            // 公開 API 型の Method を使い、実装クラスの可視性に依存させません。
            return nonEmptyString(buildInfoType.getMethod("minecraftVersionId").invoke(info));
        } catch (ReflectiveOperationException | LinkageError | SecurityException ex) {
            return null;
        }
    }

    @Nullable
    private static String readMinecraftVersion(@NotNull Server server) {
        try {
            return nonEmptyString(server.getClass().getMethod("getMinecraftVersion").invoke(server));
        } catch (ReflectiveOperationException | LinkageError | SecurityException ex) {
            return null;
        }
    }

    @Nullable
    private static String nonEmptyString(@Nullable Object value) {
        if (!(value instanceof String)) return null;
        var text = ((String) value).trim();
        return text.isEmpty() ? null : text;
    }

    @NotNull
    static Version parseBukkitVersion(@Nullable String version) {
        var text = nonEmptyString(version);
        var matcher = BUKKIT_VERSION.matcher(text == null ? "" : text);
        if (!matcher.matches()) {
            throw invalidVersion("Server.getBukkitVersion()", version);
        }
        // Paper のビルド番号・リリースチャネルは Minecraft の qualifier ではありません。
        return parseGameVersion(matcher.group(1), "Server.getBukkitVersion()");
    }

    @NotNull
    private static Version parseGameVersion(@NotNull String version, @NotNull String source) {
        if (!GAME_VERSION.matcher(version).matches()) {
            // 専用 API が未知のゲーム ID を返した場合、API バージョンで上書きして推測しません。
            throw invalidVersion(source, version);
        }
        try {
            return Version.of(version);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported Minecraft version from " + source + ": " + version, ex);
        }
    }

    @NotNull
    private static IllegalArgumentException invalidVersion(@NotNull String source, @Nullable String version) {
        return new IllegalArgumentException("Unsupported Minecraft version from " + source + ": " + version);
    }
}
