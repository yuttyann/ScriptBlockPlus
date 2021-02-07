/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.file.json;

import java.util.List;
import java.util.function.Consumer;

import com.github.yuttyann.scriptblockplus.file.json.annotation.Alternate;
import com.github.yuttyann.scriptblockplus.file.json.builder.LegacyEnumFactory;
import com.github.yuttyann.scriptblockplus.file.json.builder.LegacyReflectiveFactory;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus GsonHolder クラス
 * <p>
 * {@link SerializedName#alternate()}が無い古いバージョンの場合は、
 * <p>
 * {@link Alternate#value()}を使用することで、同じ動作をさせることができます。
 * @apiNote
 * <pre>
 * SerializedNameとAlternateの実装方法
 * // 利用する場合は必ず@SerializedNameと@Alternateを同時に実装してください。
 * 
 * // 正しい実装方法(古いバージョン、新しいバージョン共に動作します。)
 * &#064;Alternate("firstName1")
 * &#064;SerializedName(value = "firstName", alternate = { "firstName1" })
 * 
 * // 謝った実装方法(古いバージョンでしか動作しません。)
 * &#064;Alternate("firstName1")
 * &#064;SerializedName(value = "firstName") 
 * </pre>
 * @author yuttyann44581
 */
public final class GsonHolder {

    private static final boolean LEGACY_GSON;

    static {
        var methods = SerializedName.class.getMethods();
        LEGACY_GSON = !StreamUtils.anyMatch(methods, m -> m.getName().equals("alternate"));
    }

    private final GsonBuilder gsonBuilder;

    private Gson gson;

    /**
     * コンストラクタ
     * @param gsonBuilder - {@link GsonBuilder}
     */
    public GsonHolder(@NotNull GsonBuilder gsonBuilder) {
        this.gsonBuilder = gsonBuilder;
    }

    /**
     * 処理の後に設定を更新します。
     * @param action - 処理
     */
    public void builder(@NotNull Consumer<GsonBuilder> action) {
        try {
            action.accept(gsonBuilder);
        } finally {
            update();
        }
    }

    /**
     * 設定を更新します。
     */
    public void update() {
        this.gson = gsonBuilder.create();
        if (LEGACY_GSON) {
            legacy();
        }
    }

    /**
     * {@link Gson}を取得します。
     * @return {@link Gson}
     */
    @NotNull
    Gson getGson() {
        if (gson == null) {
            update();
        }
        return gson;
    }

    /**
     * 以下の{@link TypeAdapterFactory}を置換する。
     * <p>
     * {@link ReflectiveTypeAdapterFactory} ---> {@link LegacyReflectiveFactory}
     * <p>
     * {@link TypeAdapters#ENUM_FACTORY} ---> {@link LegacyEnumFactory#INSTANCE}
     */
    @SuppressWarnings("unchecked")
    private void legacy() {
        try {
            var factories = Gson.class.getDeclaredField("factories");
            factories.setAccessible(true);
            var unmodifiableList = factories.get(gson);
            var innerList = unmodifiableList.getClass().getSuperclass().getDeclaredField("list");
            innerList.setAccessible(true);
            var typeFactoryList = (List<TypeAdapterFactory>) innerList.get(unmodifiableList);
            typeFactoryList.removeIf(t -> {
                var factory = t.getClass();
                return factory.equals(ReflectiveTypeAdapterFactory.class) || factory.equals(TypeAdapters.ENUM_FACTORY.getClass());
            });

            var constructorConstructor = Gson.class.getDeclaredField("constructorConstructor");
            constructorConstructor.setAccessible(true);
            var fieldNamingPolicy = GsonBuilder.class.getDeclaredField("fieldNamingPolicy");
            fieldNamingPolicy.setAccessible(true);
            var excluder = GsonBuilder.class.getDeclaredField("excluder");
            excluder.setAccessible(true);
            typeFactoryList.add(LegacyEnumFactory.INSTANCE);
            typeFactoryList.add(newLegacyReflectiveFactory(constructorConstructor.get(gson), fieldNamingPolicy.get(gsonBuilder), excluder.get(gsonBuilder)));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@link LegacyReflectiveFactory}を生成します。
     * @return {@link LegacyReflectiveFactory}
     */
    @NotNull
    private LegacyReflectiveFactory newLegacyReflectiveFactory(@NotNull Object constructorConstructor, @NotNull Object fieldNamingPolicy, @NotNull Object excluder) {
        return new LegacyReflectiveFactory((ConstructorConstructor) constructorConstructor, (FieldNamingStrategy) fieldNamingPolicy, (Excluder) excluder);
    }
}