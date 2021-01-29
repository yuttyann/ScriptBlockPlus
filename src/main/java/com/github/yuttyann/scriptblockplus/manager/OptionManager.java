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
package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
import com.github.yuttyann.scriptblockplus.script.option.chat.*;
import com.github.yuttyann.scriptblockplus.script.option.other.*;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.script.option.vault.*;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/**
 * ScriptBlockPlus OptionManager クラス
 * @author yuttyann44581
 */
public final class OptionManager {

    private static final OptionMap OPTION_MAP = new OptionMap();

    public static void registerDefaults() {
        OPTION_MAP.clear();
        register(() -> new ScriptAction());
        register(() -> new BlockType());
        register(() -> new Group());
        register(() -> new Perm());
        register(() -> new Calculation());
        register(() -> new OldCooldown());
        register(() -> new Cooldown());
        register(() -> new Delay());
        register(() -> new ItemHand());
        register(() -> new ItemCost());
        register(() -> new MoneyCost());
        register(() -> new GroupAdd());
        register(() -> new GroupRemove());
        register(() -> new PermAdd());
        register(() -> new PermRemove());
        register(() -> new Say());
        register(() -> new Server());
        register(() -> new ToPlayer());
        register(() -> new PlaySound());
        register(() -> new Title());
        register(() -> new ActionBar());
        register(() -> new BypassOP());
        register(() -> new BypassPerm());
        register(() -> new BypassGroup());
        register(() -> new Command());
        register(() -> new Console());
        register(() -> new Execute());
        register(() -> new Amount());
        OPTION_MAP.updateOrdinal();
    }

    private static void register(@NotNull Supplier<Option> newInstance) {
        OPTION_MAP.put(new SBInstance<Option>(newInstance));
    }

    public static void register(@NotNull OptionIndex optionIndex, @NotNull Supplier<Option> newInstance) {
        OPTION_MAP.put(optionIndex, new SBInstance<Option>(newInstance));
        OPTION_MAP.updateOrdinal();
    }

    public static void sort(@NotNull List<String> scripts) {
        scripts.sort((c1, c2) -> OPTION_MAP.getOption(c1).compareTo(OPTION_MAP.getOption(c2)));
    }

    public static boolean has(@NotNull String syntax) {
        return OPTION_MAP.getInstance(syntax) != null;
    }

    @NotNull
    public static Option newInstance(@NotNull String syntax) {
        var sbInstance = OPTION_MAP.getInstance(syntax);
        if (sbInstance == null) {
            throw new NullPointerException("Option[" + syntax + "] does not exist");
        }
        return sbInstance.newInstance();
    }

    @NotNull
    public static String[] getSyntaxs() {
        return StreamUtils.toArray(OPTION_MAP.list(), s -> s.get().getSyntax(), String[]::new);
    }
}