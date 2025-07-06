
package com.github.yuttyann.scriptblockplus.utils.kyori;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;

public final class KyoriGson {

    private static final TypeAdapter<JsonElement> TYPE_ADAPTER = new Gson().getAdapter(JsonElement.class);
    private static final GsonComponentSerializer GSON_SERIALIZER;

    static {
        var builder = GsonComponentSerializer.colorDownsamplingGson().toBuilder();
        builder.editOptions(state -> {
            state.value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, false);
        });
        GSON_SERIALIZER = builder.build();
    }

    private KyoriGson() { }

    @NotNull
    public static Component fromString(@NotNull String input) {
        return GSON_SERIALIZER.deserialize(input);
    }

    @NotNull
    public static Component fromJsonElement(@NotNull JsonElement input) {
        return GSON_SERIALIZER.deserializeFromTree(input);
    }

    @NotNull
    public static String toJson(@NotNull Component component) {
        return GSON_SERIALIZER.serialize(component);
    }

    @NotNull
    public static JsonElement toJsonElement(@NotNull Component component) {
        return GSON_SERIALIZER.serializeToTree(component);
    }

    public static boolean isJsonInValid(@NotNull String input) {
        return !isJsonValid(input);
    }

    public static boolean isJsonValid(@NotNull String input) {
        if (input.isEmpty()) {
            return false;
        }
        var ch = input.charAt(0);
        if (ch == '{' || ch == '[') {
            try {
                TYPE_ADAPTER.fromJson(input);
                return true;
            } catch (IOException ignored) { }
        }
        return false;
    }
}