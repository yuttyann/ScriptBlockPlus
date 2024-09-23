package com.github.yuttyann.scriptblockplus.utils.kyori;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.*;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class KyoriLegacy {

    @NotNull
    public static LegacyComponentSerializer of(char legacyCharacter) {
        return legacy(legacyCharacter);
    }

    @NotNull
    public static TextComponent fromString(@NotNull String input) {
        return legacySection().deserialize(input);
    }

    @NotNull
    public static List<Component> fromStringList(@NotNull List<String> inputs) {
        var components = new ObjectArrayList<Component>(inputs.size());
        for (var legacyText : inputs) {
            components.add(fromString(legacyText));
        }
        return components;
    }

    @NotNull
    public static String toString(@NotNull Component component) {
        return legacySection().serialize(component);
    }

    @NotNull
    public static String toString(@NotNull Component... components) {
        var builder = new StringBuilder();
        for (var component : components) {
            builder.append(toString(component));
        }
        return builder.toString();
    }

    @NotNull
    public static List<String> toStringList(@NotNull Component... components) {
        var legacyList = new ObjectArrayList<String>(components.length);
        for (var component : components) {
            legacyList.add(toString(component));
        }
        return legacyList;
    }

    @NotNull
    public static List<String> toStringList(@NotNull List<Component> components) {
        var legacyList = new ObjectArrayList<String>(components.size());
        for (var component : components) {
            legacyList.add(toString(component));
        }
        return legacyList;
    }
}