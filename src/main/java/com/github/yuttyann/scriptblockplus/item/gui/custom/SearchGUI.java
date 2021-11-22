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
package com.github.yuttyann.scriptblockplus.item.gui.custom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.BlockScript;
import com.github.yuttyann.scriptblockplus.hook.nms.AnvilGUI.AnvilBuilder;
import com.github.yuttyann.scriptblockplus.hook.nms.AnvilGUI.Response;
import com.github.yuttyann.scriptblockplus.item.gui.CustomGUI;
import com.github.yuttyann.scriptblockplus.item.gui.GUIItem;
import com.github.yuttyann.scriptblockplus.item.gui.UserWindow;
import com.github.yuttyann.scriptblockplus.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.raytrace.SBBoundingBox;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils.TriConsumer;
import com.google.common.collect.Lists;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus SearchGUI クラス
 * @author yuttyann44581
 */
public final class SearchGUI extends CustomGUI {

    private static final int[] SLOTS =  {
        10 /* [0]=スクリプト */, 19 /* [1]=更新時間 */, 28 /* [2]=座標 */, 37 /* [3]=ネームタグ */, 
        11 /* [4]=スクリプトキー */, 20 /* [5]=次のぺージ */, 29 /* [6]=前のページ */, 38 /* [7]=クリア */,
        /*              空白                 */
        12, 13, 14, 15, 16, 21, 22, 23, 24, 25,
        30, 31, 32, 33, 34, 39, 40, 41, 42, 43
    };

    public static final String KEY_INDEX = Utils.randomUUID(), KEY_SWITCH = Utils.randomUUID(), KEY_SCRIPT = Utils.randomUUID();

    public static final String NEXT = "§a▶ §e", PREV = "§c◀ §e", TEXT = "§a✏ §e", ZERO = 0 + "§6 / §e" + 0;

    public static final List<String> EMPTY_LORE = Arrays.asList(TEXT), INTERACT_LORE = Arrays.asList(TEXT + "INTERACT");

    public static final BiFunction<String, Boolean, String> GET_VALUE = (s, b) -> {
        return (b ? ChatColor.stripColor(StringUtils.removeStart(s, TEXT)) : StringUtils.removeStart(s, TEXT)).trim();
    };

    private final TriConsumer<UserWindow, String, BiFunction<Player, String, Response>> OPEN_ANVIL = (w, s, r) -> {
        new AnvilBuilder()
        .title(SBConfig.GUI_SYS_INPUT.setColor())
        .text(s.isEmpty() ? "§r" : s)
        .onClose(p -> ScriptBlock.getScheduler().run(w::openGUI))
        .onUpdate((p, i) -> ItemUtils.setLore(i, null))
        .onComplete((p, t) -> {
            playSoundEffect(w.getSBPlayer());
            return r.apply(p, t);
        })
        .left(GUIItem.PAPER.get().toBukkit())
        .onLeftClick(p -> {
            playSoundEffect(w.getSBPlayer());
            r.apply(p, null);
            w.getSBPlayer().getPlayer().closeInventory();
        })
        .open(w.getSBPlayer().getPlayer());
    };

    private final TriConsumer<UserWindow, GUIItem, ScriptJson> OPEN_SETTING = (w1, g, s) -> {
        if (!s.has()) {
            updateWindow(w1, true, false, 0);
            return;
        }
        var sbPlayer = w1.getSBPlayer();
        getWindow(SettingGUI.class, sbPlayer).ifPresent(w2 -> {
            if (((SettingGUI) w2.getCustomGUI()).isOpened(s)) {
                sbPlayer.sendMessage(SBConfig.GUI_SYS_INPLAYER.setColor());
            } else {
                sbPlayer.getObjectMap().put(KEY_SCRIPT, s);
                w1.shiftWindow(w2);
            }
        });
    };

    {
        for (int i = 0; i < getSize(); i++) {
            if (!ArrayUtils.contains(SLOTS, i)) {
                setItem(i, GUIItem.BLACK);
            }
        }
    }

    public SearchGUI() {
        super(SBConfig.GUI_SYS_SEARCHGUI::setColor, 6, true);
        setSoundEffect(Sound.UI_BUTTON_CLICK, 1, 1);
    }

    @Override
    public void onLoaded(@NotNull UserWindow window) {
        window.setItem(SLOTS[4], new GUIItem(1, ItemUtils.getOakSignMaterial(), SBConfig.GUI_SEARCH_SCRIPTKEY.setColor(), INTERACT_LORE, (w, g, c) -> {
            w.setItem(SLOTS[4], g.setLore(TEXT + nextType(getScriptKey(g), c == ClickType.LEFT ? -1 : 1)));
            updateWindow(w, true, false, 0);
        }));
        window.setItem(SLOTS[7], new GUIItem(1, Material.BARRIER, SBConfig.GUI_SEARCH_RESET.setColor(), null, (w, g, c) -> {
            w.setItem(SLOTS[0], w.getItem(SLOTS[0]).setLore(EMPTY_LORE));
            w.setItem(SLOTS[1], w.getItem(SLOTS[1]).setLore(EMPTY_LORE));
            w.setItem(SLOTS[2], w.getItem(SLOTS[2]).setLore(EMPTY_LORE));
            w.setItem(SLOTS[3], w.getItem(SLOTS[3]).setLore(EMPTY_LORE));
            updateWindow(w, true, false, 0);
        }));

        window.setItem(SLOTS[5], new GUIItem(ItemUtils.getGlassPane(5), (w, g, c) ->
            updateWindow(w, false, false, w.getSBPlayer().getObjectMap().getInt(KEY_INDEX))
        ).setName(SBConfig.GUI_SEARCH_NEXT.setColor()));
        window.setItem(SLOTS[6], new GUIItem(ItemUtils.getGlassPane(14), (w, g, c) ->
            updateWindow(w, false, true, w.getSBPlayer().getObjectMap().getInt(KEY_INDEX))
        ).setName(SBConfig.GUI_SEARCH_PREV.setColor()));

        // AnvilGUI
        window.setItem(SLOTS[0], new GUIItem(1, ItemUtils.getCommandMaterial(), SBConfig.GUI_SEARCH_SCRIPT.setColor(), EMPTY_LORE, (w, g, c) -> {
            OPEN_ANVIL.accept(w, GET_VALUE.apply(g.getLore().get(0), true), (p, t) -> {
                w.setItem(SLOTS[0], g.setLore(TEXT + (t == null ? "" : ChatColor.stripColor(t))));
                return Response.close();
            });
        }));
        window.setItem(SLOTS[1], new GUIItem(1, Material.COMPASS, SBConfig.GUI_SEARCH_COORDS.setColor(), EMPTY_LORE, (w, g, c) -> {
            OPEN_ANVIL.accept(w, GET_VALUE.apply(g.getLore().get(0), true), (p, t) -> {
                w.setItem(SLOTS[1], g.setLore(TEXT + (t == null ? "" : ChatColor.stripColor(t))));
                return Response.close();
            });
        }));
        window.setItem(SLOTS[2], new GUIItem(1, ItemUtils.getClockMaterial(), SBConfig.GUI_SEARCH_TIME.setColor(), EMPTY_LORE, (w, g, c) -> {
            OPEN_ANVIL.accept(w, GET_VALUE.apply(g.getLore().get(0), true), (p, t) -> {
                w.setItem(SLOTS[2], g.setLore(TEXT + (t == null ? "" : ChatColor.stripColor(t))));
                return Response.close();
            });
        }));
        window.setItem(SLOTS[3], new GUIItem(1, Material.NAME_TAG, SBConfig.GUI_SEARCH_NAMETAG.setColor(), EMPTY_LORE, (w, g, c) -> {
            OPEN_ANVIL.accept(w, GET_VALUE.apply(g.getLore().get(0), true), (p, t) -> {
                w.setItem(SLOTS[3], g.setLore(TEXT + (t == null ? "" : ChatColor.stripColor(t))));
                return Response.close();
            });
        }));
    }

    @Override
    public void onOpened(@NotNull UserWindow window) {
        updateWindow(window, true, false, 0);
    }

    @Override
    public void onClosed(@NotNull UserWindow window) { }

    private void updateWindow(@NotNull UserWindow window, final boolean init, boolean prev, int index) {
        var json = BlockScriptJson.newJson(getScriptKey(window.getItem(SLOTS[4])));
        var elements = filterElements(window, Lists.newArrayList(json.copyElements()));
        var objectMap = window.getSBPlayer().getObjectMap();
        int slot = 8, slotSize = SLOTS.length - 8, jsonSize = elements.size();
        if (init) {
            if (jsonSize == 0) {
                window.setItem(SLOTS[5], window.getItem(SLOTS[5]).setLore(NEXT + ZERO));
                window.setItem(SLOTS[6], window.getItem(SLOTS[6]).setLore(PREV + ZERO));
            }
            objectMap.put(KEY_INDEX, index = 0);
            objectMap.put(KEY_SWITCH, false);
        } else if (objectMap.getBoolean(KEY_SWITCH) == !prev) {
            if ((prev ? index > 20 : index >= 0) && index < jsonSize) {
                index = prev ? index - slotSize : index + slotSize;
            }
            objectMap.put(KEY_SWITCH, prev);
        }
        double now = (double) index / slotSize, last = (double) jsonSize / slotSize;
        boolean update = prev ? now >= 1 : now < last;
        if (jsonSize == 0 || update) {
            objectMap.put(KEY_INDEX, 0);
            IntStream.range(0, slotSize).forEach(i -> window.setItem(SLOTS[i + slot], null));
        }
        if (update) {
            int surplus = index % slotSize, from = prev ? index - (slotSize + surplus) : index, to = prev ? index - surplus : slotSize + index;
            var subList = elements.subList(from = from > 0 ? from : 0, prev ? to : (to = jsonSize > to ? to : jsonSize));
            var player = window.getSBPlayer().getPlayer();
            for (int i = 0, l = subList.size(); i < l; i++) {
                var scriptJson = new ScriptJson(subList.get(i), json);
                window.setItem(SLOTS[i + slot],
                new GUIItem(
                    true,
                    1,
                    ItemUtils.getChainCommandMaterial(),
                    SBConfig.GUI_SEARCH_SETTING.setColor(),
                    scriptJson.createLore(player),
                    (w, g, c) -> OPEN_SETTING.accept(w, g, scriptJson))
                );
            }
            var pages = (int) Math.ceil((double) to / slotSize) + "§6 / §e" + (int) Math.ceil(last);
            window.setItem(SLOTS[5], window.getItem(SLOTS[5]).setLore(NEXT + pages));
            window.setItem(SLOTS[6], window.getItem(SLOTS[6]).setLore(PREV + pages));
            objectMap.put(KEY_INDEX, prev ? from : to);
        }
    }

    @NotNull
    private List<BlockScript> filterElements(@NotNull UserWindow window, @NotNull List<BlockScript> elements) {
        var script = GET_VALUE.apply(window.getItem(SLOTS[0]).getLore().get(0), true);
        var upTime = GET_VALUE.apply(window.getItem(SLOTS[1]).getLore().get(0), true);
        var coords = GET_VALUE.apply(window.getItem(SLOTS[2]).getLore().get(0), true);
        var nameTag = GET_VALUE.apply(window.getItem(SLOTS[3]).getLore().get(0), true);
        return elements.stream().filter(b -> {
            // スクリプトの部分一致検索
            return script.isEmpty() ? true : StreamUtils.anyMatch(b.getScripts(), s -> s.contains(script));
        }).filter(b -> {
            // 日付検索 yyyy/MM/dd ～ | yyyy/MM/dd~yyyy/MM/dd
            if (upTime.isEmpty()) {
                return true;
            }
            try {   
                var dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                var lastEdit = dateFormat.parse(b.getLastEdit());
                var tilde = StringUtils.split(upTime, '~');
                var date1 = dateFormat.parse(tilde.get(0));
                var date2 = tilde.size() > 1 ? dateFormat.parse(tilde.get(1)) : null;
                return date2 == null ? lastEdit.after(date1) : lastEdit.after(date1) && lastEdit.before(date2);
            } catch (ParseException e) {
                return false;
            }
        })
        .filter(b -> {
            // 座標検索 world | world:x,y,z~x,y,z | x,y,z~x,y,z
            if (coords.isEmpty()) {
                return true;
            }
            try {
                var world = BlockCoords.ZERO.getWorld();
                var colon = StringUtils.split(coords, ':');
                var tilde = StringUtils.split(colon.size() > 1 ? colon.get(1) : coords, '~');
                var coords0 = b.getBlockCoords();
                if (colon.size() > 1 && !coords0.getWorld().equals(Utils.getWorld(colon.get(0)))) {
                    return false;
                }
                var coords1 = BlockCoords.fromString(world, tilde.get(0));
                var coords2 = tilde.size() > 1 ? BlockCoords.fromString(world, tilde.get(1)) : null;
                if (coords2 == null) {
                    return coords0.toVector().equals(coords1.toVector());
                }
                return RayTrace.intersects(coords0.toVector(), new SBBoundingBox(coords1, coords2));
            } catch (Exception e) {
                try {
                    return b.getWorld().equals(Utils.getWorld(coords));
                } catch (NullPointerException e2) {
                    return false;
                }
            }
        })
        .filter(b -> {
            // ネームタグ検索
            return nameTag.isEmpty() ? true : Objects.equals(nameTag, b.getNameTag());
        }).collect(Collectors.toList());
    }

    @NotNull
    private ScriptKey getScriptKey(@NotNull GUIItem guiItem) {
        return ScriptKey.valueOf(GET_VALUE.apply(guiItem.getLore().get(0), true));
    }

    @NotNull
    private ScriptKey nextType(@NotNull ScriptKey scriptKey, int amount) {
        try {
            return ScriptKey.valueOf(scriptKey.ordinal() + amount);
        } catch (Exception e) {
            return amount >= 0 ? ScriptKey.INTERACT : ScriptKey.valueOf(ScriptKey.size() - 1);
        }
    }

    /**
     * ScriptBlockPlus ScriptJson クラス
     * @author yuttyann44581
     */
    final class ScriptJson {

        private final ScriptKey scriptKey;
        private final BlockCoords blockCoords;
        private final BlockScript blockScript;
        private final BlockScriptJson blockScriptJson;

        private ScriptJson(@NotNull BlockScript blockScript, @NotNull BlockScriptJson blockScriptJson) {
            this.scriptKey = blockScriptJson.getScriptKey();
            this.blockCoords = blockScript.getBlockCoords();
            this.blockScript = blockScript;
            this.blockScriptJson = blockScriptJson;
        }

        public boolean has() {
            return blockScriptJson.has(blockCoords);
        }

        @NotNull
        public ScriptKey getScriptKey() {
            return scriptKey;
        }

        @NotNull
        public BlockCoords getBlockCoords() {
            return blockCoords;
        }

        @NotNull
        public BlockScript getBlockScript() {
            return blockScript;
        }

        @NotNull
        public BlockScriptJson getBlockScriptJson() {
            return blockScriptJson;
        }

        @NotNull
        public List<String> createLore(@NotNull Player player) {
            var lore = new ArrayList<String>(8);
            lore.add("§eAuthor: §a" + blockScript.getAuthors().stream().map(Utils::getName).collect(Collectors.joining(", ")));
            lore.add("§eUpdate: §a" + blockScript.getLastEdit());
            lore.add("§eCoords: §a" + blockCoords.getFullCoords());
            lore.add("§eMyCount: §a" + PlayerCountJson.newJson(player.getUniqueId()).load(scriptKey, blockCoords).getAmount());
            lore.add("§eTagName: §" + (blockScript.getNameTag() == null ? "cNone" : "a" + blockScript.getNameTag()));
            lore.add("§eRedstone: §" + (blockScript.getSelector() == null ? "cfalse" : "atrue §d: §a" + blockScript.getSelector()));
            lore.add("§eScripts:");
            blockScript.getScripts().forEach(s -> lore.add("§6- §b" + s));
            return lore;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof ScriptJson) {
                var scriptJson = (ScriptJson) obj;
                return scriptKey.equals(scriptJson.scriptKey) && blockCoords.equals(scriptJson.blockCoords);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            int prime = 31;
            hash = prime * hash + scriptKey.hashCode();
            hash = prime * hash + blockCoords.hashCode();
            return hash;
        }
    }
}