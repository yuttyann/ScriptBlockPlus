package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.file.json.annotation.Exclude;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus FieldExclusion クラス
 * @author yuttyann44581
 */
public class FieldExclusion implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(@NotNull FieldAttributes field) {
        return field.getAnnotation(Exclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(@NotNull Class<?> clazz) {
        return false;
    }
}