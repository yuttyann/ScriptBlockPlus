package com.github.yuttyann.scriptblockplus.enums;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

	private static final Map<String, Field> FIELD_CACHE_MAP = new HashMap<String, Field>();
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

	public void setFieldValue(String className, String fieldName, Object instance, Object value) throws ReflectiveOperationException {
		getField(false, className, fieldName).set(instance, value);
	}

	public void setFieldValue(boolean declared, String className, String fieldName, Object instance, Object value) throws ReflectiveOperationException {
		if (StringUtils.isEmpty(className)) {
			className = instance.getClass().getSimpleName();
		}
		getField(declared, className, fieldName).set(instance, value);
	}

	public Field getField(String className, String fieldName) throws ReflectiveOperationException {
		return getField(false, className, fieldName);
	}

	public Field getField(boolean declared, String className, String fieldName) throws ReflectiveOperationException {
		String key = createKey(className, fieldName);
		Field field = FIELD_CACHE_MAP.get(key);
		if (field == null) {
			if (declared) {
				field = getClass(className).getDeclaredField(fieldName);
				field.setAccessible(true);
			} else {
				field = getClass(className).getField(fieldName);
			}
			FIELD_CACHE_MAP.put(key, field);
		}
		return field;
	}

	public Object invokeMethod(Object instance, String className, String methodName, Object... arguments) throws ReflectiveOperationException {
		return invokeMethod(false, instance, className, methodName, arguments);
	}

	public Object invokeMethod(boolean declared, Object instance, String className, String methodName, Object... arguments) throws ReflectiveOperationException {
		if (StringUtils.isEmpty(className)) {
			className = instance.getClass().getSimpleName();
		}
		if (arguments == null) {
			arguments = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		return getMethod(declared, className, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
	}

	public Method getMethod(String className, String methodName, Class<?>... parameterTypes) throws ReflectiveOperationException {
		return getMethod(false, className, methodName, parameterTypes);
	}

	public Method getMethod(boolean declared, String className, String methodName, Class<?>... parameterTypes) throws ReflectiveOperationException {
		if (parameterTypes == null) {
			parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		String key = createKey(className, methodName, parameterTypes);
		Method method = METHOD_CACHE_MAP.get(key);
		if (method == null) {
			if (declared) {
				method = getClass(className).getDeclaredMethod(methodName, parameterTypes);
				method.setAccessible(true);
			} else {
				method = getClass(className).getMethod(methodName, parameterTypes);
			}
			METHOD_CACHE_MAP.put(key, method);
		}
		return method;
	}

	public Object newInstance(String className, Object... arguments) throws ReflectiveOperationException {
		return newInstance(false, className, arguments);
	}

	public Object newInstance(boolean declared, String className, Object... arguments) throws ReflectiveOperationException {
		if (arguments == null || arguments.length == 0) {
			return getClass(className).newInstance();
		}
		return getConstructor(declared, className, DataType.getPrimitive(arguments)).newInstance(arguments);
	}

	public Constructor<?> getConstructor(String className, Class<?>... parameterTypes) throws ReflectiveOperationException {
		return getConstructor(false, className, parameterTypes);
	}

	public Constructor<?> getConstructor(boolean declared, String className, Class<?>... parameterTypes) throws ReflectiveOperationException {
		if (parameterTypes == null) {
			parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		String key = createKey(className, null, parameterTypes);
		Constructor<?> constructor = CONSTRUCTOR_CACHE_MAP.get(key);
		if (constructor == null) {
			if (declared) {
				constructor = getClass(className).getDeclaredConstructor(parameterTypes);
				constructor.setAccessible(true);
			} else {
				constructor = getClass(className).getConstructor(parameterTypes);
			}
			CONSTRUCTOR_CACHE_MAP.put(key, constructor);
		}
		return constructor;
	}

	public Class<?> getClass(String className) throws IllegalArgumentException, ClassNotFoundException {
		if (StringUtils.isEmpty(className)) {
			throw new IllegalArgumentException();
		}
		String key = this + "." + className;
		Class<?> clazz = CLASS_CACHE_MAP.get(key);
		if (clazz == null) {
			clazz = Class.forName(key);
			CLASS_CACHE_MAP.put(key, clazz);
		}
		return clazz;
	}

	private String createKey(String className, String name) {
		return createKey(className, name, null);
	}

	private String createKey(String className, String name, Class<?>[] objects) {
		if (StringUtils.isEmpty(className)) {
			return "null";
		}
		if (objects == null || objects.length == 0) {
			objects = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		int lastLength = objects.length - 1;
		if (lastLength == -1) {
			if (name != null) {
				return this + "." + className + "=" + name + "[]";
			}
			return this + "." + className;
		}
		boolean notEmptyMethod = StringUtils.isNotEmpty(name);
		int length = objects.length + className.length();
		if (notEmptyMethod) {
			length += name.length();
		}
		StrBuilder builder = new StrBuilder(length);
		builder.append(this).append('.').append(className).append(notEmptyMethod ? '=' : '[');
		if (notEmptyMethod) {
			builder.append(name).append('[');
		}
		for (int i = 0; i < objects.length; i++) {
			Class<?> clazz = objects[i];
			builder.append(clazz == null ? "null" : clazz.getName());
			if (i == lastLength) {
				return builder.append(']').toString();
			}
			builder.append(',').append(' ');
		}
		return builder.toString();
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