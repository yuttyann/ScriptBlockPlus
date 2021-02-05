package com.github.yuttyann.scriptblockplus.file.json.builder;

import java.lang.reflect.Type;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.jetbrains.annotations.NotNull;

public class BlockCoordsSerializer implements JsonSerializer<BlockCoords> {

	@Override
	@NotNull
	public JsonElement serialize(@NotNull BlockCoords blockCoords, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
		return context.serialize(blockCoords.getFullCoords());
	}
}