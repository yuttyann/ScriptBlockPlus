package com.github.yuttyann.scriptblockplus.enums;

public enum EquipSlot {
	HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, NONE;

	private static final Class<?> BUKKIT_ES_CLASS;

	static {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("org.bukkit.inventory.EquipmentSlot");
		} catch (ClassNotFoundException e) {}
		BUKKIT_ES_CLASS = clazz;
	}

	public boolean equals(Enum<?> bukkitEquipmentSlot) {
		if (!isBukkitEquipmentSlot(bukkitEquipmentSlot)) {
			return false;
		}
		return name().equals(bukkitEquipmentSlot.name());
	}

	public static EquipSlot fromEnum(Enum<?> bukkitEquipmentSlot) {
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

	private static boolean isBukkitEquipmentSlot(Enum<?> bukkitEquipmentSlot) {
		if (BUKKIT_ES_CLASS == null || bukkitEquipmentSlot == null) {
			return false;
		}
		return bukkitEquipmentSlot.getClass() == BUKKIT_ES_CLASS;
	}
}