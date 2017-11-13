package com.github.yuttyann.scriptblockplus.enums;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public enum PackageType {
	NMS("net.minecraft.server." + getPackageName()),
	CB("org.bukkit.craftbukkit." + getPackageName()),
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

	private static final Map<String, Method> METHOD_CACHE_MAP = new HashMap<String, Method>();
	private static final Map<String, Class<?>> CLASS_CACHE_MAP = new HashMap<String, Class<?>>();
	private static final Map<String, Constructor<?>> CONSTRUCTOR_CACHE_MAP = new HashMap<String, Constructor<?>>();

	private final String path;

	private PackageType(String path) {
		this.path = path;
	}

	private PackageType(PackageType parent, String path) {
		this(parent + "." + path);
	}

	public String getPath() {
		return path;
	}

	public Object newInstance(String className, Object... arguments) throws ReflectiveOperationException {
		return newInstance(false, className, arguments);
	}

	public Object newInstance(boolean declared, String className, Object... arguments) throws ReflectiveOperationException {
		if (arguments == ArrayUtils.EMPTY_OBJECT_ARRAY) {
			return getClass(className).newInstance();
		}
		return getConstructor(declared, className, DataType.getPrimitive(arguments)).newInstance(arguments);
	}

	public Method getMethod(String className, String methodName, Class<?>... parameterTypes) throws ReflectiveOperationException {
		return getMethod(false, className, methodName, parameterTypes);
	}

	public Method getMethod(boolean declared, String className, String methodName, Class<?>... parameterTypes) throws ReflectiveOperationException {
		String key = createKey(className, methodName, parameterTypes);
		Method method = METHOD_CACHE_MAP.get(key);
		if (method == null) {
			if (declared) {
				method = getClass(className).getDeclaredMethod(methodName, parameterTypes);
				method.setAccessible(true);
			} else {
				method = getClass(className).getMethod(methodName, parameterTypes);
			}
		}
		return method;
	}

	public Constructor<?> getConstructor(String className, Class<?>... parameterTypes) throws ReflectiveOperationException {
		return getConstructor(false, className, parameterTypes);
	}

	public Constructor<?> getConstructor(boolean declared, String className, Class<?>... parameterTypes) throws ReflectiveOperationException {
		String key = createKey(className, null, parameterTypes);
		Constructor<?> constructor = CONSTRUCTOR_CACHE_MAP.get(key);
		if (constructor == null) {
			if (declared) {
				constructor = getClass(className).getDeclaredConstructor(parameterTypes);
				constructor.setAccessible(true);
			} else {
				constructor = getClass(className).getConstructor(parameterTypes);
			}
		}
		return constructor;
	}

	public Class<?> getClass(String className) throws IllegalArgumentException, ClassNotFoundException {
		if (StringUtils.isEmpty(className)) {
			throw new IllegalArgumentException();
		}
		String path = this + "." + className;
		Class<?> clazz = CLASS_CACHE_MAP.get(path);
		if (clazz == null) {
			clazz = Class.forName(path);
			CLASS_CACHE_MAP.put(path, clazz);
		}
		return clazz;
	}

	private String createKey(String className, String methodName, Class<?>[] objects) {
		if (className == null) {
			return "null";
		}
		if (objects == null || objects.length == 0 || (objects.length == 1 && objects[0] == null)) {
			objects = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		int lastLength = objects.length - 1;
		if (lastLength == -1) {
			if (methodName != null) {
				return this + "." + className + "-" + methodName + "[]";
			}
			return this + "." + className;
		}
		boolean notEmptyMethod = StringUtils.isNotEmpty(methodName);
		int length = objects.length + className.length();
		if (notEmptyMethod) {
			length += methodName.length();
		}
		StrBuilder builder = new StrBuilder(length);
		builder.append(this).append('.').append(className).append(notEmptyMethod ? '-' : '[');
		if (notEmptyMethod) {
			builder.append(methodName).append('[');
		}
		int i = 0;
		while (true) {
			Class<?> clazz = objects[i];
			builder.append(clazz == null ? "null" : clazz.getName());
			if (i == lastLength) {
				return builder.append(']').toString();
			}
			builder.append(',').append(' ');
			i++;
		}
	}

	private static String getPackageName() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		return version.substring(version.lastIndexOf('.') + 1);
	}

	@Override
	public String toString() {
		return path;
	}
}