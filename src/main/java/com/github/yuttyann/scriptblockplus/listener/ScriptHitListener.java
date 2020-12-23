package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.event.ScriptBlockHitEvent;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ScriptHitListener implements Listener {

	private static final String KEY_HIT = Utils.randomUUID();

	@EventHandler(priority = EventPriority.HIGH)
	public void onProjectileHit(ProjectileHitEvent event) {
		Block block = getHitBlock(event);
		ProjectileSource shooter = event.getEntity().getShooter();
		if (block == null || !(shooter instanceof Player)) {
			return;
		}
		Location location = block.getLocation();
		if (BlockScriptJson.has(location, ScriptType.HIT)) {
			Player player = (Player) shooter;
			if (isTwice(player)) {
				return;
			}
			ScriptBlockHitEvent hitEvent = new ScriptBlockHitEvent(player, block);
			Bukkit.getServer().getPluginManager().callEvent(hitEvent);
			if (hitEvent.isCancelled()) {
				return;
			}
			new ScriptRead(player, location, ScriptType.HIT).read(0);
		}
	}

	private boolean isTwice(@NotNull Player player) {
		if (!Utils.isCBXXXorLater("1.16")) {
			return false;
		}
		ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
		if (objectMap.getBoolean(KEY_HIT)) {
			objectMap.remove(KEY_HIT);
			return true;
		}
		objectMap.put(KEY_HIT, true);
		return false;
	}

	@Nullable
	private Block getHitBlock(@NotNull ProjectileHitEvent event) {
		Predicate<Method> isGetHitBlock = m -> m.getName().equals("getHitBlock");
		if (Stream.of(event.getClass().getMethods()).anyMatch(isGetHitBlock)) {
			return event.getHitBlock();
		}
		Projectile projectile = event.getEntity();
		World world = projectile.getWorld();
		Vector start = projectile.getLocation().toVector();
		Vector direction = projectile.getVelocity().normalize();
		BlockIterator iterator = new BlockIterator(world, start, direction, 0.0D, 4);
		Block hitBlock = null;
		while (iterator.hasNext()) {
			hitBlock = iterator.next();
			if (hitBlock.getType() != Material.AIR) {
				break;
			}
		}
		return hitBlock;
	}
}