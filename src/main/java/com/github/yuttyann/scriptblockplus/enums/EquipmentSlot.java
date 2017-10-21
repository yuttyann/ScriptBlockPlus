package com.github.yuttyann.scriptblockplus.enums;

public enum EquipmentSlot {
	HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, NONE;

	private static final Class<?> BUKKIT_E_CLASS;

	static {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("org.bukkit.inventory.EquipmentSlot");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		BUKKIT_E_CLASS = clazz;
	}

	public boolean equals(Enum<?> bukkitEquipmentSlot) {
		if (!checkEClass(bukkitEquipmentSlot)) {
			return false;
		}
		return name().equals(bukkitEquipmentSlot.name());
	}

	public static EquipmentSlot fromEnum(Enum<?> bukkitEquipmentSlot) {
		if (!checkEClass(bukkitEquipmentSlot)) {
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

	private static boolean checkEClass(Enum<?> bukkitEquipmentSlot) {
		return bukkitEquipmentSlot != null && bukkitEquipmentSlot.getClass() == BUKKIT_E_CLASS;
	}
}