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
package com.github.yuttyann.scriptblockplus.enums.reflection;

import com.github.yuttyann.scriptblockplus.raytrace.RayResult;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ScriptBlockPlus PackageType 列挙型
 * @author yuttyann44581
 */
public enum PackageType {
    MJN("com.mojang.brigadier"),
    NMS("net.minecraft.server." + Utils.getPackageVersion()),
    CB("org.bukkit.craftbukkit." + Utils.getPackageVersion()),
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

    public static final boolean HAS_NMS;

    static {
        boolean hasNMS;
        try {
            Class.forName(NMS + ".Entity");
            hasNMS = true;
        } catch (ClassNotFoundException e) {
            hasNMS = false;
        }
        HAS_NMS = hasNMS;
    }

    private static final Map<Integer, Object> REFLECTION_CACHE = new HashMap<>();

    private final String path;

    PackageType(@NotNull String path) {
        this.path = path;
    }

    PackageType(@NotNull PackageType parent, @NotNull String path) {
        this(parent + "." + path);
    }

    public static void clear() {
        REFLECTION_CACHE.clear();
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @Override
    @NotNull
    public String toString() {
        return path;
    }

    public void setFieldValue(@NotNull String className, @NotNull String fieldName, @Nullable Object instance, @Nullable Object value) throws ReflectiveOperationException {
        getField(false, className, fieldName).set(instance, value);
    }

    public void setFieldValue(boolean declared, @NotNull String className, @NotNull String fieldName, @Nullable Object instance, @Nullable Object value) throws ReflectiveOperationException {
        if (StringUtils.isEmpty(className)) {
            className = instance.getClass().getSimpleName();
        }
        getField(declared, className, fieldName).set(instance, value);
    }

    @Nullable
    public Field getField(@NotNull String className, @NotNull String fieldName) throws ReflectiveOperationException {
        return getField(false, className, fieldName);
    }

    @Nullable
    public Field getField(boolean declared, @NotNull String className, @NotNull String fieldName) throws ReflectiveOperationException {
        var hash = createHash(ReturnType.FIELD, className, fieldName, null);
        var field = (Field) REFLECTION_CACHE.get(hash);
        if (field == null) {
            if (declared) {
                field = getClass(className).getDeclaredField(fieldName);
                field.setAccessible(true);
            } else {
                field = getClass(className).getField(fieldName);
            }
            REFLECTION_CACHE.put(hash, field);
        }
        return field;
    }

    @Nullable
    public Object invokeMethod(@Nullable Object instance, @NotNull String className, @NotNull String methodName) throws ReflectiveOperationException {
        return invokeMethod(false, instance, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Nullable
    public Object invokeMethod(@Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Object... arguments) throws ReflectiveOperationException {
        return invokeMethod(false, instance, className, methodName, arguments);
    }

    @Nullable
    public Object invokeMethod(boolean declared, @Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Object... arguments) throws ReflectiveOperationException {
        if (StringUtils.isEmpty(className)) {
            className = instance.getClass().getSimpleName();
        }
        if (arguments == null) {
            arguments = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        return getMethod(declared, className, methodName, ClassType.getPrimitive(arguments)).invoke(instance, arguments);
    }

    @Nullable
    public Method getMethod(@NotNull String className, @NotNull String methodName) throws ReflectiveOperationException {
        return getMethod(false, className, methodName, ArrayUtils.EMPTY_CLASS_ARRAY);
    }

    @Nullable
    public Method getMethod(@NotNull String className, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        return getMethod(false, className, methodName, parameterTypes);
    }

    @Nullable
    public Method getMethod(boolean declared, @NotNull String className, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var hash = createHash(ReturnType.METHOD, className, methodName, parameterTypes);
        var method = (Method) REFLECTION_CACHE.get(hash);
        if (method == null) {
            if (declared) {
                method = getClass(className).getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
            } else {
                method = getClass(className).getMethod(methodName, parameterTypes);
            }
            REFLECTION_CACHE.put(hash, method);
        }
        return method;
    }

    @Nullable
    public Object newInstance(@NotNull String className) throws ReflectiveOperationException {
        return newInstance(false, className, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Nullable
    public Object newInstance(@NotNull String className, @Nullable Object... arguments) throws ReflectiveOperationException {
        return newInstance(false, className, arguments);
    }

    @Nullable
    public Object newInstance(boolean declared, @NotNull String className, @Nullable Object... arguments) throws ReflectiveOperationException {
        if (arguments == null || arguments.length == 0) {
            return getClass(className).getConstructor(ArrayUtils.EMPTY_CLASS_ARRAY).newInstance();
        }
        return getConstructor(declared, className, ClassType.getPrimitive(arguments)).newInstance(arguments);
    }

    @Nullable
    public Constructor<?> getConstructor(@NotNull String className) throws ReflectiveOperationException {
        return getConstructor(false, className, ArrayUtils.EMPTY_CLASS_ARRAY);
    }

    @Nullable
    public Constructor<?> getConstructor(@NotNull String className, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        return getConstructor(false, className, parameterTypes);
    }

    @Nullable
    public Constructor<?> getConstructor(boolean declared, @NotNull String className, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var hash = createHash(ReturnType.CONSTRUCTOR, className, null, parameterTypes);
        var constructor = (Constructor<?>) REFLECTION_CACHE.get(hash);
        if (constructor == null) {
            if (declared) {
                constructor = getClass(className).getDeclaredConstructor(parameterTypes);
                constructor.setAccessible(true);
            } else {
                constructor = getClass(className).getConstructor(parameterTypes);
            }
            REFLECTION_CACHE.put(hash, constructor);
        }
        return constructor;
    }

    @Nullable
    public Enum<?> getEnumValueOf(@NotNull String className, @NotNull String name) throws ReflectiveOperationException {
        var clazz = getClass(className);
        return clazz.isEnum() ? (Enum<?>) getMethod(className, "valueOf", String.class).invoke(null, name) : null;
    }

    @Nullable
    public Class<?> getClass(@NotNull String className) throws IllegalArgumentException, ClassNotFoundException {
        if (!HAS_NMS) {
            throw new UnsupportedOperationException("NMS not found.");
        }
        if (StringUtils.isEmpty(className)) {
            throw new IllegalArgumentException();
        }
        var hash = createHash(ReturnType.CLASS, className, null, null);
        var clazz = (Class<?>) REFLECTION_CACHE.get(hash);
        if (clazz == null) {
            clazz = Class.forName(this + "." + className);
            REFLECTION_CACHE.put(hash, clazz);
        }
        return clazz;
    }

    @NotNull
    private int createHash(@NotNull ReturnType returnType, @NotNull String className, @Nullable String name, @Nullable Class<?>[] objects) {
        if (!HAS_NMS || StringUtils.isEmpty(className)) {
            return 0;
        }
        int baseHash = returnType.toString().hashCode() + toString().hashCode() + className.hashCode();
        if (objects == null) {
            return name == null ? 11 * baseHash : 21 * (baseHash + name.hashCode());
        }
        int hash = 1;
        int prime = 31;
        hash = prime * hash + baseHash;
        hash = prime * hash + String.valueOf(name).hashCode();
        hash = prime * hash + Boolean.hashCode(StringUtils.isNotEmpty(name));
        for (var object : objects) {
            hash += Objects.hashCode(object);
        }
        return hash;
    }

    public static int getMagmaCubeId() {
        if (!Utils.isCBXXXorLater("1.13")) {
            return 62;
        }
        int entityId = 0;
        try {
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
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return entityId;
    }

    public static int getSlimeSizeId() {
        if (!Utils.isCBXXXorLater("1.10")) {
            return 11;
        }
        try {
            var entitySlime = NMS.getClass("EntitySlime");
            var dataWatcherObject = NMS.getClass("DataWatcherObject");
            for (var field : entitySlime.getDeclaredFields()) {
                if (!field.getType().equals(dataWatcherObject)) {
                    continue;
                }
                field.setAccessible(true);
                return (int) NMS.invokeMethod(field.get(null), "DataWatcherObject", "a");
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void sendActionBar(@NotNull Player player, @NotNull String text) throws ReflectiveOperationException {
        var component = NMS.invokeMethod(null, "IChatBaseComponent$ChatSerializer", "a", "{\"text\": \"" + text + "\"}");
        var classes = new Class<?>[] { NMS.getClass("IChatBaseComponent"), byte.class };
        Object value = (byte) 2;
        if (Utils.isCBXXXorLater("1.12")) {
            value = NMS.getEnumValueOf("ChatMessageType", "GAME_INFO");
            classes[1] = value.getClass();
        }
        var handle = CB_ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
        var connection = NMS.getField("EntityPlayer", "playerConnection").get(handle);
        var packetChat = NMS.getConstructor("PacketPlayOutChat", classes).newInstance(component, value);
        NMS.getMethod("PlayerConnection", "sendPacket", NMS.getClass("Packet")).invoke(connection, packetChat);
    }

    @Nullable
    public static RayResult rayTraceBlocks(@NotNull Player player, final double distance) throws ReflectiveOperationException {
        var eyeLocation = player.getEyeLocation();
        var vec3d1 = toVec3D(eyeLocation.toVector());
        var vec3d2 = toVec3D(eyeLocation.toVector().add(eyeLocation.getDirection().normalize().multiply(distance)));
        var arguments = (Object[]) null;
        if (Utils.isCBXXXorLater("1.13")) {
            var NEVER = NMS.getEnumValueOf("FluidCollisionOption", "NEVER");
            arguments = new Object[] { vec3d1, vec3d2, NEVER, false, false };
        } else {
            arguments = new Object[] { vec3d1, vec3d2, false };
        }
        var world = player.getWorld();
        var nmsWorld = CB.invokeMethod(world, "CraftWorld", "getHandle");
        var rayTrace = NMS.invokeMethod(nmsWorld, "World", "rayTrace", arguments);
        if (rayTrace != null) {
            var enumDirection = NMS.getField("MovingObjectPosition", "direction");
            var position = NMS.invokeMethod(rayTrace, "MovingObjectPosition", "a");
            int x = (int) NMS.invokeMethod(position, "BaseBlockPosition", "getX");
            int y = (int) NMS.invokeMethod(position, "BaseBlockPosition", "getY");
            int z = (int) NMS.invokeMethod(position, "BaseBlockPosition", "getZ");
            return new RayResult(world.getBlockAt(x, y, z), BlockFace.valueOf(((Enum<?>) enumDirection.get(rayTrace)).name()));
        }
        return null;
    }

    @NotNull
    public static Entity[] selectEntities(@NotNull CommandSender sender, @NotNull Location location, @NotNull String selector) throws ReflectiveOperationException {
        var argmentEntity = Utils.isCBXXXorLater("1.14") ? "multipleEntities" : "b";
        var entitySelector = Utils.isCBXXXorLater("1.14") ? "getEntities" : "b";
        var vector = toVec3D(location.toVector());
        var entity = NMS.invokeMethod(null, "ArgumentEntity", argmentEntity);
        var reader = MJN.newInstance("StringReader", selector);
        var listener = CB_COMMAND.getMethod("VanillaCommandWrapper", "getListener", CommandSender.class).invoke(null, sender);
        var wrapper = NMS.invokeMethod(listener, "CommandListenerWrapper", "a", vector);
        var parse = NMS.invokeMethod(entity, "ArgumentEntity", "parse", reader, true);
        var nmsList = (List<?>) NMS.invokeMethod(parse, "EntitySelector", entitySelector, wrapper);
        var entities = new Entity[nmsList.size()];
        for (int i = 0; i < nmsList.size(); i++) {
            entities[i] = (Entity) PackageType.NMS.invokeMethod(nmsList.get(i), "Entity", "getBukkitEntity");
        }
        return entities;
    }

    @NotNull
    public static Object getAxisAlignedBB(@NotNull Block block) throws ReflectiveOperationException {
        var world = CB.invokeMethod(block.getWorld(), "CraftWorld", "getHandle");
        var position = NMS.newInstance("BlockPosition", block.getX(), block.getY(), block.getZ());
        var blockData = NMS.invokeMethod(world, "WorldServer", "getType", position);
        if (Utils.isCBXXXorLater("1.13")) {
            var name = Utils.getPackageVersion().equals("v1_13_R2") ? "i" : "g";
            var getVoxelShape = NMS.getMethod("IBlockData", name, NMS.getClass("IBlockAccess"), position.getClass());
            return NMS.invokeMethod(getVoxelShape.invoke(blockData, world, position), "VoxelShape", "a");
        } else {
            var name = Utils.isCBXXXorLater("1.11") ? "b" : "a";
            var getAxisAlignedBB = NMS.getMethod("Block", name, NMS.getClass("IBlockData"), NMS.getClass("IBlockAccess"), position.getClass());
            return getAxisAlignedBB.invoke(NMS.invokeMethod(blockData, "IBlockData", "getBlock"), blockData, world, position);
        }
    }

    @NotNull
    public static Map<String, Material> getItemRegistry() throws ReflectiveOperationException {
        var items = new HashMap<String, Material>();
        var material = CB_UTIL.getMethod("CraftMagicNumbers", "getMaterial", NMS.getClass("Item"));
        var registory = NMS.getField("Item", "REGISTRY").get(null);
        var registorySimple = (Map<?, ?>) NMS.getField(true, "RegistrySimple", "c").get(registory);
        for (var entry : registorySimple.entrySet()) {
            items.put(entry.getKey().toString(), (Material) material.invoke(null, entry.getValue()));
        }
        return items;
    }

    @NotNull
    public static Object toVec3D(@NotNull Vector vector) throws ReflectiveOperationException {
        return NMS.newInstance("Vec3D", vector.getX(), vector.getY(), vector.getZ());
    }
}