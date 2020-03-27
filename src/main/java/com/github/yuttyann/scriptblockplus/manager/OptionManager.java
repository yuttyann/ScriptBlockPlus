package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.enums.OptionPriority;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBConstructor;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.chat.*;
import com.github.yuttyann.scriptblockplus.script.option.other.*;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.script.option.vault.*;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

public final class OptionManager {

    private static final IndexedLinkedMap OPTION_MAP = new IndexedLinkedMap();

    static {
        OPTION_MAP.put(new ScriptAction());
        OPTION_MAP.put(new BlockType());
        OPTION_MAP.put(new Group());
        OPTION_MAP.put(new Perm());
        OPTION_MAP.put(new Calculation());
        OPTION_MAP.put(new OldCooldown());
        OPTION_MAP.put(new Cooldown());
        OPTION_MAP.put(new Delay());
        OPTION_MAP.put(new ItemHand());
        OPTION_MAP.put(new ItemCost());
        OPTION_MAP.put(new MoneyCost());
        OPTION_MAP.put(new Say());
        OPTION_MAP.put(new Server());
        OPTION_MAP.put(new ToPlayer());
        OPTION_MAP.put(new PlaySound());
        OPTION_MAP.put(new Title());
        OPTION_MAP.put(new ActionBar());
        OPTION_MAP.put(new Bypass());
        OPTION_MAP.put(new Command());
        OPTION_MAP.put(new Console());
        OPTION_MAP.put(new GroupAdd());
        OPTION_MAP.put(new GroupRemove());
        OPTION_MAP.put(new PermAdd());
        OPTION_MAP.put(new PermRemove());
        OPTION_MAP.put(new Execute());
        OPTION_MAP.put(new Amount());
        OPTION_MAP.updateOrdinal();
    }

    public static void register(@NotNull OptionPriority priority, @NotNull SBConstructor<Option> option) {
        Option instance = option.newInstance(InstanceType.REFLECTION);
        OPTION_MAP.put(priority, instance.getSyntax(), instance);
        OPTION_MAP.updateOrdinal();
    }

    public static void sort(@NotNull List<String> scripts) {
        scripts.sort(Comparator.comparingInt(s1 -> OPTION_MAP.indexOf(s1::startsWith)));
    }

    public static boolean has(@NotNull String syntax) {
        int index = OPTION_MAP.indexOf(syntax::startsWith);
        return index >= 0 && index < OPTION_MAP.list.size();
    }

    @NotNull
    public static Option get(@NotNull String syntax) {
        Option value = OPTION_MAP.get(OPTION_MAP.indexOf(syntax::startsWith));
        if (value == null) {
            throw new NullPointerException("Option does not exist.");
        }
        return value;
    }

    @NotNull
    public static Option newInstance(@NotNull Class<? extends Option> option, @NotNull InstanceType instanceType) {
        for (Entry<String, Option> entry : OPTION_MAP.entrySet()) {
            if (!option.equals(entry.getValue().getClass())) {
                continue;
            }
            if (instanceType == InstanceType.REFLECTION) {
                return new SBConstructor<>(option).newInstance(InstanceType.REFLECTION);
            }
            return entry.getValue().newInstance();
        }
        throw new NullPointerException(option.getName() + " does not exist");
    }

    @NotNull
    public static List<Option> getList() {
        return Collections.unmodifiableList(OPTION_MAP.values());
    }

    @NotNull
    public static String[] getSyntaxs() {
        return StreamUtils.toArray(OPTION_MAP.values(), Option::getSyntax, new String[OPTION_MAP.size()]);
    }

    private static class IndexedLinkedMap extends LinkedHashMap<String, Option> {

        private final LinkedList<String> list = new LinkedList<>();

        public int indexOf(@NotNull Predicate<String> filter) {
            int index = 0;
            for (String s : list) {
                if (filter.test(s)) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        @Nullable
        public Option get(int index) {
            return index >= 0 && index < list.size() ? super.get(list.get(index)) : null;
        }

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
                case LOWEST:
                case LOW:
                case NORMAL:
                case HIGH:
                case HIGHEST:
                    list.add(list.indexOf(priority.getSyntax()) + 1, key);
                    break;
                case TOP:
                    list.addFirst(key);
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

        private void updateOrdinal() {
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
}