package com.github.yuttyann.scriptblockplus.enums;


public enum EquipmentSlot {
	HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, NONE;

	private static final Class<?> BUKKIT_E_CLASS;

	static {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("org.bukkit.inventory.EquipmentSlot");
		} catch (ClassNotFoundException e) {}
		BUKKIT_E_CLASS = clazz;
	}

	public boolean equals(Enum<?> bukkitEquipmentSlot) {
		if (!checkClass(bukkitEquipmentSlot)) {
			return false;
		}
		return name().equals(bukkitEquipmentSlot.name());
	}

	public static EquipmentSlot fromEnum(Enum<?> bukkitEquipmentSlot) {
		if (!checkClass(bukkitEquipmentSlot)) {
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

	private static boolean checkClass(Enum<?> bukkitEquipmentSlot) {
		return BUKKIT_E_CLASS != null && bukkitEquipmentSlot != null && bukkitEquipmentSlot.getClass() == BUKKIT_E_CLASS;
	}
}