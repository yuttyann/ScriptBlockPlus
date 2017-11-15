package com.github.yuttyann.scriptblockplus.script.option.other;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Calculation extends BaseOption {

	public interface CalculationValue {

		public double getValue(BaseOption baseOption, String variable) throws Exception;
	}

	static class DefaultVariable implements CalculationValue {

		@SuppressWarnings("deprecation")
		@Override
		public double getValue(BaseOption baseOption, String variable) throws Exception {
			if (variable.startsWith("%players_in_range_") && variable.charAt(variable.length() - 1) == '%') {
				variable = variable.substring(0, variable.length() - 1);
				String distance = StringUtils.split(variable, "%players_in_range_")[1];
				int i = 10;
				try {
					i = Integer.parseInt(distance);
				} catch (NumberFormatException e) {}
				return getNearbyPlayers(baseOption.getBlockCoords(), i);
			}
			if (variable.startsWith("%player_ping_") && variable.charAt(variable.length() - 1) == '%') {
				variable = variable.substring(0, variable.length() - 1);
				Player target = Bukkit.getPlayer(StringUtils.split(variable, "%player_ping_")[1]);
				Object handle = target.getClass().getMethod("getHandle").invoke(target);
				return (int) handle.getClass().getField("ping").get(handle);
			}
			Player player = baseOption.getPlayer();
			if (variable.startsWith("%objective_score_") && variable.charAt(variable.length() - 1) == '%') {
				variable = variable.substring(0, variable.length() - 1);
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getMainScoreboard();
				return (int) board.getObjective(variable).getScore(player).getScore();
			}
			switch (variable) {
			case "%server_online%":
				return Utils.getOnlinePlayers().size();
			case "%server_offline%":
				return Bukkit.getOfflinePlayers().length;
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
				return player.getBedSpawnLocation() != null ? player.getBedSpawnLocation().getBlockX() : 0.0D;
			case "%player_bed_y%":
				return player.getBedSpawnLocation() != null ? player.getBedSpawnLocation().getBlockY() : 0.0D;
			case "%player_bed_z%":
				return player.getBedSpawnLocation() != null ? player.getBedSpawnLocation().getBlockZ() : 0.0D;
			case "%player_compass_x%":
				return player.getCompassTarget() != null ? player.getCompassTarget().getBlockX() : 0.0D;
			case "%player_compass_y%":
				return player.getCompassTarget() != null ? player.getCompassTarget().getBlockY() : 0.0D;
			case "%player_compass_z%":
				return player.getCompassTarget() != null ? player.getCompassTarget().getBlockZ() : 0.0D;
			case "%player_gamemode%":
				return player.getGameMode().getValue();
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
				return player.getMaxHealth();
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
			case "%economy_balance%":
				VaultEconomy vaultEconomy = HookPlugins.getVaultEconomy();
				return vaultEconomy.isEnabled() ? vaultEconomy.getBalance(player) : 0.0D;
			default:
				return 0.0D;
			}
		}

		private int getNearbyPlayers(BlockCoords blockCoords, int distance) {
			int count = 0;
			int result = distance * distance;
			for (Player player : blockCoords.getWorld().getPlayers()) {
				if (player.getLocation().distanceSquared(blockCoords) <= result) {
					count++;
				}
			}
			return count;
		}
	}

	private static final List<CalculationValue> VALUES = new ArrayList<CalculationValue>();

	static {
		VALUES.add(new DefaultVariable());
	}

	public Calculation() {
		super("calculation", "@calc:");
	}

	public static List<CalculationValue> getValues() {
		return VALUES;
	}

	@Override
	public boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), " ");
		double value1 = parse(array[0]);
		double value2 = parse(array[2]);
		String operator = array[1];

		if (result(value1, value2, operator)) {
			return true;
		}
		String message = StringUtils.createString(array, 3);
		message = StringUtils.replaceColorCode(message, true);
		message = StringUtils.replace(message, "%value1%", String.valueOf(value1));
		message = StringUtils.replace(message, "%value2%", String.valueOf(value2));
		message = StringUtils.replace(message, "%operator%", operator);
		Utils.sendMessage(getPlayer(), message);
		return false;
	}

	private double parse(String source) throws Exception {
		try {
			return Double.parseDouble(source);
		} catch (NumberFormatException e) {}

		double result = 0.0D;
		for (CalculationValue value : getValues()) {
			result = value.getValue(this, source);
			if (result > 0.0D) {
				break;
			}
		}
		return result;
	}

	private boolean result(double value1, double value2, String operator) {
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