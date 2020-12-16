package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.enums.OptionPriority;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.chat.*;
import com.github.yuttyann.scriptblockplus.script.option.other.*;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.script.option.vault.*;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * ScriptBlockPlus OptionManager クラス
 * @author yuttyann44581
 */
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
        OPTION_MAP.put(new GroupAdd());
        OPTION_MAP.put(new GroupRemove());
        OPTION_MAP.put(new PermAdd());
        OPTION_MAP.put(new PermRemove());
        OPTION_MAP.put(new Say());
        OPTION_MAP.put(new Server());
        OPTION_MAP.put(new ToPlayer());
        OPTION_MAP.put(new PlaySound());
        OPTION_MAP.put(new Title());
        OPTION_MAP.put(new ActionBar());
        OPTION_MAP.put(new Bypass());
        OPTION_MAP.put(new Command());
        OPTION_MAP.put(new Console());
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
        scripts.sort(Comparator.comparingInt(s1 -> get(s1).ordinal()));
    }

    public static boolean has(@NotNull String syntax) {
        return StreamUtils.fOrElse(OPTION_MAP.sValues(), o -> o.isOption(syntax), null) != null;
    }

    @NotNull
    public static Option get(@NotNull String syntax) {
        Optional<Option> value = Optional.ofNullable(StreamUtils.fOrElse(OPTION_MAP.sValues(), o -> o.isOption(syntax), null));
        return value.orElseThrow(() -> new NullPointerException("Option does not exist."));
    }

    @NotNull
    public static Option newInstance(@NotNull Class<? extends Option> option, @NotNull InstanceType instanceType) {
        for (Option value : OPTION_MAP.sValues()) {
            if (!option.equals(value.getClass())) {
                continue;
            }
            if (instanceType == InstanceType.REFLECTION) {
                return new SBConstructor<>(option).newInstance(InstanceType.REFLECTION);
            }
            return value.newInstance();
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
}