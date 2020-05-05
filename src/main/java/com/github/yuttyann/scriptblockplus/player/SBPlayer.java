package com.github.yuttyann.scriptblockplus.player;

import com.github.yuttyann.scriptblockplus.file.json.PlayerCount;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
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
	 * SBPlayerを取得します。
	 * @param player プレイヤー(オフラインも可)
	 * @return {@link SBPlayer}
	 */
	@NotNull
	static SBPlayer fromPlayer(@NotNull OfflinePlayer player) {
		return fromUUID(player.getUniqueId());
	}

	/**
	 * SBPlayerを取得します。
	 * @param uuid プレイヤーのUUID
	 * @return {@link SBPlayer}
	 */
	@NotNull
	static SBPlayer fromUUID(@NotNull UUID uuid) {
		return BaseSBPlayer.getSBPlayer(uuid);
	}

	/**
	 * プレイヤーがオンラインの場合にtrueを返します。
	 * @return プレイヤーがオンラインの場合はtrue
	 */
	boolean isOnline();

	/**
	 * プレイヤーを取得します。
	 * <p>
	 * プレイヤーがオフラインの場合はnullを返します。
	 * @return プレイヤー
	 */
	@Nullable
	Player getPlayer();

	/**
	 * オフラインプレイヤーを取得します。
	 * @return オフラインプレイヤー（オンラインの場合はキャストします。）
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
	 * プレイヤーのUUIDを取得します。
	 * @return プレイヤーのUUID
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
	 * Region（範囲選択クラス）を取得します。
	 * @return {@link Region}
	 */
	@NotNull
	Region getRegion();

	/**
	 * スクリプトの実行回数記録クラスを取得します。
	 * @return {@link PlayerCount}
	 */
	@NotNull
    PlayerCount getPlayerCount();

	/**
	 * ObjectMapを取得します。
	 * @return {@link ObjectMap}
	 */
	@NotNull
	ObjectMap getObjectMap();

	/**
	 * クリップボードをセットします。
	 * @param clipboard クリップボード
	 */
	void setClipboard(@Nullable SBClipboard clipboard);

	/**
	 * スクリプトをセットします。
	 * @param scriptLine スクリプト
	 */
	void setScriptLine(@Nullable String scriptLine);

	/**
	 * アクションをセットします。
	 * @param actionType アクション
	 */
	void setActionType(@Nullable String actionType);

	/**
	 * ワールド名を含めた文字列の座標をセットします。
	 * @param fullCoords ワールド名を含めた文字列の座標
	 */
	void setOldFullCoords(@Nullable String fullCoords);

	/**
	 * クリップボードを取得します。
	 * @return クリップボード（オプショナル）
	 */
	@NotNull
	Optional<SBClipboard> getClipboard();

	/**
	 * スクリプトを取得します。
	 * @return スクリプト（オプショナル）
	 */
	@NotNull
	Optional<String> getScriptLine();

	/**
	 * アクションを取得します。
	 * @return アクション（オプショナル）
	 */
	@NotNull
	Optional<String> getActionType();

	/**
	 * ワールド名を含めた文字列の座標を取得します。
	 * @return ワールド名を含めた文字列の座標（オプショナル）
	 */
	@NotNull
	Optional<String> getOldFullCoords();
}