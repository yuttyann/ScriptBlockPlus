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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.ArrayUtils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus CustomGUI クラス
 * @author yuttyann44581
 */
public abstract class CustomGUI {

    private static final Map<Class<? extends CustomGUI>, CustomGUI> GUIS = new HashMap<>();

    private final Supplier<String> title;

    private final UUID uuid;
    private final String key;
    private final GUIItem[] items;
    private final boolean cancelled;

    private Sound sound;
    private float volume, pitch;

    /**
     * コンストラクタ
     * @param title - GUIのタイトル
     * @param size - GUIのサイズ
     * @param cancelled - アイテムの移動をキャンセルする場合は{@code true}
    */
    protected CustomGUI(@NotNull Supplier<String> title, @NotNull int size, final boolean cancelled) {
        this.title = title;
        this.uuid = UUID.randomUUID();
        this.key = uuid.toString();
        this.items = new GUIItem[size < 0 ? size * -1 : size == 0 ? 9 : size > 6 ? 54 : size * 9];
        this.cancelled = cancelled;
    }

    /**
     * 全てのGUIを更新します。
     */
    public static void reload() {
        for (var player : Bukkit.getOnlinePlayers()) {
            var sbPlayer = SBPlayer.fromPlayer(player);
            for (var customGUI : GUIS.values()) {
                if (customGUI.hasUserWindow(sbPlayer)) {
                    customGUI.getUserWindow(sbPlayer).reload();
                }
            }
        }
    }

    /**
     * GUIを登録します。
     * @param customGUI - GUI
     */
    public static void register(@NotNull CustomGUI customGUI) {
        GUIS.put(customGUI.getClass(), customGUI);
    }

    /**
     * プレイヤーのウィンドウ取得します。
     * @param guiClass - 対象のGUI
     * @param player - プレイヤー
     * @return {@link Optional}&lt;{@link UserWindow}&gt; - プレイヤーのウィンドウ
     */
    @NotNull
    public static Optional<UserWindow> getWindow(@NotNull Class<? extends CustomGUI> guiClass, @NotNull Player player) {
        return getWindow(guiClass, SBPlayer.fromPlayer(player));
    }

    /**
     * プレイヤーのウィンドウ取得します。
     * @param guiClass - 対象のGUI
     * @param sbPlayer - プレイヤー
     * @return {@link Optional}&lt;{@link UserWindow}&gt; - プレイヤーのウィンドウ
     */
    @NotNull
    public static Optional<UserWindow> getWindow(@NotNull Class<? extends CustomGUI> guiClass, @NotNull SBPlayer sbPlayer) {
        var customGUI = GUIS.get(guiClass);
        return customGUI == null ? Optional.empty() : Optional.of(customGUI.getUserWindow(sbPlayer));
    }

    /**
     * GUIのUUIDを取得します。
     * @return {@link UUID} - GUIのUUID
     */
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * タイトルを取得します。
     * @return {@link String} - タイトル
     */
    @NotNull
    public String getTitle() {
        return title.get();
    }

    /**
     * GUIのアイテムの配列を取得します。
     * @return {@link GUIItem}[] - アイテムの配列
     */
    @NotNull
    public GUIItem[] getContents() {
        return items;
    }

    /**
     * GUIのサイズを取得します。
     * @return {@code int} - サイズ
     */
    public int getSize() {
        return items.length;
    }

    /**
     * アイテムの移動をキャンセルする場合は{@code true}を返します。 
     * @return {@code boolean} - アイテムの移動をキャンセルする場合は{@code true}
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * GUIにアイテムを設定します。
     * @param slot - スロット
     * @param item - アイテム
     * @return {@link CustomGUI}
     */
    @NotNull
    public CustomGUI setItem(int slot, @Nullable GUIItem item) {
        if (slot < items.length) {
            items[slot] = item;
        }
        return this;
    }

    /**
     * 指定したスロットにアイテムを設定します。
     * @param slots - スロット
     * @param item - アイテム
     * @return {@link CustomGUI}
     */
    @NotNull
    public CustomGUI setItem(int[] slots, @Nullable GUIItem item) {
        return setItem(slots, ArrayUtils.EMPTY_INT_ARRAY, item);
    }

    /**
     * 指定したスロットにアイテムを設定します。
     * <p>
     * {@code filter}で指定したスロットにはアイテムを設定しません。
     * @param slots - スロット
     * @param filter - フィルター
     * @param item - アイテム
     * @return {@link CustomGUI}
     */
    @NotNull
    public CustomGUI setItem(int[] slots, int[] filter, @Nullable GUIItem item) {
        for (int i = 0; i < slots.length; i++) {
            if (ArrayUtils.indexOf(filter, slots[i]) == -1) {
                setItem(i, item);
            }
        }
        return this;
    }

    /**
     * 指定したスロットの範囲にアイテムを設定します。
     * @param start - 開始位置
     * @param end - 終了位置
     * @param item - アイテム
     * @return {@link CustomGUI}
     */
    @NotNull
    public CustomGUI setItem(int start, int end, @Nullable GUIItem item) {
        return setItem(start, end, ArrayUtils.EMPTY_INT_ARRAY, item);
    }

    /**
     * 指定したスロットの範囲にアイテムを設定します。
     * <p>
     * {@code filter}で指定したスロットにはアイテムを設定しません。
     * @param start - 開始位置
     * @param end - 終了位置
     * @param filter - フィルター
     * @param item - アイテム
     * @return {@link CustomGUI}
     */
    @NotNull
    public CustomGUI setItem(int start, int end, int[] filter, @Nullable GUIItem item) {
        for (int i = start; i < end; i++) {
            if (ArrayUtils.indexOf(filter, i) == -1) {
                setItem(i, item);
            }
        }
        return this;
    }

    /**
     * 指定したスロットのアイテムを取得します。
     * @param slot - スロット
     * @return {@link GUIItem} - アイテム
     */
    @Nullable
    public GUIItem getItem(int slot) {
        return slot < items.length ? items[slot] : null;
    }

    /**
     * プレイヤーのウィンドウ取得します。
     * @param sbPlayer - プレイヤー
     * @return {@link UserWindow} - プレイヤーのウィンドウ
     */
    @NotNull
    public UserWindow getUserWindow(@NotNull SBPlayer sbPlayer) {
        var window = (UserWindow) sbPlayer.getObjectMap().get(key);
        if (window == null) {
            sbPlayer.getObjectMap().put(key, window = new UserWindow(sbPlayer, this));
        }
        return window;
    }

    /**
     * プレイヤーのウィンドウが存在する場合は{@code true}を返します。
     * @param sbPlayer - プレイヤー
     * @return {@link UserWindow} - プレイヤーのウィンドウが存在する場合は{@code true}
     */
    public boolean hasUserWindow(@NotNull SBPlayer sbPlayer) {
        return sbPlayer.getObjectMap().has(key);
    }

    /**
     * アイテムをクリックした時のサウンドを設定します。
     * @param sound - サウンド
     * @param volume - ボリューム
     * @param pitch - ピッチ
     */
    public final void setSoundEffect(@Nullable Sound sound, final int volume, final int pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * アイテムをクリックした時のサウンドを再生します。
     * @param sbPlayer - プレイヤー
     */
    public final void playSoundEffect(@NotNull SBPlayer sbPlayer) {
        if (sound != null) {
            sbPlayer.getPlayer().playSound(sbPlayer.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * GUIがロードされた時に呼び出されます。
     * @param window - プレイヤーのウィンドウ
     */
    public abstract void onLoaded(@NotNull UserWindow window);

    /**
     * GUIが開かれた時に呼び出されます。
     * @param window - プレイヤーのウィンドウ
     */
    public abstract void onOpened(@NotNull UserWindow window);

    /**
     * GUIが閉じられた時に呼び出されます。
     * @param window - プレイヤーのウィンドウ
     */
    public abstract void onClosed(@NotNull UserWindow window);
}