package com.github.yuttyann.scriptblockplus.script.option.vault;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class MoneyCost extends BaseOption {

	public MoneyCost() {
		super("moneycost", "$cost:");
	}

	@Override
	public boolean isValid() {
		VaultEconomy vaultEconomy = getVaultEconomy();
		if (!vaultEconomy.isEnabled()) {
			return false;
		}
		Player player = getPlayer();
		double cost = Double.parseDouble(getOptionValue());
		if (vaultEconomy.has(player, cost)) {
			vaultEconomy.withdrawPlayer(player, cost);
			SBPlayer.get(player).setMoneyCost(cost, true);
			return true;
		}
		double result = cost - vaultEconomy.getBalance(player);
		Utils.sendMessage(player, SBConfig.getErrorCostMessage(cost, result));
		return false;
	}
}