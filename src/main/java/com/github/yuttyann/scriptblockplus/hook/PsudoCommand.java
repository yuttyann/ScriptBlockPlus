package com.github.yuttyann.scriptblockplus.hook;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScriptBlockPlus PsudoCommand クラス
 * @author yuttyann44581
 */
public class PsudoCommand {

    public static final boolean HAS = Bukkit.getPluginManager().isPluginEnabled("PsudoCommand");
    public static final PsudoCommand INSTANCE = new PsudoCommand();

    private final static Method GET_TARGETS = getTargets();
    private final static Method GET_INT_RELATIVE = getIntRelative();
    private final static String[] SELECTOR_NAMES = { "@p", "@a", "@e", "@r" };

    private static class Index {

        private final int start;
        private int end = 0;

        private Index(int start) {
            this.start = start;
        }

        @NotNull
        private String substring(@NotNull String source) {
            return source.substring(start, Math.min(end + 1, source.length()));
        }
    }

    private PsudoCommand() { }

    @NotNull
    public List<StringBuilder> build(@NotNull CommandSender sender, @NotNull String command) {
        if (GET_TARGETS == null || GET_INT_RELATIVE == null) {
            return Collections.singletonList(new StringBuilder(command));
        }
        String[] args = StringUtils.split(command, " ");
        List<StringBuilder> builderList = new ArrayList<>();
        builderList.add(new StringBuilder());
        for (int i = 0, j = 0; i < args.length; i++) {
            List<StringBuilder> tempBuilderList = new ArrayList<>();
            for (StringBuilder builder : builderList) {
                if (args[i].startsWith("~") || args[i].startsWith("^")) {
                    int next = i + 1;
                    if (next  < args.length && !args[next].startsWith("~") && (j == 0 || j >= 3)) {
                        builder.append(sender.getName());
                    } else {
                        if (j == 0) {
                            builder.append(getIntRelative(args[i], "x", (Entity) sender));
                        }
                        if (j == 1) {
                            builder.append(getIntRelative(args[i], "y", (Entity) sender));
                        }
                        if (j == 2) {
                            builder.append(getIntRelative(args[i], "z", (Entity) sender));
                        }
                        j++;
                    }
                } else if (StreamUtils.anyMatch(SELECTOR_NAMES, args[i]::contains)) {
                    List<String> stringList = new ArrayList<>();
                    List<Index> indexList = new ArrayList<>();
                    String source = replace(args[i].toCharArray(), indexList).toString();
                    for (int k = 0; k < indexList.size(); k++) {
                        String selector = indexList.get(k).substring(args[i]);
                        Entity[] entities = getTargets(sender, selector);
                        if  ((entities == null || entities.length == 0) && selector.startsWith("@p") && sender instanceof Player) {
                            entities = new Entity[] { (Entity) sender };
                        }
                        if (entities == null) {
                            continue;
                        }
                        boolean works = true;
                        for (int l = 1; l < entities.length; l++) {
                            if (entities[l] == null) {
                                works = false;
                                break;
                            }
                            stringList.add(StringUtils.replace(source, "{" + k + "}", getName(entities[l])));
                        }
                        if (!works || entities.length == 0 || entities[0] == null) {
                            return Collections.singletonList(new StringBuilder(command));
                        } else {
                            String name = getName(entities[0]);
                            for (int l = 0; l < stringList.size(); l++) {
                                stringList.set(l, StringUtils.replace(stringList.get(l), "{" + k + "}", name));
                            }
                            source = StringUtils.replace(source, "{" + k + "}", name);
                        }
                    }
                    for (String arg : stringList) {
                        StringBuilder tempBuilder = new StringBuilder(builder.toString());
                        tempBuilder.append(arg);
                        if (i != (args.length - 1)) {
                            tempBuilder.append(" ");
                        }
                        tempBuilderList.add(tempBuilder);
                    }
                    builder.append(source);
                } else {
                    builder.append(args[i]);
                }
                if (i + 1 < args.length) {
                    builder.append(" ");
                }
            }
            if (tempBuilderList.size() > 0) {
                builderList.addAll(tempBuilderList);
                tempBuilderList.clear();
            }
        }
        return builderList;
    }

    @NotNull
    private StringBuilder replace(char[] chars, @NotNull List<Index> indexList) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, n = 0; i < chars.length; i++) {
            int type = i + 1, tag = i + 2;
            if (chars[i] == '@' && type < chars.length && "aepr".indexOf(chars[type]) > -1) {
                Index textIndex = new Index(i);
                textIndex.end = type;
                if (tag < chars.length && chars[tag] == '[') {
                    for (int m = tag + 1; m < chars.length; m++) {
                        if (chars[m] == '[') {
                            break;
                        } else if (chars[m] == ']') {
                            textIndex.end = m;
                            i += Math.max(textIndex.end - textIndex.start, 0);
                            break;
                        }
                    }
                } else {
                    i++;
                }
                indexList.add(textIndex);
                builder.append('{').append(n++).append('}');
            } else {
                builder.append(chars[i]);
            }
        }
        return builder;
    }

    @NotNull
    private String getName(@NotNull Entity entity) {
        return entity.getCustomName() == null ? entity.getName() : entity.getCustomName();
    }

    @NotNull
    private Entity[] getTargets(@NotNull CommandSender sender, @NotNull String selector) {
        try {
            return GET_TARGETS == null ? new Entity[] { } : (Entity[]) GET_TARGETS.invoke(null, sender, selector);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new Entity[] { };
    }

    private int getIntRelative(@NotNull String target, @NotNull String relative, @NotNull Entity entity) {
        try {
            return GET_INT_RELATIVE == null ? 0 : (int) GET_INT_RELATIVE.invoke(null, target, relative, entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Nullable
    private static Method getIntRelative() {
        try {
            Class<?> commandUtils = Class.forName("me.zombie_striker.psudocommands.CommandUtils");
            return commandUtils.getMethod("getIntRelative", String.class, String.class, Entity.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return null;
        }

    }

    @Nullable
    private static Method getTargets() {
        try {
            Class<?> commandUtils = Class.forName("me.zombie_striker.psudocommands.CommandUtils");
            return commandUtils.getMethod("getTargets", CommandSender.class, String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return null;
        }
    }
}