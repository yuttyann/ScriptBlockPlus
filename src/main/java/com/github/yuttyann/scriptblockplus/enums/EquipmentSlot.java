package com.github.yuttyann.scriptblockplus.enums;


public enum EquipmentSlot {
	HAND, OFF_HAND, FEET, LEGS, CHEST, HEAD, NONE;

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
		if (bukkitEquipmentSlot == null) {
			return false;
		}
		Class<?> original = bukkitEquipmentSlot.getClass();
		return original.getName().equals("org.bukkit.inventory.EquipmentSlot");
	}
}
