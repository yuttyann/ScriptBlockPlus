package com.github.yuttyann.scriptblockplus.enums.reflection;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public enum DataType {
	BYTE(byte.class, Byte.class),
	SHORT(short.class, Short.class),
	INTEGER(int.class, Integer.class),
	LONG(long.class, Long.class),
	CHARACTER(char.class, Character.class),
	FLOAT(float.class, Float.class),
	DOUBLE(double.class, Double.class),
	BOOLEAN(boolean.class, Boolean.class);

	private static final Map<Class<?>, DataType> CLASS = new HashMap<>();

	private final Class<?> primitive;
	private final Class<?> reference;

	static {
		for (DataType type : values()) {
			CLASS.put(type.primitive, type);
			CLASS.put(type.reference, type);
		}
	}

	private DataType(Class<?> primitive, Class<?> reference) {
		this.primitive = primitive;
		this.reference = reference;
	}

	public Class<?> getPrimitive() {
		return primitive;
	}

	public Class<?> getReference() {
		return reference;
	}

	public static DataType fromClass(Class<?> clazz) {
		return CLASS.get(clazz);
	}

	public static Class<?> getPrimitive(Class<?> clazz) {
		DataType type = fromClass(clazz);
		return type == null ? clazz : type.getPrimitive();
	}

	public static Class<?> getReference(Class<?> clazz) {
		DataType type = fromClass(clazz);
		return type == null ? clazz : type.getReference();
	}

	public static Class<?>[] getPrimitive(Class<?>[] classes) {
		if (classes == null || classes.length == 0) {
			return ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		int length = classes == null ? 0 : classes.length;
		Class<?>[] types = new Class<?>[length];
		for (int index = 0; index < length; index++) {
			types[index] = getPrimitive(classes[index]);
		}
		return types;
	}

	public static Class<?>[] getReference(Class<?>[] classes) {
		if (classes == null || classes.length == 0) {
			return ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		int length = classes == null ? 0 : classes.length;
		Class<?>[] types = new Class<?>[length];
		for (int index = 0; index < length; index++) {
			types[index] = getReference(classes[index]);
		}
		return types;
	}

	public static Class<?>[] getPrimitive(Object[] objects) {
		if (objects == null || objects.length == 0) {
			return ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		int length = objects == null ? 0 : objects.length;
		Class<?>[] types = new Class<?>[length];
		for (int index = 0; index < length; index++) {
			types[index] = getPrimitive(objects[index].getClass());
		}
		return types;
	}

	public static Class<?>[] getReference(Object[] objects) {
		if (objects == null || objects.length == 0) {
			return ArrayUtils.EMPTY_CLASS_ARRAY;
		}
		int length = objects == null ? 0 : objects.length;
		Class<?>[] types = new Class<?>[length];
		for (int index = 0; index < length; index++) {
			types[index] = getReference(objects[index].getClass());
		}
		return types;
	}

	public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
		if (primary == null || secondary == null || primary.length != secondary.length) {
			return false;
		}
		for (int index = 0; index < primary.length; index++) {
			Class<?> primaryClass = primary[index];
			Class<?> secondaryClass = secondary[index];
			if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
				continue;
			}
			return false;
		}
		return true;
	}
}