package com.github.yuttyann.scriptblockplus.listener.item.action;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.listener.item.ChangeSlot;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.listener.item.RunItem;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegionBlocks;
import com.github.yuttyann.scriptblockplus.region.PlayerRegion;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus ScriptViewer クラス
 * @author yuttyann44581
 */
public class ScriptViewer extends ItemAction {

	private static final Set<UUID> PLAYERS = new HashSet<>();

	static {
		new Task().runTaskTimer(ScriptBlock.getInstance(), 0L, 8L);
	}

	public ScriptViewer() {
		super(ItemUtils.getScriptViewer());
	}

	@Override
	public boolean hasPermission(@NotNull Permissible permissible) {
		return Permission.TOOL_SCRIPT_VIEWER.has(permissible);
	}

	@Override
	public void slot(@NotNull ChangeSlot changeSlot) {

	}

	@Override
	public void run(@NotNull RunItem runItem) {
		SBPlayer sbPlayer = SBPlayer.fromPlayer(runItem.getPlayer());
		switch (runItem.getAction()) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				PLAYERS.add(sbPlayer.getUniqueId());
				SBConfig.SCRIPT_VIEWER_START.send(sbPlayer);
				break;
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				PLAYERS.remove(sbPlayer.getUniqueId());
				SBConfig.SCRIPT_VIEWER_STOP.send(sbPlayer);
				break;
			default:
		}
	}

	private static class Task extends BukkitRunnable {

		@Override
		public void run() {
			for (UUID uuid : PLAYERS) {
				SBPlayer sbPlayer = SBPlayer.fromUUID(uuid);
				if (!sbPlayer.isOnline()) {
					continue;
				}
				Set<Block> temp = new HashSet<>();
				Region region = new PlayerRegion(sbPlayer.getPlayer(), 15);
				new CuboidRegionBlocks(region).forEach(b -> temp.add(b.getBlock(sbPlayer.getWorld())));
				for (ScriptType scriptType : ScriptType.values()) {
					BlockScript blockScript = new BlockScriptJson(scriptType).load();
					for (Block block : temp) {
						if (blockScript.has(block.getLocation())) {
							spawnParticlesOnBlock(sbPlayer.getPlayer(), block, block.getType() == Material.AIR);
						}
					}
				}
			}
		}

		private void spawnParticlesOnBlock(@NotNull Player player, @NotNull Block block, boolean isAIR) {
			double x = block.getX();
			double y = block.getY();
			double z = block.getZ();
			Color color = isAIR ? Color.AQUA : Color.LIME;
			if (Utils.isCBXXXorLater("1.13")) {
				Particle.DustOptions dust = new Particle.DustOptions(color, 1);
				player.spawnParticle(Particle.REDSTONE, x, y, z, 0, 0, 0, 0, dust);
				player.spawnParticle(Particle.REDSTONE, x + 1, y, z, 0, 0, 0, 0, dust);
				player.spawnParticle(Particle.REDSTONE, x + 1, y, z + 1, 0, 0, 0, 0, dust);
				player.spawnParticle(Particle.REDSTONE, x, y, z + 1, 0, 0, 0, 0, dust);
				player.spawnParticle(Particle.REDSTONE, x, y + 1, z, 0, 0, 0, 0, dust);
				player.spawnParticle(Particle.REDSTONE, x + 1, y + 1, z, 0, 0, 0, 0, dust);
				player.spawnParticle(Particle.REDSTONE, x + 1, y + 1, z + 1, 0, 0, 0, 0, dust);
				player.spawnParticle(Particle.REDSTONE, x, y + 1, z + 1, 0, 0, 0, 0, dust);
			} else {
				double r = (color.getRed() - 0.0001) / 255D;
				double g = (color.getGreen() - 0.0001) / 255D;
				double b = (color.getBlue() - 0.0001) / 255D;
				player.spawnParticle(Particle.REDSTONE, x, y, z, 0, r, g, b, 1);
				player.spawnParticle(Particle.REDSTONE, x + 1, y, z, 0, r, g, b, 1);
				player.spawnParticle(Particle.REDSTONE, x + 1, y, z + 1, 0, r, g, b, 1);
				player.spawnParticle(Particle.REDSTONE, x, y, z + 1, 0, r, g, b, 1);
				player.spawnParticle(Particle.REDSTONE, x, y + 1, z, 0, r, g, b, 1);
				player.spawnParticle(Particle.REDSTONE, x + 1, y + 1, z, 0, r, g, b, 1);
				player.spawnParticle(Particle.REDSTONE, x + 1, y + 1, z + 1, 0, r, g, b, 1);
				player.spawnParticle(Particle.REDSTONE, x, y + 1, z + 1, 0, r, g, b, 1);
			}
		}
	}
}