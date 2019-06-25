package com.github.yuttyann.scriptblockplus.enums;


import com.github.yuttyann.scriptblockplus.utils.Utils;

public enum EquipSlot {
	HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, NONE;

	public static final Class<?> BUKKIT_ES_CLASS;

	static {
		if (Utils.isCBXXXorLater("1.9")) {
			BUKKIT_ES_CLASS = org.bukkit.inventory.EquipmentSlot.class;
		} else {
			BUKKIT_ES_CLASS = null;
		}
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