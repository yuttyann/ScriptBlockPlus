package com.github.yuttyann.scriptblockplus.hook;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WorldEditSelection {

	private WorldEditPlugin worldEdit;

	private WorldEditSelection(Plugin plugin) {
		this.worldEdit = (WorldEditPlugin) plugin;
	}

	protected static WorldEditSelection setupWorldEditAPI() {
		if (!HookPlugins.hasWorldEdit()) {
			return null;
		}
		return new WorldEditSelection(Utils.getPlugin("WorldEdit"));
	}

	public List<Block> getSelectionBlocks(Selection selection) {
		List<Block> blocks = new ArrayList<Block>();
		if (!(selection instanceof CuboidSelection)) {
			return blocks;
		}
		World world = selection.getWorld();
		Location min = selection.getMinimumPoint();
		Location max = selection.getMaximumPoint();
		for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
			for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
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
}