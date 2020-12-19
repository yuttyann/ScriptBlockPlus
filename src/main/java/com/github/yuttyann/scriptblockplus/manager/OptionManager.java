package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * ScriptBlockPlus OptionManager クラス
 * @author yuttyann44581
 */
public final class OptionManager {

    private static final OptionMap OPTION_MAP = new OptionMap();

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

    public static void register(@NotNull OptionIndex priority, @NotNull Class<? extends BaseOption> optionClass) {
        OPTION_MAP.put(priority, optionClass);
        OPTION_MAP.updateOrdinal();
    }

    public static void sort(@NotNull List<String> scripts) {
        scripts.sort(Comparator.comparingInt(s -> get(s).ordinal()));
    }

    public static boolean has(@NotNull String syntax) {
        return OPTION_MAP.values().stream().anyMatch(o -> o.isOption(syntax));
    }

    @NotNull
    private static Option get(@NotNull String syntax) {
        Optional<Option> option = OPTION_MAP.values().stream().filter(o -> o.isOption(syntax)).findFirst();
        return option.orElseThrow(() -> new NullPointerException("Option does not exist."));
    }

    @NotNull
    public static Option newInstance(@NotNull String syntax) {
        return get(syntax).newInstance();
    }

    @NotNull
    public static Option newInstance(@NotNull Class<? extends Option> optionClass, @NotNull InstanceType instanceType) {
        for (Option option : OPTION_MAP.values()) {
            if (!optionClass.equals(option.getClass())) {
                continue;
            }
            if (instanceType == InstanceType.REFLECTION) {
                return new SBConstructor<>(optionClass).newInstance(InstanceType.REFLECTION);
            }
            return option.newInstance();
        }
        throw new NullPointerException(optionClass.getName() + " does not exist");
    }

    @NotNull
    public static String[] getSyntaxs() {
        return StreamUtils.toArray(OPTION_MAP.list(), Option::getSyntax, new String[OPTION_MAP.size()]);
    }
}