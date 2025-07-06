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
package com.github.yuttyann.scriptblockplus.player;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * ScriptBlockPlus SBPlayer インターフェース
 * @author yuttyann44581
 */
public interface SBPlayer extends SBPlayerMap, Permissible {

    /**
     * プレイヤーがオンラインかどうなのかを設定します。
     * @param isOnline - プレイヤーがオンラインの場合は{@code true}
     */
    void setOnline(boolean isOnline);

    /**
     * プレイヤーがオンラインの場合は{@code true}を返します。
     * @return {@code boolean} - プレイヤーがオンラインの場合は{@code true}
     */
    boolean isOnline();

    /**
     * {@code BukkitAPI}の{@code org.bukkit.entity.Player}を取得します。
     * @return {@link Player} - プレイヤー
     */
    @NotNull
    Player toPlayer();

    /**
     * {@code BukkitAPI}の{@code org.bukkit.OfflinePlayer}を取得します。
     * @return {@link OfflinePlayer} - オフラインプレイヤー
     */
    @NotNull
    OfflinePlayer toOfflinePlayer();

    /**
     * プレイヤーのサーバーを取得します。
     * @return {@link Server} - サーバー
     */
    @NotNull
    public Server getServer();

    /**
     * プレイヤーのインベントリを取得します。
     * @return {@link PlayerInventory} - インベントリ
     */
    @NotNull
    PlayerInventory getInventory();

    /**
     * プレイヤーの名前を取得します。
     * @return {@link String} - プレイヤーの名前
     */
    @NotNull
    public String getName();

    /**
     * プレイヤーの{@link UUID}を取得します。
     * @return {@link UUID} - プレイヤーの{@link UUID}
     */
    @NotNull
    UUID getUniqueId();

    /**
     * プレイヤーが存在しているワールドを取得します。
     * @return {@link World} - ワールド
     */
    @NotNull
    World getWorld();

    /**
     * プレイヤーの座標を取得します。
     * @return {@link Location} - プレイヤーの座標
     */
    @NotNull
    Location getLocation();

    /**
     * プレイヤーのメインハンドのアイテムを取得します。
     * @return {@link ItemStack} - メインハンドアイテム
     */
    @Nullable
    ItemStack getItemInMainHand();

    /**
     * プレイヤーのオフハンドのアイテムを取得します。
     * @return {@link ItemStack} - オフハンドアイテム
     */
    @Nullable
    ItemStack getItemInOffHand();

    /**
     * プレイヤーにメッセージを送信します。
     * @param message - メッセージ
     */
    public void sendMessage(@NotNull String message);

    /**
     * プレイヤーにメッセージを送信します。
     * @param messages - メッセージ
     */
    public void sendMessage(@NotNull String... messages);

    /**
     * プレイヤーにメッセージを送信します。
     * @param uuid - 送信者
     * @param message - メッセージ
     */
    public void sendMessage(@NotNull UUID uuid, @NotNull String message);

    /**
     * プレイヤーにメッセージを送信します。
     * @param uuid - 送信者
     * @param messages - メッセージ
     */
    public void sendMessage(@NotNull UUID uuid, @NotNull String... messages);
}