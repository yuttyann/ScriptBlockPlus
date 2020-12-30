package com.github.yuttyann.scriptblockplus.script.option.vault;

import com.github.yuttyann.scriptblockplus.hook.plugin.VaultEconomy;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus MoneyCost オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "moneycost", syntax = "$cost:")
public class MoneyCost extends BaseOption {

    public static final String KEY = Utils.randomUUID();

    @Override
    @NotNull
    public Option newInstance() {
        return new MoneyCost();
    }

    @Override
    protected boolean isValid() throws Exception {
        var vaultEconomy = VaultEconomy.INSTANCE;
        if (!vaultEconomy.isEnabled()) {
            throw new UnsupportedOperationException();
        }
        var player = getPlayer();
        double cost = Double.parseDouble(getOptionValue());
        if (vaultEconomy.has(player, cost)) {
            vaultEconomy.withdrawPlayer(player, cost);
            getTempMap().put(KEY, cost + (getTempMap().has(KEY) ? getTempMap().getDouble(KEY) : 0.0D));
            return true;
        }
        double result = cost - vaultEconomy.getBalance(player);
        SBConfig.ERROR_COST.replace(cost, result).send(player);
        return false;
    }
}