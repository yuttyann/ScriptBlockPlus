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
package com.github.yuttyann.scriptblockplus.item.action;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.item.RunItem;
import com.github.yuttyann.scriptblockplus.item.gui.CustomGUI;
import com.github.yuttyann.scriptblockplus.item.gui.UserWindow;
import com.github.yuttyann.scriptblockplus.item.gui.custom.SearchGUI;
import com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft;

/**
 * ScriptBlockPlus ScriptManager クラス
 * @author yuttyann44581
 */
public class ScriptManager extends ItemAction {

    public ScriptManager() {
        super(Material.BOOK, () -> "§dScript Manager", SBConfig.SCRIPT_MANAGER::setListColor);
        setItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public void run(@NotNull RunItem runItem) {
        switch (runItem.getAction()) {
            case RIGHT_CLICK_AIR: case RIGHT_CLICK_BLOCK:
                if (!NetMinecraft.hasNMS()) {
                    runItem.getSBPlayer().sendMessage("§c" + NetMinecraft.WARNING_TEXT);
                    return;
                }
                CustomGUI.getWindow(SearchGUI.class, runItem.getSBPlayer()).ifPresent(UserWindow::open);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_SCRIPT_MANAGER.has(permissible);
    }
}