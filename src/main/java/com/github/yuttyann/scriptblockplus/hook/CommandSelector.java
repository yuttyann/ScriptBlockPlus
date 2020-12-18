package com.github.yuttyann.scriptblockplus.hook;

import com.github.yuttyann.scriptblockplus.hook.plugin.PsudoCommand;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ScriptBlockPlus CommandSelector クラス
 * @author yuttyann44581
 */
public final class CommandSelector {

    public static final CommandSelector INSTANCE = new CommandSelector();

    private final static String SELECTOR_SUFFIX = "paers";
    private final static String[] SELECTOR_NAMES = { "@p", "@a", "@e", "@r", "@s" };

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

    private CommandSelector() { }

    public boolean has(@NotNull String command) {
        return Arrays.stream(SELECTOR_NAMES).anyMatch(command::contains);
    }

    @NotNull
    public List<StringBuilder> build(@NotNull CommandSender sender, @NotNull String command) {
        String[] args = StringUtils.split(command, " ");
        List<StringBuilder> builderList = new ArrayList<>();
        builderList.add(new StringBuilder());
        for (int i = 0, j = 0; i < args.length; i++) {
            List<StringBuilder> tempBuilderList = new ArrayList<>();
            for (StringBuilder builder : builderList) {
                int next = i + 1;
                if (args[i].startsWith("~") || args[i].startsWith("^")) {
                    if (next < args.length && !args[next].startsWith("~") && (j == 0 || j >= 3)) {
                        builder.append(sender.getName());
                    } else {
                        String xyz = j == 0 ? "x" : j == 1 ? "y" : j == 2 ? "z" : "x";
                        builder.append(PsudoCommand.INSTANCE.getIntRelative(args[i], xyz, (Entity) sender));
                        j++;
                    }
                } else if (has(args[i])) {
                    List<Index> indexList = new ArrayList<>();
                    List<String> replaceList = new ArrayList<>();
                    String source = replace(args[i].toCharArray(), indexList).toString();
                    for (int k = 0; k < indexList.size(); k++) {
                        String selector = indexList.get(k).substring(args[i]);
                        Entity[] entities = PsudoCommand.INSTANCE.getTargets(sender, selector);
                        if  ((entities == null || entities.length == 0)
                                && selector.startsWith("@p") && sender instanceof Player) {
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
                            replaceList.add(StringUtils.replace(source, "{" + k + "}", getName(entities[l])));
                        }
                        if (!works || entities.length == 0 || entities[0] == null) {
                            return Collections.singletonList(new StringBuilder(command));
                        } else {
                            String name = getName(entities[0]);
                            for (int l = 0; l < replaceList.size(); l++) {
                                replaceList.set(l, StringUtils.replace(replaceList.get(l), "{" + k + "}", name));
                            }
                            source = StringUtils.replace(source, "{" + k + "}", name);
                        }
                    }
                    for (String arg : replaceList) {
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
                if (next < args.length) {
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
            if (chars[i] == '@' && type < chars.length && SELECTOR_SUFFIX.indexOf(chars[type]) > -1) {
                Index textIndex = new Index(i);
                textIndex.end = type;
                if (tag < chars.length && chars[tag] == '[') {
                    for (int m = tag + 1; m < chars.length; m++) {
                        if (chars[m] == '[') {
                            i++;
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
}