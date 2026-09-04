package com.github.yuttyann.scriptblockplus.utils.server.minecraft;

import static com.github.yuttyann.scriptblockplus.utils.reflect.Reflection.method;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

import java.lang.reflect.Method;
import java.util.Locale;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * 26系のキューブのNMS EntityTypeを、CraftBukkitのレジストリ変換経由で取得します。
 * EntityType/EntityTypesの静的フィールドやIdentifierの名前には依存しません。
 */
final class CubeEntityTypeResolver {

    private CubeEntityTypeResolver() { }

    @NotNull
    static Object resolve(@NotNull Class<?> craftEntityType, @NotNull Class<?> minecraftEntityType) throws ReflectiveOperationException {
        return resolve(craftEntityType, minecraftEntityType, EntityType.MAGMA_CUBE);
    }

    @NotNull
    static Object resolve(@NotNull Class<?> craftEntityType, @NotNull Class<?> minecraftEntityType, @NotNull EntityType entityType) throws ReflectiveOperationException {
        // 取得結果は各アクセサーが保持するため、エンティティ生成ごとの検索は不要です。
        var result = findConverter(craftEntityType, minecraftEntityType).invoke(null, entityType);
        if (result == null) {
            throw new ReflectiveOperationException(craftEntityType.getName() + ".bukkitToMinecraft returned null for minecraft:" + entityType.name().toLowerCase(Locale.ROOT));
        }
        return result;
    }

    @NotNull
    static Method findConverter(@NotNull Class<?> craftEntityType, @NotNull Class<?> minecraftEntityType) throws NoSuchMethodException {
        var converter = method(craftEntityType)
            .modifiers(PUBLIC, STATIC)
            .name("bukkitToMinecraft")
            .parameterTypes(EntityType.class)
            .returnType(minecraftEntityType)
            .findFirstOrNull();
        if (converter == null) {
            throw new NoSuchMethodException(craftEntityType.getName() + ".bukkitToMinecraft(org.bukkit.entity.EntityType) -> " + minecraftEntityType.getName());
        }
        return converter;
    }
}
