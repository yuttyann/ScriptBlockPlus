package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ItemUtils {

	@SuppressWarnings("deprecation")
	public static void setDamage(@NotNull ItemStack item, int damage) {
		Validate.notNull(item, "Item cannot be null");
		if (Utils.isCBXXXorLater("1.13")) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				((org.bukkit.inventory.meta.Damageable) meta).setDamage(damage);
				item.setItemMeta(meta);
			}
		} else {
			item.setDurability((short) damage);
		}
	}

	@SuppressWarnings("deprecation")
	public static int getDamage(@NotNull ItemStack item) {
		Validate.notNull(item, "Item cannot be null");
		if (Utils.isCBXXXorLater("1.13")) {
			ItemMeta meta = item.getItemMeta();
			return meta == null ? 0 : ((org.bukkit.inventory.meta.Damageable) meta).getDamage();
		}
		return item.getDurability();
	}

	public static Material getMaterial(String name) {
		Material type = Material.getMaterial(name.toUpperCase());
		return type == null ? Material.AIR : type;
	}

	@NotNull
	public static ItemStack getBlockSelector() {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dBlock Selector");
		meta.setLore(setListColor(SBConfig.BLOCK_SELECTOR.get()));
		item.setItemMeta(meta);
		return item;
	}

	@NotNull
	public static ItemStack getScriptEditor(@NotNull ScriptType scriptType) {
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dScript Editor§6[Mode: " + scriptType.name() + "]");
		meta.setLore(setListColor(SBConfig.SCRIPT_EDITOR.get()));
		item.setItemMeta(meta);
		return item;
	}

	@NotNull
	private static List<String> setListColor(List<String> list) {
		list = new ArrayList<>(list);
		for (int i = 0; i < list.size(); i++) {
			list.set(i, StringUtils.setColor(list.get(i), true));
		}
		return list;
	}

	public static boolean isBlockSelector(@Nullable ItemStack item) {
		return isItem(item, Material.STICK, s -> s.equals("§dBlock Selector"));
	}

	public static boolean isScriptEditor(@Nullable ItemStack item) {
		return isItem(item, Material.BLAZE_ROD, s -> s.startsWith("§dScript Editor§6[Mode: ") && s.endsWith("]"));
	}

	@NotNull
	public static ScriptType getScriptType(@Nullable ItemStack item) {
		if (isScriptEditor(item)) {
			String name = StringUtils.removeStart(ItemUtils.getName(item, ""), "§dScript Editor§6[Mode: ");
			return ScriptType.valueOf(name.substring(0, name.length() - 1));
		}
		return ScriptType.INTERACT;
	}

	@NotNull
	public static ItemStack getItemInMainHand(Player player) {
		PlayerInventory inventory = player.getInventory();
		return inventory.getItemInMainHand();
	}

	@NotNull
	public static ItemStack getItemInOffHand(Player player) {
		return player.getInventory().getItemInOffHand();
	}

	@NotNull
	public static ItemStack[] getHandItems(Player player) {
		return new ItemStack[] { getItemInMainHand(player), getItemInOffHand(player) };
	}

	public static void setName(@NotNull ItemStack item, @NotNull String name) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		item.setItemMeta(itemMeta);
	}

	@NotNull
	public static String getName(@NotNull ItemStack item, @Nullable String def) {
		def = def == null ? item.getType().name() : def;
		if (item.getType() == Material.AIR) {
			return def;
		}
		ItemMeta meta = item.getItemMeta();
		return meta == null ? def : meta.hasDisplayName() ? meta.getDisplayName() : def;
	}

	@NotNull
	public static String getName(@NotNull ItemStack item) {
		return getName(item, item.getType().name());
	}

	public static boolean isItem(@Nullable ItemStack item, @Nullable Material type, @NotNull String name) {
		return isItem(item, type, name::equals);
	}

	public static boolean isItem(@Nullable ItemStack item, @Nullable Material type, @NotNull Predicate<String> name) {
		return item != null && type != null && item.getType() == type && name.test(getName(item));
	}
}