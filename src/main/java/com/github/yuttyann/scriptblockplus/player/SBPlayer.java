package com.github.yuttyann.scriptblockplus.player;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * ScriptBlockPlus SBPlayer インターフェース
 * @author yuttyann44581
 */
public interface SBPlayer extends CommandSender {

    /**
     * ScriptBlockPlusの{@link SBPlayer}を取得します。
     * @param player プレイヤー
     * @return {@link SBPlayer}
     */
    @NotNull
    static SBPlayer fromPlayer(@NotNull OfflinePlayer player) {
        return fromUUID(player.getUniqueId());
    }

    /**
     * ScriptBlockPlusの{@link SBPlayer}を取得します。
     * @param uuid プレイヤーの{@link UUID}
     * @return {@link SBPlayer}
     */
    @NotNull
    static SBPlayer fromUUID(@NotNull UUID uuid) {
        BaseSBPlayer sbPlayer = BaseSBPlayer.getSBPlayers().get(uuid);
        if (sbPlayer == null) {
            BaseSBPlayer.getSBPlayers().put(uuid, sbPlayer = new BaseSBPlayer(uuid));
        }
        return sbPlayer;
    }

    /**
     * プレイヤーがオンラインの場合にtrueを返します。
     * @return プレイヤーがオンラインの場合はtrue
     */
    boolean isOnline();

    /**
     * BukkitAPIの{@link Player}を取得します。
     * @return {@link Player}
     */
    @NotNull
    Player getPlayer();

    /**
     * BukkitAPIの{@link OfflinePlayer}を取得します。
     * @return {@link OfflinePlayer}
     */
    @NotNull
    OfflinePlayer getOfflinePlayer();

    /**
     * プレイヤーのインベントリを取得します。
     * @return プレイヤーのインベントリ
     */
    @NotNull
    PlayerInventory getInventory();

    /**
     * プレイヤーの{@link UUID}を取得します。
     * @return {@link UUID}
     */
    @NotNull
    UUID getUniqueId();

    /**
     * プレイヤーが存在しているワールドを取得します。
     * @return プレイヤーが存在しているワールド
     */
    @NotNull
    World getWorld();

    /**
     * プレイヤーの座標を取得します。
     * @return プレイヤーの座標
     */
    @NotNull
    Location getLocation();

    /**
     * プレイヤーのメインハンドのアイテムを取得します。
     * @return メインハンドのアイテム
     */
    @Nullable
    ItemStack getItemInMainHand();

    /**
     * プレイヤーのオフハンドのアイテムを取得します。
     * @return オフハンドのアイテム
     */
    @Nullable
    ItemStack getItemInOffHand();

    /**
     * ワールドの選択範囲を取得します。
     * @return ワールドの選択範囲
     */
    @NotNull
    Region getRegion();

    /**
     * プレイヤーの格納データを取得します。
     * @return プレイヤーの格納データ
     */
    @NotNull
    ObjectMap getObjectMap();

    /**
     * スクリプトをセットします。
     * @param scriptLine スクリプト
     */
    void setScriptLine(@Nullable String scriptLine);

    /**
     * スクリプトエディットをセットします。
     * @param scriptEdit スクリプトエディット
     */
    void setScriptEdit(@Nullable ScriptEdit scriptEdit);

    /**
     * スクリプトのクリップボードをセットします。
     * @param sbClipboard スクリプトのクリップボード
     */
    void setSBClipboard(@Nullable SBClipboard sbClipboard);

    /**
     * ブロックの座標をセットします。
     * @param blockCoords ブロックの座標
     */
    void setOldBlockCoords(@Nullable BlockCoords blockCoords);

    /**
     * スクリプトを取得します。
     * @return スクリプト（オプショナル）
     */
    @NotNull
    Optional<String> getScriptLine();

    /**
     * スクリプトエディットを取得します。
     * @return スクリプトエディット（オプショナル）
     */
    @NotNull
    Optional<ScriptEdit> getScriptEdit();

    /**
     * スクリプトのクリップボードを取得します。
     * @return スクリプトのクリップボード（オプショナル）
     */
    @NotNull
    Optional<SBClipboard> getSBClipboard();

    /**
     * ブロックの座標を取得します。
     * @return ブロックの座標（オプショナル）
     */
    @NotNull
    Optional<BlockCoords> getOldBlockCoords();
}