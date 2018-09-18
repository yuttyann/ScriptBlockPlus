package com.github.yuttyann.scriptblockplus.utils;

import java.util.function.Predicate;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;

public class ItemUtils {

	@SuppressWarnings("deprecation")
	public static void setDamage(ItemStack item, int damage) {
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
	public static int getDamage(ItemStack item) {
		Validate.notNull(item, "Item cannot be null");
		if (Utils.isCBXXXorLater("1.13")) {
			ItemMeta meta = item.getItemMeta();
			return meta == null ? 0 : ((org.bukkit.inventory.meta.Damageable) meta).getDamage();
		}
		return item.getDurability();
	}

	public static ItemStack getBlockSelector() {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dBlock Selector");
		meta.setLore(SBConfig.getBlockSelectorLore());
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getScriptEditor(ScriptType scriptType) {
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§dScript Editor§6[Mode: " + scriptType.name() + "]");
		meta.setLore(SBConfig.getScriptEditorLore(scriptType));
		item.setItemMeta(meta);
		return item;
	}

	public static boolean isBlockSelector(Player player, ItemStack item) {
		return isItem(item, Material.STICK, s -> s.equals("§dBlock Selector"));
	}

	public static boolean isScriptEditor(Player player, ItemStack item) {
		return isItem(item, Material.BLAZE_ROD, s -> s.startsWith("§dScript Editor§6[Mode: "));
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItemInMainHand(Player player) {
		PlayerInventory inventory = player.getInventory();
		return Utils.isCBXXXorLater("1.9") ? inventory.getItemInMainHand() : inventory.getItemInHand();
	}

	public static ItemStack getItemInOffHand(Player player) {
		return Utils.isCBXXXorLater("1.9") ? player.getInventory().getItemInOffHand() : null;
	}

	public static ItemStack[] getHandItems(Player player) {
		return new ItemStack[] { getItemInMainHand(player), getItemInOffHand(player) };
	}

	public static void setName(ItemStack item, String name) {
		if (item != null && StringUtils.isNotEmpty(name)) {
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(name);
			item.setItemMeta(itemMeta);
		}
	}

	public static String getName(ItemStack item, String def) {
		if (item == null || item.getType() == Material.AIR) {
			return def;
		}
		ItemMeta meta = item.getItemMeta();
		return meta == null ? def : meta.hasDisplayName() ? meta.getDisplayName() : def;
	}

	public static boolean isItem(ItemStack item, Material material, Predicate<String> name) {
		return item != null && item.getType() == material && name.test(getName(item, Material.AIR.name()));
	}
}