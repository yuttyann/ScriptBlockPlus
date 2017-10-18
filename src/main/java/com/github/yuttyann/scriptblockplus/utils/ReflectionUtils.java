package com.github.yuttyann.scriptblockplus.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtils {

	public static Object newInstance(Class<?> clazz, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Constructor<?> constructor = clazz.getDeclaredConstructor(getClasses(arguments));
		constructor.setAccessible(true);
		return constructor.newInstance(arguments);
	}

	public static Object invokeMethod(Object instance, Class<?> clazz, String methodName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Method method = clazz.getDeclaredMethod(methodName);
		method.setAccessible(true);
		return method.invoke(instance);
	}

	public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Method method = clazz.getDeclaredMethod(methodName, getClasses(arguments));
		method.setAccessible(true);
		return method.invoke(instance, arguments);
	}

	public static Object getValue(Object instance, Class<?> clazz, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(instance);
	}

	public static Class<?>[] getClasses(Object[] objects) {
		int length = objects == null ? 0 : objects.length;
		Class<?>[] types = new Class<?>[length];
		for (int i = 0; i < length; i++) {
			types[i] = objects[i].getClass();
		}
		return types;
	}
}