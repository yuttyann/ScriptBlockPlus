package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.enums.OptionPriority;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBConstructor;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
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

    private static final PriorityMap OPTION_MAP = new PriorityMap();

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

    public static void register(@NotNull OptionPriority priority, @NotNull Class<? extends BaseOption> option) {
        Option instance = new SBConstructor<>(option).newInstance(InstanceType.REFLECTION);
        OPTION_MAP.put(priority, instance.getSyntax(), instance);
        OPTION_MAP.updateOrdinal();
    }

    public static void sort(@NotNull List<String> scripts) {
        scripts.sort(Comparator.comparingInt(s1 -> OPTION_MAP.list.indexOf(s1::startsWith)));
    }

    @NotNull
    public static Option newInstance(@NotNull String syntax) {
        return Objects.requireNonNull(get(syntax)).newInstance();
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

    @Nullable
    public static Option get(@NotNull String syntax) {
        return OPTION_MAP.get(OPTION_MAP.list.indexOf(syntax::startsWith));
    }

    @NotNull
    public static List<Option> getList() {
        return Collections.unmodifiableList(OPTION_MAP.values());
    }

    @NotNull
    public static String[] getNames() {
        return StreamUtils.toArray(OPTION_MAP.values(), Option::getName, new String[OPTION_MAP.size()]);
    }

    @NotNull
    public static String[] getSyntaxs() {
        return StreamUtils.toArray(OPTION_MAP.values(), Option::getSyntax, new String[OPTION_MAP.size()]);
    }

    private static class PriorityMap extends LinkedHashMap<String, Option> {

        private final StringList list = new StringList();

        @Nullable
        public Option get(int index) {
            return super.get(list.get(index));
        }

        @Override
        public Option get(Object key) {
            return super.get(key);
        }

        @Nullable
        public Option put(@NotNull Option option) {
            return put(option.getSyntax(), option);
        }

        @Override
        @Nullable
        public Option put(@NotNull String key, @NotNull Option value) {
            list.add(key);
            return super.put(key, value);
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
                    field.setInt(get(syntax), index++);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static class StringList extends LinkedList<String> {

        public int indexOf(@NotNull Predicate<String> filter) {
            int index = 0;
            for (String s : this) {
                if (filter.test(s)) {
                    return index;
                }
                index++;
            }
            return -1;
        }
    }
}