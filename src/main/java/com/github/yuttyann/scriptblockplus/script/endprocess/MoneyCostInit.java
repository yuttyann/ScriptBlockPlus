package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;

public class MoneyCostInit implements EndProcess {

	@Override
	public void success(ScriptRead scriptRead) {
		VaultEconomy economy = scriptRead.getVaultEconomy();
		if (economy.isEnabled()) {
			SBPlayer sbPlayer = scriptRead.getSBPlayer();
			Double cost = sbPlayer.getData("MoneyCost");
			if (cost != null) {
				sbPlayer.removeData("MoneyCost");
				economy.depositPlayer(sbPlayer.getOfflinePlayer(), cost);
			}
		}
	}

	@Override
	public void failed(ScriptRead scriptRead) {
		if (scriptRead.getVaultEconomy().isEnabled()) {
			scriptRead.getSBPlayer().removeData("MoneyCost");
		}
	}
}