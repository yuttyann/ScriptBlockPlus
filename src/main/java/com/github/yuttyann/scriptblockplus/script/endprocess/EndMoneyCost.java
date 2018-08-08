package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.option.vault.MoneyCost;

public class EndMoneyCost implements EndProcess {

	@Override
	public void success(SBRead sbRead) {
		if (HookPlugins.getVaultEconomy().isEnabled()) {
			sbRead.removeData(MoneyCost.KEY_COST);
		}
	}

	@Override
	public void failed(SBRead sbRead) {
		VaultEconomy economy = HookPlugins.getVaultEconomy();
		if (economy.isEnabled() && sbRead.hasData(MoneyCost.KEY_COST)) {
			sbRead.removeData(MoneyCost.KEY_COST);
			economy.depositPlayer(sbRead.getSBPlayer().getOfflinePlayer(), sbRead.getDouble(MoneyCost.KEY_COST));
		}
	}
}