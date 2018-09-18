package com.github.yuttyann.scriptblockplus.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ItemData extends ItemStack {

    public ItemData(ItemStack item) {
    	super(item);
	}

	@SuppressWarnings("deprecation")
	public void setDamage(int damage) {
		if (Utils.isCBXXXorLater("1.13")) {
	        ItemMeta meta = getItemMeta();
	        if (meta != null) {
	            ((Damageable) meta).setDamage(damage);
	            setItemMeta(meta);
	        }
		} else {
			super.setDurability((short) damage);
		}
    }

	@SuppressWarnings("deprecation")
	public int getDamage() {
		if (Utils.isCBXXXorLater("1.13")) {
			ItemMeta meta = getItemMeta();
	        return meta == null ? 0 : ((Damageable) meta).getDamage();
		}
		return super.getDurability();
	}

	public String getDisplayName() {
        ItemMeta meta = getItemMeta();
		return meta != null && meta.hasDisplayName() ? meta.getDisplayName() : null;
	}

	public List<String> getLore() {
        ItemMeta meta = getItemMeta();
		return meta != null && meta.hasLore() ? meta.getLore() : new ArrayList<>();
	}

	public Map<Enchantment, Integer> getEnchants() {
        ItemMeta meta = getItemMeta();
		return meta != null && meta.hasEnchants() ? meta.getEnchants() : new HashMap<>();
	}

	public Set<ItemFlag> getItemFlags() {
        ItemMeta meta = getItemMeta();
		return meta == null ? new HashSet<>() : meta.getItemFlags();
	}
}