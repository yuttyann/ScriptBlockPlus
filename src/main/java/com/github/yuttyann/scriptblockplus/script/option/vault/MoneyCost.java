package com.github.yuttyann.scriptblockplus.script.option.vault;

import java.util.Map;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class MoneyCost extends BaseOption {

	public MoneyCost(ScriptManager scriptManager) {
		super(scriptManager, "moneycost", "$cost:");
	}

	@Override
	public boolean isValid() {
		double cost = Double.parseDouble(optionData);
		if (vaultEconomy.has(player, cost)) {
			vaultEconomy.withdrawPlayer(player, cost);
			putMoneyCosts(cost);
			return true;
		}
		double result = cost - vaultEconomy.getBalance(player);
		Utils.sendPluginMessage(player, Lang.getErrorCostMessage(cost, result));
		return false;
	}

	private void putMoneyCosts(double cost) {
		Map<UUID, Double> moneyCosts = mapManager.getMoneyCosts();
		if (moneyCosts.containsKey(uuid)) {
			cost += moneyCosts.get(uuid);
		}
		moneyCosts.put(uuid, cost);
	}
}