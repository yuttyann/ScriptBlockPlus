package com.github.yuttyann.scriptblockplus.script.option.hook;

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
		for (int x1 = min.getBlockX(), x2 = max.getBlockX(); x1 <= x2; x1++) {
			for (int y1 = min.getBlockY(), y2 = max.getBlockY(); y1 <= y2; y1++) {
				for (int z1 = min.getBlockZ(), z2 = max.getBlockZ(); z1 <= z2; z1++) {
					blocks.add(world.getBlockAt(x1, y1, z1));
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