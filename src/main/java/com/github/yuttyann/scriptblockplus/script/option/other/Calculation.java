package com.github.yuttyann.scriptblockplus.script.option.other;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.collect.Maps;

public class Calculation extends BaseOption {

	public interface CalculationValue {

		public double getValue(BaseOption baseOption);
	}

	private static final Map<String, CalculationValue> VALUES = Maps.newHashMap();

	public Calculation() {
		super("calculation", "@calc:");
	}

	static Map<String, CalculationValue> getValues() {
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
		Utils.sendMessage(getSBPlayer(), message);
		return false;
	}

	@SuppressWarnings("deprecation")
	private double parse(String source) {
		Player player = getPlayer();
		source = StringUtils.replace(source, "%player_exp%", String.valueOf(player.getExp()));
		source = StringUtils.replace(source, "%player_level%", String.valueOf(player.getLevel()));
		source = StringUtils.replace(source, "%player_health%", String.valueOf(player.getHealth()));
		source = StringUtils.replace(source, "%player_maxhealth%", String.valueOf(player.getMaxHealth()));
		source = StringUtils.replace(source, "%player_gamemode%", String.valueOf(player.getGameMode().getValue()));
		source = StringUtils.replace(source, "%player_lastdamage%", String.valueOf(player.getLastDamage()));
		source = StringUtils.replace(source, "%player_score%", String.valueOf(player.getTotalExperience()));
		source = StringUtils.replace(source, "%player_money%", String.valueOf(getMoney(player)));
		source = StringUtils.replace(source, "%player_x%", String.valueOf(player.getLocation().getX()));
		source = StringUtils.replace(source, "%player_y%", String.valueOf(player.getLocation().getY()));
		source = StringUtils.replace(source, "%player_z%", String.valueOf(player.getLocation().getZ()));

		for (Entry<String, CalculationValue> entry : VALUES.entrySet()) {
			String key = entry.getKey();
			CalculationValue value = entry.getValue();
			source = StringUtils.replace(source, key, String.valueOf(value.getValue(this)));
		}

		return Double.parseDouble(source);
	}

	private double getMoney(Player player) {
		double money = 0.0D;
		if (HookPlugins.getVaultEconomy().isEnabled()) {
			money = HookPlugins.getVaultEconomy().getBalance(player);
		}
		return money;
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
