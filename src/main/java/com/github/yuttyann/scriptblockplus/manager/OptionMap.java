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
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * ScriptBlockPlus OptionMap クラス
 * @author yuttyann44581
 */
public final class OptionMap {

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

    private final List<String> SYNTAXES = new ArrayList<>();
    private final Map<String, SBInstance<Option>> SBINSTANCES = new HashMap<>();

    @NotNull
    public Option getOption(@NotNull String syntax) {
        var sbInstance = getInstance(syntax);
        return Objects.requireNonNull(sbInstance, "Option[" + syntax + "] does not exist").get();
    }

    @Nullable
    public SBInstance<Option> getInstance(@NotNull String syntax) {
        for (int i = 0; i < SYNTAXES.size(); i++) {
            var value = SYNTAXES.get(i);
            if (syntax.indexOf(value) == 0) {
                return SBINSTANCES.get(value);
            }
        }
        return null;
    }

    @Nullable
    public SBInstance<Option> put(@NotNull SBInstance<Option> sbInstance) {
        var syntax = sbInstance.get().getSyntax();
        if (!SBINSTANCES.containsKey(syntax)) {
            SYNTAXES.add(syntax);
        }
        return SBINSTANCES.put(syntax, sbInstance);
    }

    @Nullable
    public SBInstance<Option> put(@NotNull OptionIndex optionIndex, @NotNull SBInstance<Option> sbInstance) {
        return SBINSTANCES.put(addSyntax(optionIndex, sbInstance.getDeclaringClass()), sbInstance);
    }

    @NotNull
    private String addSyntax(@NotNull OptionIndex optionIndex, @NotNull Class<Option> optionClass) {
        var syntax = optionClass.getAnnotation(OptionTag.class).syntax();
        if (!SBINSTANCES.containsKey(syntax)) {
            switch (optionIndex.getIndexType()) {
                case TOP:
                    SYNTAXES.add(0, syntax);
                    break;
                case LAST:
                    SYNTAXES.add(syntax);
                    break;
                default:
                    int index = SYNTAXES.indexOf(optionIndex.getSyntax());
                    int amount = optionIndex.getIndexType().getAmount();
                    SYNTAXES.add(Math.min(Math.max(index + amount, 0), SYNTAXES.size()), syntax);
                    break;
            }
        }
        return syntax;
    }

    public void remove(@NotNull Object key) {
        SYNTAXES.remove(key);
        SBINSTANCES.remove(key);
    }

    public void clear() {
        SYNTAXES.clear();
        SBINSTANCES.clear();
    }

    @NotNull
    public List<SBInstance<Option>> list() {
        var list = new ArrayList<SBInstance<Option>>(SBINSTANCES.values());
        list.sort((c1, c2) -> c1.get().compareTo(c2.get()));
        return list;
    }

    @NotNull
    public Collection<SBInstance<Option>> values() {
        return SBINSTANCES.values();
    }

    public synchronized void updateOrdinal() {
        try {
            int index = 0;
            for (String syntax : SYNTAXES) {
                ORDINAL.setInt(SBINSTANCES.get(syntax).get(), index++);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}