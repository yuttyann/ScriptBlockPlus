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

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.script.option.chat.*;
import com.github.yuttyann.scriptblockplus.script.option.discord.DiscordChannel;
import com.github.yuttyann.scriptblockplus.script.option.discord.DiscordRole;
import com.github.yuttyann.scriptblockplus.script.option.discord.DiscordRoleAdd;
import com.github.yuttyann.scriptblockplus.script.option.discord.DiscordRoleRemove;
import com.github.yuttyann.scriptblockplus.script.option.other.*;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.script.option.vault.*;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * ScriptBlockPlus OptionManager クラス
 * @author yuttyann44581
 */
public final class OptionManager {

    private static final OptionMap OPTION_MAP = new OptionMap();
    private static final Comparator<? super String> SORT = (c1, c2) -> OPTION_MAP.getOption(c1).compareTo(OPTION_MAP.getOption(c2));

    static {
        register(PlayerAction::new);
        register(BlockType::new);
        register(Group::new);
        register(Perm::new);
        register(DiscordRole::new);
        register(DiscordChannel::new);
        register(IfAction::new);
        register(OldCooldown::new);
        register(Cooldown::new);
        register(Delay::new);
        register(ItemHand::new);
        register(ItemCost::new);
        register(MoneyCost::new);
        register(GroupAdd::new);
        register(GroupRemove::new);
        register(PermAdd::new);
        register(PermRemove::new);
        register(DiscordRoleAdd::new);
        register(DiscordRoleRemove::new);
        register(Say::new);
        register(Server::new);
        register(ToPlayer::new);
        register(PlaySound::new);
        register(Title::new);
        register(ActionBar::new);
        register(BypassOP::new);
        register(BypassPerm::new);
        register(BypassGroup::new);
        register(Command::new);
        register(Console::new);
        register(Execute::new);
        register(Amount::new);
    }

    public static void update() {
        OPTION_MAP.updateOrdinal();
    }

    private static void register(@NotNull Supplier<Option> newInstance) {
        OPTION_MAP.put(new SBInstance<Option>(newInstance));
    }

    public static void register(@NotNull OptionIndex optionIndex, @NotNull Supplier<Option> newInstance) {
        OPTION_MAP.put(optionIndex, new SBInstance<Option>(newInstance));
    }

    public static void unregister(@NotNull Class<? extends BaseOption> optionClass) {
        OPTION_MAP.remove(optionClass.getAnnotation(OptionTag.class).syntax());
    }

    public static void sort(@NotNull List<String> scripts) {
        if (!scripts.isEmpty()) { scripts.sort(SORT); }
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

    @NotNull
    public static OptionTag[] getTags() {
        return StreamUtils.toArray(OPTION_MAP.list(), s -> s.getDeclaringClass().getAnnotation(OptionTag.class), OptionTag[]::new);
    }
}