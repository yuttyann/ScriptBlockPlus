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

import java.util.concurrent.atomic.AtomicInteger;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.item.gui.CustomGUI;
import com.github.yuttyann.scriptblockplus.item.gui.GUIItem;
import com.github.yuttyann.scriptblockplus.item.gui.UserWindow;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ToolBoxGUI クラス
 * @author yuttyann44581
 */
public final class ToolBoxGUI extends CustomGUI {

    public ToolBoxGUI() {
        super(SBConfig.GUI_SYS_TOOLBOXGUI::setColor, 3, true);
        setSoundEffect(Sound.ENTITY_HORSE_SADDLE, 1, 1);
    }

    @Override
    public void onLoaded(@NotNull UserWindow window) { }

    @Override
    public void onOpened(@NotNull UserWindow window) {
        var index = new AtomicInteger();
        ItemAction.getItems().forEach(i -> {
            if (i.hasPermission(window.getSBPlayer())) {
                window.setItem(index.getAndIncrement(), new GUIItem(i.getItem(),
                (w, g, c) -> w.getSBPlayer().getInventory().addItem(g.toBukkit().clone())));
            }
        });
    }

    @Override
    public void onClosed(@NotNull UserWindow window) { }
}