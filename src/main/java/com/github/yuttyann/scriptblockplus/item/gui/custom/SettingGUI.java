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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.hook.nms.AnvilGUI.AnvilBuilder;
import com.github.yuttyann.scriptblockplus.hook.nms.AnvilGUI.Response;
import com.github.yuttyann.scriptblockplus.item.gui.CustomGUI;
import com.github.yuttyann.scriptblockplus.item.gui.GUIItem;
import com.github.yuttyann.scriptblockplus.item.gui.UserWindow;
import com.github.yuttyann.scriptblockplus.item.gui.custom.SearchGUI.ScriptJson;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.selector.CommandSelector;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils.TriConsumer;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus SettingGUI クラス
 * @author yuttyann44581
 */
public final class SettingGUI extends CustomGUI {

    private static final int[] SLOTS =  { 4, 21, 22, 23, 25, 30, 31, 32, 34, 49 };

    private static final int LIMIT = Utils.isCBXXXorLater("1.17") ? 50 : 35;

    private static final String KEY_LORE = Utils.randomUUID(), KEY_ANVIL = Utils.randomUUID(), KEY_SCRIPTS = Utils.randomUUID();

    private final Set<ScriptJson> OPENED = new HashSet<>();

    private final TriConsumer<UserWindow, Function<ScriptJson, String>, BiFunction<String, ScriptJson, Response>> ANVIL_OPEN = (w, f, r) -> {
        var scriptJson = getScriptJson(w);
        if (scriptJson == null) {
            return;
        }
        var input = f.apply(scriptJson);
        var sbPlayer = w.getSBPlayer();
        if (StringUtils.isNotEmpty(input) && input.length() > LIMIT) {
            sbPlayer.sendMessage(SBConfig.GUI_SYS_OVERFLOW.setColor());
        }
        sbPlayer.getObjectMap().put(KEY_ANVIL, true);
        try {
            new AnvilBuilder()
            .title(SBConfig.GUI_SYS_INPUT.setColor())
            .text(def(input, "§r"))
            .onClose(p -> {
                ScriptBlock.getScheduler().run(() -> {
                    if (scriptJson.has()) {
                        w.openGUI();
                    }
                    if (!sbPlayer.isOnline()) {
                        onClosed(w);
                    }
                });
            })
            .onUpdate((p, i) -> ItemUtils.setLore(i, null))
            .onComplete((p, t) -> {
                if (scriptJson.has()) {
                    playSoundEffect(sbPlayer);
                    return r.apply(t, scriptJson);
                } else {
                    sbPlayer.getPlayer().closeInventory();
                    return Response.close();
                }
            })
            .left(GUIItem.PAPER.toBukkit())
            .onLeftClick(p -> {
                if (scriptJson.has()) {
                    playSoundEffect(sbPlayer);
                    r.apply(null, scriptJson);
                }
                sbPlayer.getPlayer().closeInventory();
            })
            .open(sbPlayer.getPlayer());
        } finally {
            sbPlayer.getObjectMap().put(KEY_ANVIL, false);
        }
    };

    {
        for (int i = 0; i < getSize(); i++) {
            if (!ArrayUtils.contains(SLOTS, i)) {
                setItem(i, GUIItem.BLACK);
            }
        }
    }

    public SettingGUI() {
        super(SBConfig.GUI_SYS_SETTINGGUI.setColor(), 6, true);
        setSoundEffect(Sound.UI_BUTTON_CLICK, 1, 1);
    }

    @Override
    public void onLoaded(@NotNull UserWindow window) {
        window.setItem(SLOTS[0], new GUIItem(1, Material.BARRIER, SBConfig.GUI_SETTING_DELETE.setColor(), null, (w, g, c) -> {
            var scriptJson = getScriptJson(w);
            if (scriptJson != null) {
                scriptJson.getBlockScriptJson().init(scriptJson.getBlockCoords());
                scriptJson.getBlockScriptJson().remove(scriptJson.getBlockCoords());
                scriptJson.getBlockScriptJson().saveJson();
                getWindow(SearchGUI.class, w.getSBPlayer()).ifPresent(w::shiftWindow);
            }
        }));
        window.setItem(SLOTS[9], new GUIItem(1, Material.ARROW, SBConfig.GUI_SETTING_CLOSE.setColor(), null, (w, g, c) -> {
            getWindow(SearchGUI.class, w.getSBPlayer()).ifPresent(w::shiftWindow);
        }));

        window.setItem(SLOTS[4], new GUIItem(1, Material.BOOK, SBConfig.GUI_SETTING_COPY.setColor(), null, (w, g, c) -> {
            var scriptJson = getScriptJson(w);
            if (scriptJson == null) {
                return;
            }
            var sbPlayer = w.getSBPlayer();
            if (sbPlayer.getSBClipboard().isPresent()) {
                g.setEnchant(false);
                sbPlayer.setSBClipboard(null);
            } else {
                g.setEnchant(true);
                new SBClipboard(sbPlayer, scriptJson.getScriptKey(), scriptJson.getBlockCoords()).copy();
            }
            w.setItem(SLOTS[4], g);
        }));
        window.setItem(SLOTS[8], new GUIItem(1, ItemUtils.getWritableBookMaterial(), SBConfig.GUI_SETTING_PASTE.setColor(), null, (w, g, c) -> {
            var scriptJson = getScriptJson(w);
            if (scriptJson != null) {
                w.getSBPlayer().getSBClipboard().ifPresent(s -> { s.paste(scriptJson.getBlockCoords(), true); update(w, scriptJson); });
            }
        }));

        window.setItem(SLOTS[5], new GUIItem(1, Material.NETHER_STAR, SBConfig.GUI_SETTING_TELEPORT.setColor(), null, (w, g, c) -> {
            var scriptJson = getScriptJson(w);
            if (scriptJson != null) {
                var location = scriptJson.getBlockScript().getBlockCoords().toLocation();
                w.getSBPlayer().getPlayer().teleport(location, TeleportCause.PLUGIN);
            }
        }));
        window.setItem(SLOTS[6], new GUIItem(1, Material.END_CRYSTAL, SBConfig.GUI_SETTING_EXECUTE.setColor(), null, (w, g, c) -> {
            var scriptJson = getScriptJson(w);
            if (scriptJson != null) {
                new ScriptRead(w.getSBPlayer(), scriptJson.getBlockCoords(), scriptJson.getScriptKey()).read(0);
                w.setItem(SLOTS[7], w.getItem(SLOTS[7]).setLore(scriptJson.createLore(w.getSBPlayer().getPlayer())));
            }
        }));
        window.setItem(SLOTS[7], new GUIItem(1, Material.ENCHANTED_BOOK, SBConfig.GUI_SETTING_VIEW.setColor(), null, (w, g, c) -> {
            Optional.ofNullable(getScriptJson(w)).ifPresent(s -> update(w, s));
        }));

        // AnvilGUI
        window.setItem(SLOTS[1], new GUIItem(1, Material.REDSTONE_BLOCK, SBConfig.GUI_SETTING_REDSTONE.setColor(), null, (w, g, c) -> {
            ANVIL_OPEN.accept(w, s -> s.getBlockScript().getSelector(), (t, s) -> {
                var text = ChatColor.stripColor(t);
                if (StringUtils.isNotEmpty(text) && !CommandSelector.has(text)) {
                    text = text.trim() + " @p";
                }
                s.getBlockScript().setSelector(def(text, null));
                s.getBlockScriptJson().saveJson();
                return Response.close();
            });
        }));
        window.setItem(SLOTS[2], new GUIItem(1, ItemUtils.getCommandMaterial(), SBConfig.GUI_SETTING_SCRIPT.setColor(), null, (w, g, c) -> {
            if (c == ClickType.LEFT) {
                w.setItem(SLOTS[2], g.setLore(createLore(w.getSBPlayer(), getScriptJson(w), 1)));
                return;
            }
            var index = w.getSBPlayer().getObjectMap().getInt(KEY_LORE);
            @SuppressWarnings("unchecked")
            var scripts = (List<String>) w.getSBPlayer().getObjectMap().get(KEY_SCRIPTS);
            ANVIL_OPEN.accept(w, s -> scripts.get(index), (t, s) -> {
                var text = ChatColor.stripColor(t);
                if (text == null) {
                    if (scripts.size() == 1) {
                        s.getBlockScriptJson().init(s.getBlockCoords());
                        s.getBlockScriptJson().remove(s.getBlockCoords());
                        s.getBlockScriptJson().saveJson();
                        return Response.close();
                    }
                    scripts.remove(index);
                } else {
                    scripts.set(index, text);
                }
                s.getBlockScript().setScripts(scripts);
                s.getBlockScriptJson().saveJson();
                return Response.close();
            });
        }));
        window.setItem(SLOTS[3], new GUIItem(1, Material.NAME_TAG, SBConfig.GUI_SETTING_NAMETAG.setColor(), null, (w, g, c) -> {
            ANVIL_OPEN.accept(w, s -> s.getBlockScript().getNameTag(), (t, s) -> {
                var text = ChatColor.stripColor(t);
                s.getBlockScript().setNameTag(def(text, null));
                s.getBlockScriptJson().saveJson();
                return Response.close();
            });
        }));
    }

    @Override
    public void onOpened(@NotNull UserWindow window) {
        var scriptJson = getScriptJson(window);
        if (scriptJson == null) {
            return;
        }
        OPENED.add(scriptJson);
        update(window, scriptJson);
    }

    @Override
    public void onClosed(@NotNull UserWindow window) {
        var objectMap = window.getSBPlayer().getObjectMap();
        if (!objectMap.getBoolean(KEY_ANVIL)) {
            Optional.ofNullable(objectMap.get(SearchGUI.KEY_SCRIPT)).ifPresent(OPENED::remove);
        }
    }

    public boolean isOpened(@NotNull ScriptJson scriptJson) {
        return OPENED.contains(scriptJson);
    }

    @NotNull
    public List<String> createLore(@NotNull SBPlayer sbPlayer, @NotNull ScriptJson scriptJson, final int amount) {
        var lore = new ArrayList<String>();
        var scripts = getScripts(sbPlayer, scriptJson.getBlockScript());
        var objectMap = sbPlayer.getObjectMap();
        scripts.forEach(s -> lore.add(s.length() > LIMIT ? "   §c[" + s + "]" : "   §7[" + s + "]"));
        if (lore.size() == 1 || amount == 0) {
            lore.set(0, SearchGUI.TEXT + (scripts.get(0).length() > LIMIT ? "§c" : "§b") + ChatColor.stripColor(lore.get(0).trim()));
            objectMap.put(KEY_LORE, 0);
        } else if (lore.size() > 1) {
            var index = objectMap.getInt(KEY_LORE);
            var next = index + 1 >= lore.size() ? 0 : index + 1;
            var prev = StringUtils.removeStart(lore.get(index).trim(), SearchGUI.TEXT);
            lore.set(next, SearchGUI.TEXT + (scripts.get(next).length() > LIMIT ? "§c" : "§b") + ChatColor.stripColor(lore.get(next).trim()));
            lore.set(index, scripts.get(index).length() > LIMIT ? "   §c" + prev : "   §7" + prev);
            objectMap.put(KEY_LORE, next);
        }
        return lore;
    }

    @NotNull
    private List<String> getScripts(@NotNull SBPlayer sbPlayer, @NotNull BlockScript blockScript) {
        var scripts = new ArrayList<String>();
        for (var script : blockScript.getScripts()) {
            StringUtils.parseScript(script).forEach(scripts::add);
        }
        sbPlayer.getObjectMap().put(KEY_SCRIPTS, scripts);
        return scripts;
    }

    @Nullable
    private ScriptJson getScriptJson(@NotNull UserWindow window) {
        var objectMap = window.getSBPlayer().getObjectMap();
        var scriptJson = (ScriptJson) objectMap.get(SearchGUI.KEY_SCRIPT);
        if (scriptJson == null || !scriptJson.has()) {
            if (scriptJson != null) {
                OPENED.remove(scriptJson);
            }
            objectMap.remove(KEY_LORE);
            objectMap.remove(KEY_ANVIL);
            objectMap.remove(KEY_SCRIPTS);
            objectMap.remove(SearchGUI.KEY_SCRIPT);
            window.closeGUI();
            return null;
        }
        return scriptJson;
    }

    @Nullable
    private String def(@NotNull String source, @Nullable String def) {
        return StringUtils.isEmpty(source) ? def : source;
    }

    private void update(@NotNull UserWindow window, @NotNull ScriptJson scriptJson) {
        window.setItem(SLOTS[1], window.getItem(SLOTS[1]).setLore(SearchGUI.TEXT + def(scriptJson.getBlockScript().getSelector(), "")));
        window.setItem(SLOTS[2], window.getItem(SLOTS[2]).setLore(createLore(window.getSBPlayer(), scriptJson, 0)));
        window.setItem(SLOTS[3], window.getItem(SLOTS[3]).setLore(SearchGUI.TEXT + def(scriptJson.getBlockScript().getNameTag(), "")));
        window.setItem(SLOTS[4], window.getItem(SLOTS[4]).setEnchant(window.getSBPlayer().getSBClipboard().isPresent()));
        window.setItem(SLOTS[7], window.getItem(SLOTS[7]).setLore(scriptJson.createLore(window.getSBPlayer().getPlayer())));
    }
}