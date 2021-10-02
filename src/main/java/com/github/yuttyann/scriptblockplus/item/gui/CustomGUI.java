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

import com.github.yuttyann.scriptblockplus.player.SBPlayer;

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

    private final UUID uuid;
    private final String key, title;
    private final GUIItem[] items;
    private final boolean cancelled;

    private Sound sound;
    private float volume, pitch;

    protected CustomGUI(@NotNull String title, @NotNull int size, final boolean cancelled) {
        this.uuid = UUID.randomUUID();
        this.key = uuid.toString();
        this.title = title;
        this.items = new GUIItem[size < 0 ? size * -1 : size == 0 ? 9 : size > 6 ? 54 : size * 9];
        this.cancelled = cancelled;
    }

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

    public static void register(@NotNull CustomGUI customGUI) {
        GUIS.put(customGUI.getClass(), customGUI);
    }

    @NotNull
    public static Optional<UserWindow> getWindow(@NotNull Class<? extends CustomGUI> guiClass, @NotNull Player player) {
        return getWindow(guiClass, SBPlayer.fromPlayer(player));
    }

    @NotNull
    public static Optional<UserWindow> getWindow(@NotNull Class<? extends CustomGUI> guiClass, @NotNull SBPlayer sbPlayer) {
        var customGUI = GUIS.get(guiClass);
        return customGUI == null ? Optional.empty() : Optional.ofNullable(customGUI.getUserWindow(sbPlayer));
    }

    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public GUIItem[] getContents() {
        return items;
    }

    public int getSize() {
        return items.length;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @NotNull
    public CustomGUI setItem(int slot, @Nullable GUIItem item) {
        if (slot < items.length) {
            items[slot] = item;
        }
        return this;
    }

    @Nullable
    public GUIItem getItem(int slot) {
        return slot < items.length ? items[slot] : null;
    }

    @NotNull
    public UserWindow getUserWindow(@NotNull SBPlayer sbPlayer) {
        var window = (UserWindow) sbPlayer.getObjectMap().get(key);
        if (window == null) {
            sbPlayer.getObjectMap().put(key, window = new UserWindow(sbPlayer, this));
        }
        return window;
    }

    public boolean hasUserWindow(@NotNull SBPlayer sbPlayer) {
        return sbPlayer.getObjectMap().has(key);
    }

    public final void setSoundEffect(@Nullable Sound sound, final int volume, final int pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public final void playSoundEffect(@NotNull SBPlayer sbPlayer) {
        if (sound != null) {
            sbPlayer.getPlayer().playSound(sbPlayer.getLocation(), sound, volume, pitch);
        }
    }

    public abstract void onLoaded(@NotNull UserWindow window);

    public abstract void onOpened(@NotNull UserWindow window);

    public abstract void onClosed(@NotNull UserWindow window);
}