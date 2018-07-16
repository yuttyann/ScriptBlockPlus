package com.github.yuttyann.scriptblockplus.enums;

import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.player.PlayerEvent;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public enum EquipSlot {
	HAND,
	OFF_HAND,
	FEET,
	LEGS,
	CHEST,
	HEAD,
	NONE;

	private static final Class<?> BUKKIT_ES_CLASS;

	static {
		Class<?> clazz = null;
		try {
			clazz = Utils.isCBXXXorLater("1.9") ? null : Class.forName("org.bukkit.inventory.EquipmentSlot");
		} catch (ClassNotFoundException e) {}
		BUKKIT_ES_CLASS = clazz;
	}

	public boolean equals(Enum<?> bukkitEquipmentSlot) {
		if (!isBukkitEquipmentSlot(bukkitEquipmentSlot)) {
			return false;
		}
		return name().equals(bukkitEquipmentSlot.name());
	}

	public static EquipSlot fromEquipmentSlot(Enum<?> bukkitEquipmentSlot) {
		if (!isBukkitEquipmentSlot(bukkitEquipmentSlot)) {
			return NONE;
		}
		switch (bukkitEquipmentSlot.name()) {
		case "HAND":
			return HAND;
		case "OFF_HAND":
			return OFF_HAND;
		case "FEET":
			return FEET;
		case "LEGS":
			return LEGS;
		case "CHEST":
			return CHEST;
		case "HEAD":
			return HEAD;
		default:
			return NONE;
		}
	}

	public static EquipSlot getHand(PlayerEvent event) {
		if (event != null && Utils.isCBXXXorLater("1.9")) {
			try {
				Method method = event.getClass().getMethod("getHand");
				if (method.getReturnType() == BUKKIT_ES_CLASS) {
					Object hand = method.invoke(event, ArrayUtils.EMPTY_OBJECT_ARRAY);
					return fromEquipmentSlot((Enum<?>) hand);
				}
			} catch (ReflectiveOperationException e) {}
		}
		return HAND;
	}

	private static boolean isBukkitEquipmentSlot(Enum<?> bukkitEquipmentSlot) {
		if (BUKKIT_ES_CLASS == null || bukkitEquipmentSlot == null) {
			return false;
		}
		return bukkitEquipmentSlot.getClass() == BUKKIT_ES_CLASS;
	}
}