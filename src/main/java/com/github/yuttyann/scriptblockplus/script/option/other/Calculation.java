package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class Calculation extends BaseOption {

	static final Pattern REALNUMBER_PATTERN = Pattern.compile("^-?(0|[1-9]\\d*)(\\.\\d+|)$");

	public Calculation() {
		super("calculation", "@calc:");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new Calculation();
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), " ");
		double value1 = parse(array[0]);
		double value2 = parse(array[2]);
		String operator = array[1];

		if (result(value1, value2, operator)) {
			return true;
		}
		if (array.length > 3) {
			String message = StringUtils.createString(array, 3);
			message = StringUtils.replaceColorCode(message, true);
			message = StringUtils.replace(message, "%value1%", String.valueOf(value1));
			message = StringUtils.replace(message, "%value2%", String.valueOf(value2));
			message = StringUtils.replace(message, "%operator%", operator);
			Utils.sendMessage(getPlayer(), message);
		}
		return false;
	}

	private double parse(@NotNull String source) throws Exception {
		if (REALNUMBER_PATTERN.matcher(source).matches()) {
			return Double.parseDouble(source);
		}
		return getValue(getPlayer(), source);
	}

	private double getValue(@NotNull Player player, @NotNull String variable) throws Exception {
		if (variable.startsWith("%player_others_in_range_") && variable.endsWith("%")) {
			variable = variable.substring("%player_others_in_range_".length(), variable.length() - 1);
			int i = 10;
			try {
				i = Integer.parseInt(variable);
			} catch (NumberFormatException e) {}
			return getNearbyOthers(player, i);
		}
		if (variable.startsWith("%player_count_") && variable.endsWith("%")) {
			variable = variable.substring("%player_count_".length(), variable.length() - 1);
			BlockCoords blockCoords = BlockCoords.fromString(variable);
			return getSBPlayer().getPlayerCount().getInfo(blockCoords, getScriptType()).getAmount();
		}
		if (variable.startsWith("%player_ping_") && variable.endsWith("%")) {
			variable = variable.substring("%player_ping_".length(), variable.length() - 1);
			Player target = Utils.getPlayer(variable);
			if (target == null) {
				return 0.0D;
			}
			Object handle = PackageType.CB_ENTITY.invokeMethod(target, "CraftPlayer", "getHandle");
			return PackageType.NMS.getField("EntityPlayer", "ping").getInt(handle);
		}
		if (variable.startsWith("%server_online_") && variable.endsWith("%")) {
			variable = variable.substring("%server_online_".length(), variable.length() - 1);
			return Utils.getWorld(variable).getPlayers().size();
		}
		if (variable.startsWith("%objective_score_") && variable.endsWith("%")) {
			variable = variable.substring("%objective_score_".length(), variable.length() - 1);
			Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(variable);
			return objective == null ? 0.0D : getScore(objective, player).getScore();
		}
		switch (variable) {
		case "%server_online%":
			return Bukkit.getOnlinePlayers().size();
		case "%server_offline%":
			return Bukkit.getOfflinePlayers().length;
		case "%player_count%":
			BlockCoords fullCoords = BlockCoords.fromString(getFullCoords());
			return getSBPlayer().getPlayerCount().getInfo(fullCoords, getScriptType()).getAmount();
		case "%player_ping%":
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			return (int) handle.getClass().getField("ping").get(handle);
		case "%player_x%":
			return player.getLocation().getBlockX();
		case "%player_y%":
			return player.getLocation().getBlockY();
		case "%player_z%":
			return player.getLocation().getBlockZ();
		case "%player_bed_x%":
			return player.getBedSpawnLocation() == null ? 0.0D : player.getBedSpawnLocation().getBlockX();
		case "%player_bed_y%":
			return player.getBedSpawnLocation() == null ? 0.0D : player.getBedSpawnLocation().getBlockY();
		case "%player_bed_z%":
			return player.getBedSpawnLocation() == null ? 0.0D : player.getBedSpawnLocation().getBlockZ();
		case "%player_compass_x%":
			return player.getCompassTarget() == null ? 0.0D : player.getCompassTarget().getBlockX();
		case "%player_compass_y%":
			return player.getCompassTarget() == null ? 0.0D : player.getCompassTarget().getBlockY();
		case "%player_compass_z%":
			return player.getCompassTarget() == null ? 0.0D : player.getCompassTarget().getBlockZ();
		case "%player_gamemode%":
			@SuppressWarnings("deprecation")
			int value = player.getGameMode().getValue();
			return value;
		case "%player_world_time%":
			return player.getWorld().getTime();
		case "%player_exp%":
			return player.getExp();
		case "%player_exp_to_level%":
			return player.getExpToLevel();
		case "%player_level%":
			return player.getLevel();
		case "%player_fly_speed%":
			return player.getFlySpeed();
		case "%player_food_level%":
			return player.getFoodLevel();
		case "%player_health%":
			return player.getHealth();
		case "%player_health_scale%":
			return player.getHealthScale();
		case "%player_last_damage%":
			return player.getLastDamage();
		case "%player_max_health%":
			@SuppressWarnings("deprecation")
			double health = player.getMaxHealth();
			return health;
		case "%player_max_air%":
			return player.getMaximumAir();
		case "%player_max_no_damage_ticks%":
			return player.getMaximumNoDamageTicks();
		case "%player_no_damage_ticks%":
			return player.getNoDamageTicks();
		case "%player_time%":
			return player.getPlayerTime();
		case "%player_time_offset%":
			return player.getPlayerTimeOffset();
		case "%player_remaining_air%":
			return player.getRemainingAir();
		case "%player_saturation%":
			return player.getSaturation();
		case "%player_sleep_ticks%":
			return player.getSleepTicks();
		case "%player_ticks_lived%":
			return player.getTicksLived();
		case "%player_seconds_lived%":
			return (player.getTicksLived() * 20);
		case "%player_minutes_lived%":
			return ((player.getTicksLived() * 20) / 60);
		case "%player_total_exp%":
			return player.getTotalExperience();
		case "%player_walk_speed%":
			return player.getWalkSpeed();
		case "%vault_eco_balance%":
			VaultEconomy vaultEconomy = HookPlugins.getVaultEconomy();
			return vaultEconomy.isEnabled() ? vaultEconomy.getBalance(player) : 0.0D;
		default:
			if (HookPlugins.hasPlaceholderAPI()) {
				String version = PlaceholderAPIPlugin.getInstance().getDescription().getVersion();
				if (Utils.isUpperVersion("2.8.8", version)) {
					return Double.parseDouble(PlaceholderAPI.setPlaceholders((OfflinePlayer) getPlayer(), variable));
				} else {
					@SuppressWarnings("deprecation")
					double result = Double.parseDouble(PlaceholderAPI.setPlaceholders(getPlayer(), variable));
					return result;
				}
			}
			return 0.0D;
		}
	}

	private int getNearbyOthers(@NotNull Player player, int distance) {
		int count = 0;
		int result = distance * distance;
		for (Player p : player.getLocation().getWorld().getPlayers()) {
			if (player != p && player.getLocation().distanceSquared(p.getLocation()) <= result) {
				count++;
			}
		}
		return count;
	}

	private Score getScore(@NotNull Objective objective, @NotNull Player player) {
		return objective.getScore(player.getName());
	}

	private boolean result(double value1, double value2, @NotNull String operator) {
		switch (operator) {
		case "<":
			return value1 < value2;
		case "<=":
			return value1 <= value2;
		case ">":
			return value1 > value2;
		case ">=":
			return value1 >= value2;
		case "==":
			return value1 == value2;
		case "!=":
			return value1 != value2;
		default:
			return false;
		}
	}
}