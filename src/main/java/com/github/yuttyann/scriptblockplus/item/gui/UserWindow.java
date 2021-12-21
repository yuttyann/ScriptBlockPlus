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
package com.github.yuttyann.scriptblockplus.item.gui;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.hook.nms.AnvilGUI;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus UserWindow クラス
 * @author yuttyann44581
 */
public class UserWindow {

    public static final String KEY_WINDOW = Utils.randomUUID();

    protected final SBPlayer sbPlayer;
    protected final CustomGUI customGUI;
    protected final GUIItem[] items;

    protected boolean open, update;
    protected Inventory inventory;

    /**
     * コンストラクタ
     * @param sbPlayer - プレイヤー
     * @param customGUI - GUIの骨組み
     */
    public UserWindow(@NotNull SBPlayer sbPlayer, @NotNull CustomGUI customGUI) {
        this.sbPlayer = sbPlayer;
        this.customGUI = customGUI;
        this.items = copyItems(customGUI.getContents());
        reload();
    }

    /**
     * 全てのプレイヤーのウィンドウを閉じます。
     */
    public static void closeAll() {
        for (var player : Bukkit.getOnlinePlayers()) {
            var objectMap = SBPlayer.fromPlayer(player).getObjectMap();
            if (objectMap.has(KEY_WINDOW)) {
                ((UserWindow) objectMap.get(KEY_WINDOW)).close();
            } else if (objectMap.getBoolean(AnvilGUI.KEY_OPEN)) {
                player.closeInventory();
            }
        }
    }

    /**
     * ウィンドウを閉じます。
     */
    public void close() {
        if (!update && sbPlayer.isOnline()) {
            this.open = false;
            customGUI.onClosed(this);
            sbPlayer.getObjectMap().put(KEY_WINDOW, null);
            sbPlayer.getPlayer().closeInventory();
        }
    }

    /**
     * ウィンドウを開きます。
     */
    public void open() {
        if (sbPlayer.isOnline()) {
            this.open = true;
            customGUI.onOpened(this);
            sbPlayer.getObjectMap().put(KEY_WINDOW, this);
            sbPlayer.getPlayer().openInventory(getInventory());
        }
    }

    /**
     * ウィンドウを更新します。
     */
    public void update() {
        this.update = true;
        try {
            shift(this);
        } finally {
            this.update = false;
        }
    }

    /**
     * 指定したウィンドウに変更します。
     * @param window
     */
    public void shift(@NotNull UserWindow window) {
        if (!sbPlayer.isOnline()) {
            return;
        }
        try {
            sbPlayer.getPlayer().closeInventory();
        } finally {
            ScriptBlock.getScheduler().run(window::open);
        }
    }

    /**
     * ウィンドウを更新します。
     */
    public void reload() {
        try {
            this.inventory = customGUI.isCancelled() ? createInventory() : null;  
        } finally {
            customGUI.onLoaded(this);
        }
    }

    /**
     * ウィンドウを開いている場合は{@code true}を返します。
     * @return {@code boolean} - ウィンドウを開いている場合は{@code true}
     */
    public boolean isOpened() {
        return open;
    }

    /**
     * 骨組みとなる{@code CustomGUI}を取得します。
     * @return {@code CustomGUI} - GUI
     */
    @NotNull
    public CustomGUI getCustomGUI() {
        return customGUI;
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
     * ウィンドウのアイテムの配列を取得します。
     * @return {@link GUIItem}[] - アイテムの配列
     */
    @NotNull
    public GUIItem[] getContents() {
        return items;
    }

    /**
     * ウィンドウにアイテムを設定します。
     * @param slot - スロット
     * @param item - アイテム
     * @return {@link UserWindow}
     */
    @NotNull
    public UserWindow setItem(int slot, @Nullable GUIItem item) {
        if (slot < items.length) {
            items[slot] = item;
            if (inventory != null) {
                inventory.setItem(slot, item == null ? null : item.toBukkit());
            }
        }
        return this;
    }

    /**
     * ウィンドウのアイテムを取得します。
     * @param slot - スロット
     * @return {@link GUIItem} - アイテム
     */
    @Nullable
    public GUIItem getItem(int slot) {
        return slot < items.length ? items[slot] : null;
    }

    /**
     * ウィンドウのインベントリを取得します。
     * @return {@link Inventory} - インベントリ
     */
    @NotNull
    public Inventory getInventory() {
        return inventory == null ? createInventory() : inventory;
    }

    /**
     * インベントリを作成します。
     * @return {@link Inventory} - インベントリ
     */
    @NotNull
    protected Inventory createInventory() {
        var inventory = Bukkit.createInventory(sbPlayer.getPlayer(), items.length, customGUI.getTitle());
        for (int i = 0; i < items.length; i++) {
            var item = getItem(i);
            if (item != null) {
                inventory.setItem(i, item.toBukkit());
            }
        }
        return inventory;
    }

    /**
     * アイテムの配列の内容をコピーして返します。
     * @param items - アイテムの配列
     * @return {@link GUIItem}[] - アイテムの配列のコピー
     */
    @NotNull
    protected final GUIItem[] copyItems(@NotNull GUIItem[] items) {
        var copy = new GUIItem[items.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = items[i] == null ? null : items[i].clone();
        }
        return copy;
    }
}