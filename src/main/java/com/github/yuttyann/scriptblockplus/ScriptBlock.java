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
package com.github.yuttyann.scriptblockplus;

import java.util.UUID;

import com.github.yuttyann.scriptblockplus.command.BaseCommand;
import com.github.yuttyann.scriptblockplus.command.ScriptBlockPlusCommand;
import com.github.yuttyann.scriptblockplus.enums.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.file.SBFiles;
import com.github.yuttyann.scriptblockplus.file.json.CacheJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.legacy.ConvertList;
import com.github.yuttyann.scriptblockplus.file.json.legacy.LegacyFormatJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.VaultEconomy;
import com.github.yuttyann.scriptblockplus.hook.plugin.VaultPermission;
import com.github.yuttyann.scriptblockplus.listener.*;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.item.action.BlockSelector;
import com.github.yuttyann.scriptblockplus.item.action.ScriptEditor;
import com.github.yuttyann.scriptblockplus.item.action.ScriptViewer;
import com.github.yuttyann.scriptblockplus.item.action.ScriptManager;
import com.github.yuttyann.scriptblockplus.item.action.TickRunnable;
import com.github.yuttyann.scriptblockplus.item.gui.CustomGUI;
import com.github.yuttyann.scriptblockplus.item.gui.UserWindow;
import com.github.yuttyann.scriptblockplus.item.gui.custom.SearchGUI;
import com.github.yuttyann.scriptblockplus.item.gui.custom.SettingGUI;
import com.github.yuttyann.scriptblockplus.item.gui.custom.ToolBoxGUI;
import com.github.yuttyann.scriptblockplus.listener.trigger.BreakTrigger;
import com.github.yuttyann.scriptblockplus.listener.trigger.HitTrigger;
import com.github.yuttyann.scriptblockplus.listener.trigger.InteractTrigger;
import com.github.yuttyann.scriptblockplus.listener.trigger.WalkTrigger;
import com.github.yuttyann.scriptblockplus.manager.APIManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptBlock メインクラス
 * @author yuttyann44581
 */
public class ScriptBlock extends JavaPlugin {

    private static Scheduler scheduler;
    private static ScriptBlock scriptBlock;

    @Override
    public void onEnable() {
        // 1.9未満のバージョンだった場合はプラグインを無効化
        if (!Utils.isCBXXXorLater("1.9")) {
            getLogger().warning("Unsupported Version: " + Utils.getServerVersion());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 一部のリフレクションをキャッシュする。
        if (NetMinecraft.hasNMS()) {
            try {
                NMSHelper.build();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } else {
            // NMSが見つからなかった場合警告
            getLogger().warning(NetMinecraft.WARNING_TEXT);
        }

        // 全てのファイルを読み込む。
        SBFiles.reload();

        // Vaultが導入されているのか確認
        if (VaultEconomy.INSTANCE.has()) {
            VaultEconomy.INSTANCE.setupEconomy();
            VaultPermission.INSTANCE.setupPermission();
        }

        // 旧ScriptBlockが導入されていた場合は無効化
        var plugin = getServer().getPluginManager().getPlugin("ScriptBlock");
        if (plugin != null) {
            getServer().getPluginManager().disablePlugin(plugin);
        }

        // スクリプトの形式を".yml"から".json"へ移行
        ScriptKey.iterable().forEach(BlockScriptJson::convert);

        // 古い形式のJSONファイルを最新の物へ移行する。
        LegacyFormatJson.convert(ConvertList.create(this, "json"));

        // キャッシュを生成(設定有効時のみ)
        CacheJson.loading();

        // ログイン中のプレイヤーの設定をオンラインへ変更
        Bukkit.getOnlinePlayers().forEach(p -> getSBPlayer(p).setOnline(true));

        // リスナーの登録
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(), this);

        // トリガーの登録
        TriggerListener.register(new HitTrigger(this));
        TriggerListener.register(new WalkTrigger(this));
        TriggerListener.register(new BreakTrigger(this));
        TriggerListener.register(new InteractTrigger(this));

        // GUI の登録
        CustomGUI.register(new SearchGUI());
        CustomGUI.register(new SettingGUI());
        CustomGUI.register(new ToolBoxGUI());

        // アイテムアクションの登録
        ItemAction.register(new ScriptEditor());
        ItemAction.register(new ScriptViewer());
        ItemAction.register(new ScriptManager());
        ItemAction.register(new BlockSelector());
    
        // オプションの更新
        OptionManager.update();

        // コマンドの登録
        BaseCommand.register("scriptblockplus", new ScriptBlockPlusCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            UserWindow.closeAll();
            ScriptViewer.PLAYERS.clear();
            TickRunnable.GLOW_ENTITY.removeAll();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@link ScriptBlock}のAPIを取得します。
     * @return {@link ScriptBlockAPI} - {@link ScriptBlock}のAPI
     */
    @NotNull
    public ScriptBlockAPI getAPI() {
        return new APIManager();
    }

    /**
     * {@link Scheduler}のインスタンスを取得します。
     * @return {@link Scheduler} - インスタンス
     */
    @NotNull
    public static Scheduler getScheduler() {
        return scheduler == null ? scheduler = new Scheduler(getInstance()) : scheduler;
    }

    /**
     * {@link ScriptBlock}のインスタンスを取得します。
     * @return {@link ScriptBlock} - インスタンス
     */
    @NotNull
    public static ScriptBlock getInstance() {
        return scriptBlock == null ? scriptBlock = getPlugin(ScriptBlock.class) : scriptBlock;
    }

    /**
     * {@link SBPlayer}を取得します。
     * @param player - {@link Bukkit}の{@link OfflinePlayer}
     * @return {@link SBPlayer} - プレイヤー
     */
    @NotNull
    public static SBPlayer getSBPlayer(@NotNull OfflinePlayer player) {
        return getSBPlayer(player.getUniqueId());
    }

    /**
     * {@link SBPlayer}を取得します。
     * @param uuid - プレイヤーの{@link UUID}
     * @return {@link SBPlayer} - プレイヤー
     */
    @NotNull
    public static SBPlayer getSBPlayer(@NotNull UUID uuid) {
        return BaseSBPlayer.fromUUID(uuid);
    }
}