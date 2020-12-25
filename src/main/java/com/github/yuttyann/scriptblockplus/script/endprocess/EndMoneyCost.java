package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.hook.plugin.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.vault.MoneyCost;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus EndMoneyCost エンドプロセスクラス
 * @author yuttyann44581
 */
public class EndMoneyCost implements EndProcess {

    @Override
    @NotNull
    public EndProcess newInstance() {
        return new EndMoneyCost();
    }

    @Override
    public void success(@NotNull SBRead sbRead) {
        
    }

    @Override
    public void failed(@NotNull SBRead sbRead) {
        VaultEconomy economy = VaultEconomy.INSTANCE;
        if (economy.isEnabled() && sbRead.has(MoneyCost.KEY)) {
            economy.depositPlayer(sbRead.getSBPlayer().getOfflinePlayer(), sbRead.getDouble(MoneyCost.KEY));
        }
    }
}