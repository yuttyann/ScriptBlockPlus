package com.github.yuttyann.scriptblockplus.file.json.builder;

import java.lang.reflect.Type;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.NotNull;

public class BlockCoordsDeserializer implements JsonDeserializer<BlockCoords> {

	@Override
	@NotNull
	public BlockCoords deserialize(@NotNull JsonElement json, @NotNull Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
		return BlockCoords.fromString(json.getAsString());
	}
}