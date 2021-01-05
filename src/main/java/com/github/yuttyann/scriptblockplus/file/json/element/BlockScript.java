package com.github.yuttyann.scriptblockplus.file.json.element;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * ScriptBlockPlus BlockScript クラス
 * @author yuttyann44581
 */
public class BlockScript {

    @SerializedName(value = "scriptkey", alternate = { "scripttype" })
    private final ScriptKey scriptKey;

    @SerializedName("scripts")
    private final HashMap<String, ScriptParam> scripts = new HashMap<>();

    public BlockScript(@NotNull ScriptKey scriptKey) {
        this.scriptKey = scriptKey;
    }

    public boolean has(@NotNull Location location) {
        var fullCoords = BlockCoords.getFullCoords(location);
        var scriptParam = scripts.get(fullCoords);
        if (scriptParam == null) {
            return false;
        }
        return scriptParam.getAuthor().size() > 0;
    }

    public void remove(@NotNull Location location) {
        scripts.remove(BlockCoords.getFullCoords(location));
    }

    @NotNull
    public ScriptParam get(@NotNull Location location) {
        var fullCoords = BlockCoords.getFullCoords(location);
        var scriptParam = scripts.get(fullCoords);
        if (scriptParam == null) {
            scripts.put(fullCoords, scriptParam = new ScriptParam());
        }
        return scriptParam;
    }

    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @Override
    public int hashCode() {
        return scriptKey.hashCode();
    }
}