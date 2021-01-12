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

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * ScriptBlockPlus OptionMap クラス
 * @author yuttyann44581
 */
@SuppressWarnings("serial")
public final class OptionMap extends HashMap<String, Option> {

    private static final Field ORDINAL;

    static {
        var field = (Field) null;
        try {
            field = Option.class.getDeclaredField("ordinal");
            field.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        } finally {
            ORDINAL = field;
        }
    }

    private final LinkedList<String> LINKED_LIST = new LinkedList<>();

    @Nullable
    public <T extends BaseOption> Option put(@NotNull T option) {
        var syntax = option.getSyntax();
        if (!containsKey(syntax)) {
            LINKED_LIST.add(syntax);
        }
        return super.put(syntax, option);
    }

    @Nullable
    public Option put(@NotNull OptionIndex optionIndex, @NotNull Class<? extends BaseOption> optionClass) {
        var syntax = addSyntax(optionIndex, optionClass.getAnnotation(OptionTag.class).syntax());
        return super.put(syntax, new SBConstructor<>(optionClass).newInstance(InstanceType.REFLECTION));
    }

    @NotNull
    private String addSyntax(@NotNull OptionIndex optionIndex, @NotNull String syntax) {
        if (!containsKey(syntax)) {
            switch (optionIndex.getIndexType()) {
                case TOP:
                    LINKED_LIST.addFirst(syntax);
                    break;
                case LAST:
                    LINKED_LIST.addLast(syntax);
                    break;
                default:
                    int index = LINKED_LIST.indexOf(optionIndex.getSyntax());
                    int amount = optionIndex.getIndexType().getAmount();
                    LINKED_LIST.add(Math.min(Math.max(index + amount, 0), LINKED_LIST.size()), syntax);
                    break;
            }
        }
        return syntax;
    }

    @Override
    @Deprecated
    public Option put(@Nullable String key, @Nullable Option value) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public List<Option> list() {
        var list = new ArrayList<Option>(values());
        list.sort(Option::compareTo);
        return list;
    }

    void updateOrdinal() {
        try {
            int index = 0;
            for (String syntax : LINKED_LIST) {
                ORDINAL.setInt(super.get(syntax), index++);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}