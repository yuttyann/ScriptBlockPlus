package com.github.yuttyann.scriptblockplus.script.option.vault;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class MoneyCost extends BaseOption {

	public MoneyCost() {
		super("moneycost", "$cost:");
	}

	@Override
	public boolean isValid() throws Exception {
		VaultEconomy vaultEconomy = HookPlugins.getVaultEconomy();
		if (!vaultEconomy.isEnabled()) {
			throw new UnsupportedOperationException();
		}
		Player player = getPlayer();
		double cost = Double.parseDouble(getOptionValue());
		if (vaultEconomy.has(player, cost)) {
			vaultEconomy.withdrawPlayer(player, cost);
			SBPlayer sbPlayer = SBPlayer.get(player);
			Double value = (Double) sbPlayer.getData("MoneyCost");
			sbPlayer.setData("MoneyCost", cost + (value == null ? 0.0D : value.doubleValue()));
			return true;
		}
		double result = cost - vaultEconomy.getBalance(player);
		Utils.sendMessage(player, SBConfig.getErrorCostMessage(cost, result));
		return false;
	}
}