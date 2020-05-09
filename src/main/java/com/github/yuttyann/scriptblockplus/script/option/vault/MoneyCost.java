package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.hook.HookPlugins;
import com.github.yuttyann.scriptblockplus.script.hook.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus MoneyCost オプションクラス
 * @author yuttyann44581
 */
public class MoneyCost extends BaseOption {

	public static final String KEY = Utils.randomUUID();

	public MoneyCost() {
		super("moneycost", "$cost:");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new MoneyCost();
	}

	@Override
	protected boolean isValid() throws Exception {
		VaultEconomy vaultEconomy = HookPlugins.getVaultEconomy();
		if (!vaultEconomy.isEnabled()) {
			throw new UnsupportedOperationException();
		}
		Player player = getPlayer();
		double cost = Double.parseDouble(getOptionValue());
		if (vaultEconomy.has(player, cost)) {
			vaultEconomy.withdrawPlayer(player, cost);
			getSBRead().put(KEY, cost + (getSBRead().has(KEY) ? getSBRead().getDouble(KEY) : 0.0D));
			return true;
		}
		double result = cost - vaultEconomy.getBalance(player);
		SBConfig.ERROR_COST.replace(cost, result).send(player);
		return false;
	}
}