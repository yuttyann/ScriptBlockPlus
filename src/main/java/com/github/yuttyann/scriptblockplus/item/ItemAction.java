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
package com.github.yuttyann.scriptblockplus.item;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.event.RunItemEvent;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * ScriptBlockPlus ItemAction クラス
 * @author yuttyann44581
 */
public abstract class ItemAction implements Cloneable {

    private static final Set<ItemAction> ITEMS = new HashSet<>();

    private final Material material;
    private final Supplier<String> name;
    private final Supplier<List<String>> lore;

    private ItemFlag[] flags;

    /**
     * コンストラクタ
     * @param material - アイテムの種類
     * @param name - アイテムの名前
     */
    public ItemAction(@NotNull Material material, @NotNull Supplier<String> name) {
        this(material, name, null);
    }

    /**
     * コンストラクタ
     * @param material - アイテムの種類
     * @param name - アイテムの名前
     * @param lore - アイテムの概要
     */
    public ItemAction(@NotNull Material material, @NotNull Supplier<String> name, @Nullable Supplier<List<String>> lore) {
        this.material = material;
        this.name = name;
        this.lore = lore;
    }

    /**
     * アイテムを取得します。
     * @return {@link ItemStack} - アイテム
     */
    @NotNull
    public final ItemStack getItem() {
        var item = new ItemStack(material);
        ItemUtils.setName(item, name.get());
        if (lore != null) {
            ItemUtils.setLore(item, lore.get());
        }
        if (flags != null) {
            var meta = item.getItemMeta();
            meta.addItemFlags(flags);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * アイテムフラグを設定します。
     * @param flags - アイテムフラグ
     */
    public void setItemFlags(@Nullable ItemFlag... flags) {
        this.flags = flags;
    }

    /**
     * アイテムの処理が実行された際に呼び出されます。
     * @param runItem - 実行したアイテムやプレイヤーの情報
     */
    public abstract void run(@NotNull RunItem runItem);

    /**
     * アイテムのスロットを切り替えた際に呼び出されます。
     * @param changeSlot - 切り替えたスロット番号やプレイヤーの情報
     */
    public void slot(@NotNull ChangeSlot changeSlot) { }

    /**
     * パーミッションを所持しているのか判定します。
     * @param permissible - 対象者
     * @return {@code boolean} - パーミッションを所持している場合は{@code true}
     */
    public boolean hasPermission(@NotNull Permissible permissible) {
        return true;
    }

    /**
     * アイテムアクションの一覧を取得します。
     * @return {@link Set}&lt;{@link ItemAction}&gt; - アイテムアクションの一覧
     */
    @NotNull
    public static Set<ItemAction> getItems() {
        return ITEMS;
    }

    /**
     * アイテムアクションを登録します。
     * @param itemAction - アイテムアクション
     */
    public static void register(@NotNull ItemAction itemAction) {
        ITEMS.add(itemAction);
    }

    /**
     * アイテムの処理を実行できる状態なのか確認します。
     * @param permissible - 対象者
     * @param item - アイテム
     * @param permission - パーミッションを所持を確認するのかどうか
     * @return {@code boolean} - 実行できる場合は{@code true}
     */
    public static boolean has(@NotNull Permissible permissible, @Nullable ItemStack item, boolean permission) {
        var itemAction = StreamUtils.filterFirst(ITEMS, i -> i.compare(item));
        return itemAction.filter(i -> !permission || i.hasPermission(permissible)).isPresent();
    }

    /**
     * アイテムの処理を呼び出します。
     * @param player - プレイヤー
     * @param item - アイテム
     * @param location - 座標
     * @param action - アクション
     * @return {@code boolean} - 実行に成功した場合は{@code true}
     */
    public static boolean callRun(@NotNull Player player, @Nullable ItemStack item, @Nullable Location location, @NotNull Action action) {
        var itemAction = ITEMS.stream().filter(i -> i.compare(item)).filter(i -> i.hasPermission(player)).findFirst();
        if (itemAction.isPresent()) {
            var runItem = new RunItem(item, SBPlayer.fromPlayer(player), action, location == null ? null : BlockCoords.of(location));
            var runItemEvent = new RunItemEvent(runItem);
            Bukkit.getPluginManager().callEvent(runItemEvent);
            if (!runItemEvent.isCancelled()) {
                itemAction.get().clone().run(runItem);
            }
            return true;
        }
        return false;
    }

    /**
     * スロット処理を呼び出します。
     * @param player - プレイヤー
     * @param item - アイテム
     * @param newSlot - 新しいスロット番号
     * @param oldSlot - 前のスロット番号
     */
    public static void callSlot(@NotNull Player player, @Nullable ItemStack item, int newSlot, int oldSlot) {
        var itemAction = ITEMS.stream().filter(i -> i.compare(item)).filter(i -> i.hasPermission(player));
        itemAction.findFirst().ifPresent(i -> i.clone().slot(new ChangeSlot(SBPlayer.fromPlayer(player), newSlot, oldSlot)));
    }

    /**
     * アイテムを比較します。
     * @param item - アイテム
     * @return {@code boolean} - 一致する場合は{@code true}
     */
    public boolean compare(@Nullable ItemStack item) {
        if (item == null || item.getType() != material) {
            return false;
        }
        if (lore != null && !ItemUtils.getLore(item).equals(StringUtils.setListColor(lore.get()))) {
            return false;
        }
        return ItemUtils.getName(item).equals(StringUtils.setColor(name.get()));
    }

    @Override
    @NotNull
    public ItemAction clone() {
        try {
            return (ItemAction) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ItemAction)) {
            return false;
        }
        var item = (ItemAction) obj;
        return this.material == item.material && this.name == item.name && this.lore == item.lore;
    }

    @Override
    public int hashCode() {
        return getItem().hashCode();
    }
}