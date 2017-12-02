package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.option.vault.MoneyCost;

public class EndMoneyCost implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		if (HookPlugins.getVaultEconomy().isEnabled()) {
			sbRead.getSBPlayer().removeData(MoneyCost.KEY_COST);
		}
	}

	@Override
	public void failed(SBRead sbRead) {
		VaultEconomy economy = HookPlugins.getVaultEconomy();
		if (economy.isEnabled()) {
			SBPlayer sbPlayer = sbRead.getSBPlayer();
			Double cost = (Double) sbPlayer.getData(MoneyCost.KEY_COST);
			if (cost != null) {
				sbPlayer.removeData(MoneyCost.KEY_COST);
				economy.depositPlayer(sbPlayer.getOfflinePlayer(), cost);
			}
		}
	}
}