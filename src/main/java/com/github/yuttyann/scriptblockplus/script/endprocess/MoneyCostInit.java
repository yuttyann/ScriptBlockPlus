package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptRead.EndProcess;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;

public class MoneyCostInit implements EndProcess {

	@Override
	public void success(ScriptRead scriptRead) {
		VaultEconomy economy = scriptRead.getVaultEconomy();
		if (economy.isEnabled()) {
			SBPlayer sbPlayer = SBPlayer.get(scriptRead.getUniqueId());
			Double cost = sbPlayer.getMoneyCost();
			if (cost != null) {
				sbPlayer.setMoneyCost(null, false);
				economy.depositPlayer(scriptRead.getPlayer(), cost);
			}
		}
	}

	@Override
	public void failed(ScriptRead scriptRead) {
		if (scriptRead.getVaultEconomy().isEnabled()) {
			SBPlayer.get(scriptRead.getUniqueId()).setMoneyCost(null, false);
		}
	}
}