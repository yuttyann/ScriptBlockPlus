package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.SBLoader;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonOptions;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 * ScriptBlockPlus BlockScriptJson クラス
 * @author yuttyann44581
 */
@JsonOptions(path = "json/blockscript", file = "{id}.json")
public class BlockScriptJson extends Json<BlockScript> {

    public BlockScriptJson(@NotNull ScriptType scriptType) {
        super(scriptType.type());
    }

    public static boolean has(@NotNull Location location, @NotNull ScriptType scriptType) {
        BlockScriptJson blockScriptJson = new BlockScriptJson(scriptType);
        return blockScriptJson.exists() && blockScriptJson.load().has(location);
    }

    public static boolean has(@NotNull Location location, @NotNull BlockScriptJson blockScriptJson) {
        return blockScriptJson.exists() && blockScriptJson.load().has(location);
    }

    @NotNull
    public ScriptType getScriptType() {
        return ScriptType.valueOf(id);
    }

    @Override
    @NotNull
    public BlockScript newInstance(@NotNull Object[] args) {
        return new BlockScript(getScriptType());
    }

    public static void convart(@NotNull ScriptType scriptType) {
        // YAML形式のファイルからデータを読み込むクラス
        SBLoader scriptLoader = new SBLoader(scriptType);
        if (!scriptLoader.getFile().exists()) {
            return;
        }

        // JSON
        Json<BlockScript> json = new BlockScriptJson(scriptType);
        BlockScript blockScript = json.load();
        scriptLoader.forEach(s -> {

            // 移行の為、パラメータを設定する
            List<UUID> author = s.getAuthors();
            if (author.size() == 0) {
                return;
            }
            ScriptParam scriptParam = blockScript.get(s.getLocation());
            scriptParam.setAuthor(new LinkedHashSet<>(s.getAuthors()));
            scriptParam.setScript(s.getScripts());
            scriptParam.setLastEdit(s.getLastEdit());
            scriptParam.setAmount(s.getAmount());
        });
        json.saveFile();

        // 移行完了後にファイルとディレクトリを削除する
        scriptLoader.getFile().delete();
        File parent = scriptLoader.getFile().getParentFile();
        if (parent.isDirectory() && parent.list().length == 0) {
            parent.delete();
        }
    }
}