package com.github.yuttyann.scriptblockplus.enums.reflection;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ScriptBlockPlus PackageType 列挙型
 * @author yuttyann44581
 */
public enum PackageType {
    NMS("net.minecraft.server." + getVersionName()),
    CB("org.bukkit.craftbukkit." + getVersionName()),
    CB_BLOCK(CB, "block"),
    CB_CHUNKIO(CB, "chunkio"),
    CB_COMMAND(CB, "command"),
    CB_CONVERSATIONS(CB, "conversations"),
    CB_ENCHANTMENS(CB, "enchantments"),
    CB_ENTITY(CB, "entity"),
    CB_EVENT(CB, "event"),
    CB_GENERATOR(CB, "generator"),
    CB_HELP(CB, "help"),
    CB_INVENTORY(CB, "inventory"),
    CB_MAP(CB, "map"),
    CB_METADATA(CB, "metadata"),
    CB_POTION(CB, "potion"),
    CB_PROJECTILES(CB, "projectiles"),
    CB_SCHEDULER(CB, "scheduler"),
    CB_SCOREBOARD(CB, "scoreboard"),
    CB_UPDATER(CB, "updater"),
    CB_UTIL(CB, "util");

    private enum ReturnType {
        CLASS("class_"),
        FIELD("field_"),
        METHOD("method_"),
        CONSTRUCTOR("constructor_");

        private final String name;

        ReturnType(@NotNull String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final Map<String, Object> CACHE = new HashMap<>();

    private final String path;

    PackageType(@NotNull String path) {
        this.path = path;
    }

    PackageType(@NotNull PackageType parent, @NotNull String path) {
        this(parent + "." + path);
    }

    @NotNull
    public String getPath() {
        return path;
    }

    public void setFieldValue(@NotNull String className, @NotNull String fieldName, @Nullable Object instance, @Nullable Object value) throws ReflectiveOperationException {
        getField(false, className, fieldName).set(instance, value);
    }

    public void setFieldValue(boolean declared, @NotNull String className, @NotNull String fieldName, @Nullable Object instance, @Nullable Object value) throws ReflectiveOperationException {
        if (StringUtils.isEmpty(className)) {
            className = Objects.requireNonNull(instance).getClass().getSimpleName();
        }
        getField(declared, className, fieldName).set(instance, value);
    }

    public Field getField(@NotNull String className, @NotNull String fieldName) throws ReflectiveOperationException {
        return getField(false, className, fieldName);
    }

    public Field getField(boolean declared, @NotNull String className, @NotNull String fieldName) throws ReflectiveOperationException {
        var key = createKey(ReturnType.FIELD, className, fieldName, null);
        var field = (Field) CACHE.get(key);
        if (field == null) {
            if (declared) {
                field = getClass(className).getDeclaredField(fieldName);
                field.setAccessible(true);
            } else {
                field = getClass(className).getField(fieldName);
            }
            CACHE.put(key, field);
        }
        return field;
    }

    public Object invokeMethod(@Nullable Object instance, @NotNull String className, @NotNull String methodName) throws ReflectiveOperationException {
        return invokeMethod(false, instance, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    public Object invokeMethod(@Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Object... arguments) throws ReflectiveOperationException {
        return invokeMethod(false, instance, className, methodName, arguments);
    }

    public Object invokeMethod(boolean declared, @Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Object... arguments) throws ReflectiveOperationException {
        if (StringUtils.isEmpty(className)) {
            className = Objects.requireNonNull(instance).getClass().getSimpleName();
        }
        if (arguments == null) {
            arguments = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        return getMethod(declared, className, methodName, ClassType.getPrimitive(arguments)).invoke(instance, arguments);
    }

    public Method getMethod(@NotNull String className, @NotNull String methodName) throws ReflectiveOperationException {
        return getMethod(false, className, methodName, ArrayUtils.EMPTY_CLASS_ARRAY);
    }

    public Method getMethod(@NotNull String className, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        return getMethod(false, className, methodName, parameterTypes);
    }

    public Method getMethod(boolean declared, @NotNull String className, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var key = createKey(ReturnType.METHOD, className, methodName, parameterTypes);
        var method = (Method) CACHE.get(key);
        if (method == null) {
            if (declared) {
                method = getClass(className).getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
            } else {
                method = getClass(className).getMethod(methodName, parameterTypes);
            }
            CACHE.put(key, method);
        }
        return method;
    }

    public Object newInstance(@NotNull String className) throws ReflectiveOperationException {
        return newInstance(false, className, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    public Object newInstance(@NotNull String className, @Nullable Object... arguments) throws ReflectiveOperationException {
        return newInstance(false, className, arguments);
    }

    public Object newInstance(boolean declared, @NotNull String className, @Nullable Object... arguments) throws ReflectiveOperationException {
        if (arguments == null || arguments.length == 0) {
            return getClass(className).getConstructor(ArrayUtils.EMPTY_CLASS_ARRAY).newInstance();
        }
        return getConstructor(declared, className, ClassType.getPrimitive(arguments)).newInstance(arguments);
    }

    public Constructor<?> getConstructor(@NotNull String className) throws ReflectiveOperationException {
        return getConstructor(false, className, ArrayUtils.EMPTY_CLASS_ARRAY);
    }

    public Constructor<?> getConstructor(@NotNull String className, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        return getConstructor(false, className, parameterTypes);
    }

    public Constructor<?> getConstructor(boolean declared, @NotNull String className, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var key = createKey(ReturnType.CONSTRUCTOR, className, null, parameterTypes);
        var constructor = (Constructor<?>) CACHE.get(key);
        if (constructor == null) {
            if (declared) {
                constructor = getClass(className).getDeclaredConstructor(parameterTypes);
                constructor.setAccessible(true);
            } else {
                constructor = getClass(className).getConstructor(parameterTypes);
            }
            CACHE.put(key, constructor);
        }
        return constructor;
    }

    public static int getSlimeSizeId() throws ReflectiveOperationException {
        var entitySlime = NMS.getClass("EntitySlime");
        var dataWatcherObject = NMS.getClass("DataWatcherObject");
        for (var field : entitySlime.getDeclaredFields()) {
            if (!field.getType().equals(dataWatcherObject)) {
                continue;
            }
            field.setAccessible(true);
            return (int) NMS.invokeMethod(field.get(null), "DataWatcherObject", "a");
        }
        return -1;
    }

    public static int getMagmaCubeId() throws ReflectiveOperationException {
        int entityId = 0;
        var entityTypes = NMS.getClass("EntityTypes");
        for (var field : entityTypes.getFields()) {
            if (!field.getType().equals(entityTypes)) {
                continue;   
            }
            if (field.getName().equals("MAGMA_CUBE")) {
                break;
            }
            entityId++;
        }
        return entityId;
    }

    public static void sendActionBar(@NotNull Player player, @NotNull String text) throws ReflectiveOperationException {
        var chatSerializer = "IChatBaseComponent$ChatSerializer";
        var component = NMS.invokeMethod(null, chatSerializer, "a", "{\"text\": \"" + text + "\"}");
        var classes = new Class<?>[] { NMS.getClass("IChatBaseComponent"), byte.class };
        Object value = (byte) 2;
        if (Utils.isCBXXXorLater("1.12")) {
            value = NMS.getEnumValueOf("ChatMessageType", "GAME_INFO");
            classes[1] = value.getClass();
        }
        sendPacket(player, NMS.getConstructor("PacketPlayOutChat", classes).newInstance(component, value));
    }

    public static void sendPacket(@NotNull Player player, @NotNull Object packet) throws ReflectiveOperationException {
        var packetClass = NMS.getClass("Packet");
        var handle = CB_ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
        var connection = NMS.getField("EntityPlayer", "playerConnection").get(handle);
        NMS.getMethod("PlayerConnection", "sendPacket", packetClass).invoke(connection, packet);
    }

    public Class<?> getClass(@NotNull String className) throws IllegalArgumentException, ClassNotFoundException {
        if (StringUtils.isEmpty(className)) {
            throw new IllegalArgumentException();
        }
        var pass = this + "." + className;
        var key = ReturnType.CLASS + pass;
        var clazz = (Class<?>) CACHE.get(key);
        if (clazz == null) {
            clazz = Class.forName(pass);
            CACHE.put(key, clazz);
        }
        return clazz;
    }

    public Enum<?> getEnumValueOf(@NotNull String className, @NotNull String name) throws IllegalArgumentException, ReflectiveOperationException {
        var clazz = getClass(className);
        return clazz.isEnum() ? (Enum<?>) getMethod(className, "valueOf", String.class).invoke(null, name) : null;
    }

    private String createKey(@NotNull ReturnType returnType, @NotNull String className, @Nullable String name, @Nullable Class<?>[] objects) {
        if (StringUtils.isEmpty(className)) {
            return "null";
        }
        var rName = returnType + "";
        int lastLength = objects == null ? -1 : objects.length - 1;
        if (lastLength == -1) {
            if (name != null) {
                return rName + this + "." + className + "=" + name + "[]";
            }
            return rName + this + "." + className;
        }
        var builder = new StringBuilder();
        boolean notEmptyName = StringUtils.isNotEmpty(name);
        builder.append(rName).append(this).append('.').append(className).append(notEmptyName ? '=' : '[');
        if (notEmptyName) {
            builder.append(name).append('[');
        }
        for (int i = 0; i < objects.length; i++) {
            builder.append(objects[i] == null ? null : objects[i].getName());
            if (i == lastLength) {
                return builder.append(']').toString();
            }
            builder.append(',');
        }
        return builder.toString();
    }

    @NotNull
    public static String getVersionName() {
        var name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static void clear() {
        CACHE.clear();
    }

    @Override
    public String toString() {
        return path;
    }
}