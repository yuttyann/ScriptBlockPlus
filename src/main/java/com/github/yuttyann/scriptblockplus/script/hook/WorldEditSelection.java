package com.github.yuttyann.scriptblockplus.script.hook;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public final class WorldEditSelection {

	private WorldEditPlugin worldEdit;

	private WorldEditSelection(Plugin plugin) {
		this.worldEdit = (WorldEditPlugin) plugin;
	}

	static WorldEditSelection setupWorldEditAPI() {
		return new WorldEditSelection(Bukkit.getPluginManager().getPlugin("WorldEdit"));
	}

	public List<Block> getBlocks(Selection selection) {
		List<Block> blocks = new ArrayList<Block>();
		if (!(selection instanceof CuboidSelection)) {
			return blocks;
		}
		World world = selection.getWorld();
		Location min = selection.getMinimumPoint();
		Location max = selection.getMaximumPoint();
		for (int x = min.getBlockX(), x_max = max.getBlockX(); x <= x_max; x++) {
			for (int y = min.getBlockY(), y_max = max.getBlockY(); y <= y_max; y++) {
				for (int z = min.getBlockZ(), z_max = max.getBlockZ(); z <= z_max; z++) {
					blocks.add(world.getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}

	public Selection getSelection(Player player) {
		return worldEdit.getSelection(player);
	}

	public WorldEdit getWorldEdit() {
		return worldEdit.getWorldEdit();
	}

	public Material getWandType() {
		@SuppressWarnings("deprecation")
		Material material = Material.getMaterial(worldEdit.getConfig().getInt("wand-item"));
		return material;
	}
}