package com.github.yuttyann.scriptblockplus.enums;


import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public enum EquipSlot {
	HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, NONE;

	public static final Class<?> BUKKIT_ES_CLASS;

	static {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("org.bukkit.inventory.EquipmentSlot");
		} catch (ClassNotFoundException e) { }
		BUKKIT_ES_CLASS = clazz;
	}

	@NotNull
	public static EquipSlot getHand(@NotNull PlayerEvent event) {
		if (!Utils.isCBXXXorLater("1.9")) {
			return HAND;
		}
		try {
			Method hand = event.getClass().getMethod("getHand");
			return fromEquipmentSlot((Enum<?>) hand.invoke(event, ArrayUtils.EMPTY_OBJECT_ARRAY));
		} catch (ReflectiveOperationException e) {
			return NONE;
		}
	}

	@NotNull
	public static EquipSlot fromEquipmentSlot(@NotNull Enum<?> equipmentSlot) {
		if (isBukkitEquipmentSlot(equipmentSlot)) {
			for (EquipSlot equipSlot : values()) {
				if (equipSlot.name().equals(equipmentSlot.name())) {
					return equipSlot;
				}
			}
		}
		return NONE;
	}

	public boolean equals(Enum<?> bukkitEquipmentSlot) {
		return isBukkitEquipmentSlot(bukkitEquipmentSlot) && name().equals(bukkitEquipmentSlot.name());
	}

	private static boolean isBukkitEquipmentSlot(Enum<?> equipmentSlot) {
		if (BUKKIT_ES_CLASS == null || equipmentSlot == null) {
			return false;
		}
		return BUKKIT_ES_CLASS.equals(equipmentSlot.getClass());
	}
}