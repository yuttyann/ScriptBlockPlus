package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public enum EquipSlot {
	HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, NONE;

	private static final Class<?> BUKKIT_ES_CLASS;

	static {
		BUKKIT_ES_CLASS = Utils.isCBXXXorLater("1.9") ? org.bukkit.inventory.EquipmentSlot.class : null;
	}

	public static EquipSlot fromEquipmentSlot(Enum<?> equipmentSlot) {
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