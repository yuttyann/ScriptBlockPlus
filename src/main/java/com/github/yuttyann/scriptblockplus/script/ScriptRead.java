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
package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.event.ScriptReadEndEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptReadStartEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.Placeholder;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.Option.Result;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableBlockCoords;
import com.google.common.collect.Iterators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.yuttyann.scriptblockplus.script.option.Option.Result.*;

/**
 * ScriptBlockPlus ScriptRead クラス
 * @author yuttyann44581
 */
public class ScriptRead extends ScriptMap {

    /**
     * スクリプトの進行度
     */
    protected int index;

    /**
     * 結果を反転する場合は{@code true}
     */
    protected boolean invert;

    /**
     * オプション
     */
    protected Option option;

    /**
     * オプションの値
     */
    protected String value;

    /**
     * スクリプトの一覧
     */
    protected List<String> scripts;

    /**
     * 非同期の実行を許可する場合は{@code true}
     */
    protected final boolean async;

    /**
     * プレイヤー
     */
    protected final SBPlayer sbPlayer;

    /**
     * スクリプトキー
     */
    protected final ScriptKey scriptKey;

    /**
     * スクリプトの座標
     */
    protected final BlockCoords blockCoords;

    /**
     * スクリプトのデータファイル
     */
    protected final BlockScriptJson scriptJson;

    /**
     * コンストラクタ
     * @param sbPlayer - プレイヤー
     * @param blockCoords - 座標
     * @param scriptKey - スクリプトキー
     */
    public ScriptRead(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull ScriptKey scriptKey) {
        this(false, sbPlayer, blockCoords, scriptKey);
    }

    public ScriptRead(final boolean isAsync, @NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull ScriptKey scriptKey) {
        this.async = isAsync;
        this.sbPlayer = sbPlayer;
        this.scriptKey = scriptKey;
        this.blockCoords = new UnmodifiableBlockCoords(blockCoords);
        this.scriptJson = BlockScriptJson.get(scriptKey);   
    }

    /**
     * 非同期の実行を許可する場合は{@code true}を返します。
     * @return {@code boolean} - 非同期の実行を許可する場合は{@code true}
     */
    public final boolean isAsynchronous() {
        return async;
    }

    /**
     * プレイヤーを取得します。
     * @return {@link SBPlayer} - プレイヤー
     */
    @NotNull
    public SBPlayer getSBPlayer() {
        return sbPlayer;
    }

    /**
     * スクリプトキーを取得します。
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    /**
     * {@code BukkitAPI}の{@code org.bukkit.Location}を取得します。
     * @return {@link Location} - スクリプトの座標
     */
    @NotNull
    public Location getLocation() {
        return blockCoords.toLocation();
    }

    /**
     * スクリプトの座標を取得します。
     * @return {@link BlockCoords} - スクリプトの座標
     */
    @NotNull
    public BlockCoords getBlockCoords() {
        return blockCoords;
    }

    /**
     * スクリプトの一覧を取得します。
     * @return {@link List}&lt;{@link String}&gt; - スクリプトの一覧
     */
    @NotNull
    public List<String> getScripts() {
        return scripts;
    }

    /**
     * オプションの値を取得します。
     * @return {@link String} - オプションの値
     */
    @NotNull
    public String getOptionValue() {
        return value;
    }

    /**
     * スクリプトを何番目まで実行したのか取得します。
     * @return {@code int} - 進行度
     */
    public int getScriptIndex() {
        return index;
    }

    /**
     * オプションの結果を反転するのかどうか。
     * @return {@code boolean} - 反転する場合は{@code true}
     */
    public boolean isInverted() {
        return invert;
    }

    /**
     * スクリプトを実行します。
     * @param index - 開始位置
     * @return {@code boolean} - 実行に成功した場合は{@code true}
     */
    public synchronized boolean read(final int index) {
        if (!sbPlayer.isOnline()) {
            return false;
        }
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return false;
        }
        if (!sortScripts(scriptJson.load(blockCoords).getScripts())) {
            SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptKey).send(sbPlayer);
            SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(scriptKey, blockCoords).console();
            return false;
        }
        var result = FAILURE;
        var manager = Bukkit.getPluginManager();
        try {
            manager.callEvent(new ScriptReadStartEvent(randomId, this));
            return (result = perform(index)) == SUCCESS;
        } finally {
            manager.callEvent(new ScriptReadEndEvent(randomId, result, this));
            StreamUtils.ifAction(result != STOP, this::clear);
        }
    }

    /**
     * オプションを実行します。
     * @param index - 開始位置
     * @return {@code boolean} - 実行に成功した場合は{@code true}
     */
    @NotNull
    protected Result perform(final int index) {
        var player = getSBPlayer().toPlayer();
        for (this.index = index; this.index < scripts.size(); this.index++) {
            var script = scripts.get(this.index);
            this.invert = script.startsWith("!");
            this.option = OptionManager.newInstance(invert ? script = script.substring(1) : script);
            this.value = Placeholder.INSTANCE.replace(player, option.getValue(script));

            // オプションの実行
            var result = option.callOption(this);
            switch (result == STOP ? STOP : invert ? result == SUCCESS ? FAILURE : SUCCESS : result) {
                case SUCCESS:
                    EndProcessManager.forEach(e -> e.success(this));
                    SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(scriptKey, blockCoords).console();
                    break;
                case FAILURE:
                    EndProcessManager.forEach(e -> e.failed(this));
                    return FAILURE;
                case STOP:
                    return STOP;
                default:
                    return FAILURE;
            }
        }
        return SUCCESS;
    }

    /**
     * スクリプトの並び替えに成功した場合は{@code true}を返します。
     * @param scripts - スクリプト
     * @return {@code boolean} - スクリプトの並び替えに成功した場合は{@code true}
     */
    protected boolean sortScripts(@NotNull List<String> scripts) {
        try {
            var parse = new ArrayList<String>();
            for (int i = 0, l = scripts.size(); i < l; i++) {
                Iterators.addAll(parse, StringUtils.parseScript(scripts.get(i)).iterator());
            }
            if (SBConfig.SORT_SCRIPTS.getValue()) {
                OptionManager.sort(parse);
            }
            this.scripts = Collections.unmodifiableList(parse);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }
}