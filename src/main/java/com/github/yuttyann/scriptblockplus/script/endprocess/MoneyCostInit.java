package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;

public class MoneyCostInit implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		VaultEconomy economy = HookPlugins.getVaultEconomy();
		if (economy.isEnabled()) {
			SBPlayer sbPlayer = sbRead.getSBPlayer();
			Double cost = (Double) sbPlayer.getData("MoneyCost");
			if (cost != null) {
				sbPlayer.removeData("MoneyCost");
				economy.withdrawPlayer(sbPlayer.getOfflinePlayer(), cost);
			}
		}
	}

	@Override
	public void failed(SBRead sbRead) {
		if (HookPlugins.getVaultEconomy().isEnabled()) {
			sbRead.getSBPlayer().removeData("MoneyCost");
		}
	}
}