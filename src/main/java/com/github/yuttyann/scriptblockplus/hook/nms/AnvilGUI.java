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
package com.github.yuttyann.scriptblockplus.hook.nms;

import static com.github.yuttyann.scriptblockplus.utils.server.minecraft.Minecraft.*;
import static com.github.yuttyann.scriptblockplus.utils.version.McVersion.*;
import static org.bukkit.event.inventory.InventoryAction.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.utils.version.McVersion;

/**
 * ScriptBlockPlus AnvilGUI クラス
 * <p>
 * https://github.com/WesJD/AnvilGUI
 * @author yuttyann44581
 */
public final class AnvilGUI {

    public static final String KEY_OPEN = Utils.randomUUID();

    private final Consumer<Player> close, leftClick, rightClick;
    private final BiConsumer<Player, ItemStack> update;
    private final BiFunction<Player, String, Response> complete;

    private final Player player;
    private final AnvilListener listener;
    private final boolean prevent;

    private String title, text;
    private ItemStack left, right;
    private Inventory inventory;
    private Object container;
    private int containerId;
    private boolean open;

    /**
     * コンストラクタ
     * @param player - プレイヤー
     * @param title - タイトル
     * @param text - テキスト
     * @param left - インプットアイテム１
     * @param right - インプットアイテム２
     * @param prevent - インベントリを閉じないようにするかどうか
     * @param close - インベントリを閉じた時の処理
     * @param leftClick - インプットアイテム１をクリックした時の処理
     * @param rightClick - インプットアイテム２をクリックした時の処理
     * @param update - インベントリが更新されたときの処理
     * @param complete - アイテムの編集が完了した時の処理
     */
    private AnvilGUI(
        @NotNull Player player,
        @NotNull String title,
        @Nullable String text,
        @Nullable ItemStack left,
        @Nullable ItemStack right,
        @Nullable boolean prevent,
        @Nullable Consumer<Player> close,
        @Nullable Consumer<Player> leftClick,
        @Nullable Consumer<Player> rightClick,
        @Nullable BiConsumer<Player, ItemStack> update,
        @Nullable BiFunction<Player, String, Response> complete
    ) {
        this.close = close;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
        this.update = update;
        this.complete = complete;
        this.player = player;
        this.listener = new AnvilListener();
        this.prevent = prevent;
        this.title = title;
        this.left = left;
        this.right = right;
        if (text != null) {
            ItemUtils.setName(left == null ? this.left = new ItemStack(Material.PAPER) : left, this.text = text);
        }
        openInventory();
    }

    /**
     * インベントリを開いているのか設定します。
     * @param open - 開いているのかどうか
     */
    private void setOpened(final boolean open) {
        ScriptBlock.getSBPlayer(player).getObjectMap().put(KEY_OPEN, this.open = open);
    }

    /**
     * インベントリを開いているのかどうか
     * @return {@code boolean} - 開いている場合は{@code true}
     */
    public boolean isOpened() {
        return open;
    }

    /**
     * インベントリを取得します。
     * @return {@link Inventory} - インベントリ
     */
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * インベントリを閉じます。
     */
    public void closeInventory() {
        closeInventory(true);
    }

    /**
     * インベントリを閉じます。
     * @param sendClosePacket - インベントリを閉じるのかどうか
     */
    private void closeInventory(final boolean sendClosePacket) {
        if (isOpened()) {
            setOpened(false);
            HandlerList.unregisterAll(listener);
            if (sendClosePacket) {
                try {
                    var serverPlayer = getServerPlayer(player);
                    handleInventoryCloseEvent(serverPlayer);
                    setActiveContainer(serverPlayer, getDefaultContainer(serverPlayer));
                    sendPacket(serverPlayer, newClientboundContainerClosePacket(containerId));
                } catch (ReflectiveOperationException ex) { ex.printStackTrace(); }
            }
            if (close != null) {
                close.accept(player);
            }
        }
    }

    /**
     * インベントリを開きます。
     */
    private void openInventory() {
        try {
            var serverPlayer = getServerPlayer(player);
            var serverLevel = getServerLevel(player.getWorld());
            handleInventoryCloseEvent(serverPlayer);
            setActiveContainer(serverPlayer, getDefaultContainer(serverPlayer));
            Bukkit.getPluginManager().registerEvents(listener, ScriptBlock.getInstance());
            var componentTitle = getComponentFromJson(title);
            this.container = newAnvilMenu(serverPlayer, serverLevel, componentTitle);
            this.inventory = getBukkitView(container).getTopInventory();
            inventory.setItem(Slot.INPUT_LEFT, left);
            if (right != null) {
                inventory.setItem(Slot.INPUT_RIGHT, right);
            }
            this.containerId = NMSHelper.getContainerId(serverPlayer, container);
            if (V_1_14.isSupported()) {
                sendPacket(serverPlayer, newClientboundOpenScreenPacket(container, componentTitle));
            } else {
                var name = NetMinecraft.LEGACY_PATH.newInstance(true, "ChatMessage", "tile.anvil.name", new Object[0]);
                sendPacket(serverPlayer, newClientboundOpenScreenPacket(containerId, "minecraft:anvil", name));
            }
            setActiveContainer(serverPlayer, container);
            NMSHelper.setActiveContainerId(container, containerId);
            initMenu(serverPlayer, container);
            setOpened(true);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * ScriptBlockPlus AnvilListener クラス
     * @author yuttyann44581
     */
    private class AnvilListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void onPrepareAnvil(PrepareAnvilEvent event) {
            if (!event.getInventory().equals(inventory)) {
                return;
            }
            var output = event.getResult();
            if (output == null || output.getType() == Material.AIR) {
                return;
            }
            var name = ItemUtils.getName(output, "");
            if (McVersion.V_1_16_2.isUnSupported() && StringUtils.isNotEmpty(text) && text.startsWith("§") && name.length() > 0) {
                ItemUtils.setName(output, name.substring(1));
            }
            if (McVersion.V_1_12.isSupported()) {
                ScriptBlock.getScheduler().run(() -> event.getView().setRepairCost(0));
            } else if (McVersion.V_1_11.isSupported()) {
                ScriptBlock.getScheduler().run(() -> ((AnvilInventory) event.getInventory()).setRepairCost(0));
            }
            if (update != null) {
                update.accept((Player) event.getView().getPlayer(), output);
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClick(InventoryClickEvent event) {
            if (!event.getInventory().equals(inventory)) {
                return;
            }
            if (event.getRawSlot() < 3 || event.getAction().equals(MOVE_TO_OTHER_INVENTORY)) {
                event.setCancelled(true);
                var player = (Player) event.getWhoClicked();
                switch (event.getRawSlot()) {
                    case Slot.OUTPUT: {
                        var item = inventory.getItem(Slot.OUTPUT);
                        if (item == null || item.getType() == Material.AIR) {
                            return;
                        }
                        var response = complete.apply(player, ItemUtils.getName(item, ""));
                        if (response.getText() != null) {
                            inventory.setItem(Slot.INPUT_LEFT, ItemUtils.setName(item, response.getText()));
                        } else if (response.getInventoryToOpen() != null) {
                            player.openInventory(response.getInventoryToOpen());
                        } else {
                            closeInventory();
                        }
                        break;
                    }
                    case Slot.INPUT_LEFT: {
                        if (leftClick != null) {
                            leftClick.accept(player);
                        }
                        break;
                    }
                    case Slot.INPUT_RIGHT: {
                        if (rightClick != null) {
                            rightClick.accept(player);
                        }
                        break;
                    }
                    default:
                }
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryDrag(InventoryDragEvent event) {
            if (!event.getInventory().equals(inventory)) {
                return;
            }
            for (int slot : Slot.VALUES) {
                if (event.getRawSlots().contains(slot)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClose(InventoryCloseEvent event) {
            if (!isOpened() || !event.getInventory().equals(inventory)) {
                return;
            }
            try {
                inventory.clear();
            } finally {
                closeInventory(false);
                if (prevent) {
                    ScriptBlock.getScheduler().run(AnvilGUI.this::openInventory);
                }
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin().equals(ScriptBlock.getInstance())) {
                inventory.clear();
            }
        }
    }

    /**
     * ScriptBlockPlus AnvilBuilder クラス
     * @author yuttyann44581
     */
    public static final class AnvilBuilder {

        private Consumer<Player> close, leftClick, rightClick;
        private BiConsumer<Player, ItemStack> update;
        private BiFunction<Player, String, Response> complete;

        private String title = "minecraft:anvil", text;
        private ItemStack left, right;
        private boolean prevent = false;

        /**
         * インベントリを閉じることが出来ないように設定します。
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder preventClose() {
            prevent = true;
            return this;
        }

        /**
         * インベントリを閉じた時の処理を設定します。
         * @param close - インベントリを閉じた時の処理
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder onClose(@NotNull Consumer<Player> close) {
            this.close = close;
            return this;
        }

        /**
         * インプットアイテム１をクリックした時の処理を設定します。
         * @param leftClick - インプットアイテム１をクリックした時の処理
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder onLeftClick(@NotNull Consumer<Player> leftClick) {
            this.leftClick = leftClick;
            return this;
        }

        /**
         * インプットアイテム２をクリックした時の処理を設定します。
         * @param rightClick - インプットアイテム２をクリックした時の処理
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder onRightClick(@NotNull Consumer<Player> rightClick) {
            this.rightClick = rightClick;
            return this;
        }

        /**
         * インベントリが更新されたときの処理を設定します。
         * @param update - インベントリが更新されたときの処理
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder onUpdate(@NotNull BiConsumer<Player, ItemStack> update) {
            this.update = update;
            return this;
        }

        /**
         * アイテムの編集が完了した時の処理を設定します。
         * @param complete - アイテムの編集が完了した時の処理
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder onComplete(@NotNull BiFunction<Player, String, Response> complete) {
            this.complete = complete;
            return this;
        }

        /**
         * テキストを設定します。
         * @param text - テキスト
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder text(@NotNull String text) {
            this.text = text;
            return this;
        }

        /**
         * タイトルを設定します。
         * @param title - タイトル
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder title(@NotNull String title) {
            this.title = title;
            return this;
        }

        /**
         * インプットアイテム１を設定します。
         * @param item - アイテム
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder left(@NotNull ItemStack item) {
            this.left = item;
            return this;
        }

        /**
         * インプットアイテム２を設定します。
         * @param item - アイテム
         * @return {@link AnvilBuilder}
         */
        @NotNull
        public AnvilBuilder right(@NotNull ItemStack item) {
            this.right = item;
            return this;
        }

        /**
         * 金床のインベントリを開きます。
         * @param player - プレイヤー
         * @return {@link AnvilGUI}
         */
        @NotNull
        public AnvilGUI open(@NotNull Player player) {
            return new AnvilGUI(player, title, text, left, right, prevent, close, leftClick, rightClick, update, complete);
        }
    }

    /**
     * ScriptBlockPlus Response クラス
     * @author yuttyann44581
     */
    public static final class Response {

        private final String text;
        private final Inventory openInventory;

        /**
         * コンストラクタ
         * @param text
         * @param openInventory
         */
        private Response(@Nullable String text, @Nullable Inventory openInventory) {
            this.text = text;
            this.openInventory = openInventory;
        }

        /**
         * テキストを取得します。
         * @return {@link String} - テキスト
         */
        @Nullable
        public String getText() {
            return text;
        }

        /**
         * インベントリを取得します。
         * @return {@link Inventory} - インベントリ
         */
        @Nullable
        public Inventory getInventoryToOpen() {
            return openInventory;
        }

        /**
         * インベントリを閉じるレスポンスを返します。
         * @return {@link Response} - レスポンス
         */
        @NotNull
        public static Response close() {
            return new Response(null, null);
        }

        /**
         * テキストを設定するレスポンスを返します。
         * @param text - テキスト
         * @return {@link Response} - レスポンス
         */
        @NotNull
        public static Response text(@NotNull String text) {
            return new Response(text, null);
        }

        /**
         * インベントリを開くレスポンスを返します。
         * @param inventory - インベントリ
         * @return {@link Response} - レスポンス
         */
        @NotNull
        public static Response openInventory(@NotNull Inventory inventory) {
            return new Response(null, inventory);
        }
    }

    /**
     * ScriptBlockPlus Slot クラス
     * @author yuttyann44581
     */
    public static final class Slot {

        /**
         * 金床のインベントリのスロット番号
         */
        public static final int INPUT_LEFT = 0, INPUT_RIGHT = 1, OUTPUT = 2;

        /**
         * 金床のインベントリのスロット番号の配列
         */
        private static final int[] VALUES = new int[] { INPUT_LEFT, INPUT_RIGHT, OUTPUT };
    }
}