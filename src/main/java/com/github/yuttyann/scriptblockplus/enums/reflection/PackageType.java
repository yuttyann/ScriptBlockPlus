package com.github.yuttyann.scriptblockplus.enums.reflection;

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

	private enum RType {
		CLASS("class_"), FIELD("field_"), METHOD("method_"), CONSTRUCTOR("constructor_");

		private String name;

		private RType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final Map<String, Object> CACHE = new HashMap<>();

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
		String key = createKey(RType.FIELD, className, fieldName, null);
		Field field = (Field) CACHE.get(key);
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

	public Object invokeMethod(Object instance, String className, String methodName) throws ReflectiveOperationException {
		return invokeMethod(false, instance, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY);
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

	public Method getMethod(String className, String methodName) throws ReflectiveOperationException {
		return getMethod(false, className, methodName, ArrayUtils.EMPTY_CLASS_ARRAY);
	}

	public Method getMethod(String className, String methodName, Class<?>... parameterTypes) throws ReflectiveOperationException {
		return getMethod(false, className, methodName, parameterTypes);
	}

	public Method getMethod(boolean declared, String className, String methodName, Class<?>... parameterTypes) throws ReflectiveOperationException {
		if (parameterTypes == null) {
			parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		String key = createKey(RType.METHOD, className, methodName, parameterTypes);
		Method method = (Method) CACHE.get(key);
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

	public Object newInstance(String className) throws ReflectiveOperationException {
		return newInstance(false, className, ArrayUtils.EMPTY_OBJECT_ARRAY);
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

	public Constructor<?> getConstructor(String className) throws ReflectiveOperationException {
		return getConstructor(false, className, ArrayUtils.EMPTY_CLASS_ARRAY);
	}

	public Constructor<?> getConstructor(String className, Class<?>... parameterTypes) throws ReflectiveOperationException {
		return getConstructor(false, className, parameterTypes);
	}

	public Constructor<?> getConstructor(boolean declared, String className, Class<?>... parameterTypes) throws ReflectiveOperationException {
		if (parameterTypes == null) {
			parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		String key = createKey(RType.CONSTRUCTOR, className, null, parameterTypes);
		Constructor<?> constructor = (Constructor<?>) CACHE.get(key);
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

	public Class<?> getClass(String className) throws IllegalArgumentException, ClassNotFoundException {
		if (StringUtils.isEmpty(className)) {
			throw new IllegalArgumentException();
		}
		String pass = this + "." + className;
		String key = RType.CLASS + pass;
		Class<?> clazz = (Class<?>) CACHE.get(key);
		if (clazz == null) {
			clazz = Class.forName(pass);
			CACHE.put(key, clazz);
		}
		return clazz;
	}

	public Enum<?> getEnumValueOf(String className, String name) throws IllegalArgumentException, ReflectiveOperationException {
		Class<?> clazz = getClass(className);
		if (clazz != null && clazz.isEnum()) {
			return (Enum<?>) getMethod(className, "valueOf", String.class).invoke(null, name);
		}
		return null;
	}

	public static void viewMethods(Class<?> clazz) {
		for (Method m : clazz.getDeclaredMethods()) {
			System.out.println("*#Method#: " + m.getName());
			for (Class<?> p : m.getParameterTypes()) {
				System.out.println("- #Parameter#: " + p.getSimpleName());
			}
		}
	}

	private String createKey(RType rType, String className, String name, Class<?>[] objects) {
		if (StringUtils.isEmpty(className)) {
			return "null";
		}
		String rName = rType + "";
		int lastLength = objects == null ? -1 : objects.length - 1;
		if (lastLength == -1) {
			if (name != null) {
				return rName + this + "." + className + "=" + name + "[]";
			}
			return rName + this + "." + className;
		}
		StrBuilder builder = new StrBuilder();
		boolean notEmptyName = StringUtils.isNotEmpty(name);
		builder.append(rName).append(this).append('.').append(className).append(notEmptyName ? '=' : '[');
		if (notEmptyName) {
			builder.append(name).append('[');
		}
		for (int i = 0; i < objects.length; i++) {
			builder.append(objects[i] == null ? "null" : objects[i].getName());
			if (i == lastLength) {
				return builder.append(']').toString();
			}
			builder.append(',');
		}
		return builder.toString();
	}

	public static String getVersionName() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		return version.substring(version.lastIndexOf('.') + 1);
	}

	@Override
	public String toString() {
		return path;
	}
}