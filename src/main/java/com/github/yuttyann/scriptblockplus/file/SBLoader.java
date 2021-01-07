package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.YamlConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus ScriptLoader クラス
 * @author yuttyann44581
 */
public final class SBLoader {

    private static final String KEY_AUTHOR = ".Author";
    private static final String KEY_AMOUNT = ".Amount";
    private static final String KEY_SCRIPTS = ".Scripts";
    private static final String KEY_LASTEDIT = ".LastEdit";

    private String path;
    private Location location;
    private YamlConfig scriptFile;

    public SBLoader(@NotNull ScriptKey scriptKey) {
        var filePath = "scripts" + SBFiles.S + scriptKey.getName() + ".yml";
        this.scriptFile = YamlConfig.load(ScriptBlock.getInstance(), filePath, false);
    }

    public void forEach(@NotNull Consumer<SBLoader> action) {
        for (var name : scriptFile.getKeys()) {
            var world = Utils.getWorld(name);
            for (var xyz : scriptFile.getKeys(name)) {
                action.accept(createPath(BlockCoords.fromString(world, xyz)));
            }
        }
    }

    @NotNull
    public File getFile() {
        return scriptFile.getFile();
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public List<UUID> getAuthors() {
        var author = scriptFile.getString(path + KEY_AUTHOR, "");
        if (StringUtils.isEmpty(author)) {
            return new ArrayList<>();
        }
        var uuids = new ArrayList<UUID>();
        StreamUtils.forEach(StringUtils.split(author, ','), s -> uuids.add(UUID.fromString(s.trim())));
        return uuids;
    }

    @NotNull
    public List<String> getScripts() {
        return scriptFile.getStringList(path + KEY_SCRIPTS);
    }

    @NotNull
    public String getLastEdit() {
        return scriptFile.getString(path + KEY_LASTEDIT, "null");
    }

    public int getAmount() {
        return scriptFile.getInt(path + KEY_AMOUNT, -1);
    }

    @NotNull
    private SBLoader createPath(@NotNull Location location) {
        this.location = location;
        this.path = location.getWorld().getName() + "." + BlockCoords.getCoords(location);
        return this;
    }
}