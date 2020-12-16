package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.enums.OptionPriority;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

class IndexedLinkedMap extends HashMap<String, Option> {

    private final LinkedList<String> list = new LinkedList<>();

    @Nullable
    public Option put(@NotNull Option option) {
        list.add(option.getSyntax());
        return super.put(option.getSyntax(), option);
    }

    @Nullable
    public Option put(@NotNull OptionPriority priority, @NotNull String key, @NotNull Option value) {
        switch (priority) {
            case LAST:
                list.addLast(key);
                break;
            case TOP:
                list.addFirst(key);
                break;
            default:
                list.add(list.indexOf(priority.getSyntax()) + 1, key);
                break;
        }
        return super.put(key, value);
    }

    @Override
    @Deprecated
    public Option put(String key, Option value) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public List<Option> values() {
        List<Option> list = new ArrayList<>(super.values());
        list.sort(Option::compareTo);
        return list;
    }

    @NotNull
    Collection<Option> sValues() {
        return super.values();
    }

    void updateOrdinal() {
        try {
            Field field = Option.class.getDeclaredField("ordinal");
            field.setAccessible(true);
            int index = 0;
            for (String syntax : list) {
                field.setInt(super.get(syntax), index++);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
