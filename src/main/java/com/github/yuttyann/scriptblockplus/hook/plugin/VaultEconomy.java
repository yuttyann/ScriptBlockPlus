package com.github.yuttyann.scriptblockplus.hook.plugin;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus VaultEconomy クラス
 * @author yuttyann44581
 */
public final class VaultEconomy extends HookPlugin {

    public static final VaultEconomy INSTANCE = VaultEconomy.setupEconomy();

    private final String name;
    private final Economy economy;

    private VaultEconomy(@Nullable Economy economy) {
        this.name = economy == null ? "None" : economy.getName();
        this.economy = economy;
    }

    @Override
    @NotNull
    public String getPluginName() {
        return "Vault";
    }

    @NotNull
    private static VaultEconomy setupEconomy() {
        var provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider != null) {
            var vault = new VaultEconomy(provider.getProvider());
            if (vault.isEnabled()) {
                return vault;
            }
        }
        return new VaultEconomy(null);
    }

    public boolean isEnabled() {
        return economy != null && economy.isEnabled();
    }

    @NotNull
    public String getName() {
        return name;
    }

    public double getBalance(@NotNull OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public boolean has(@NotNull OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }

    public boolean withdrawPlayer(@NotNull OfflinePlayer player, double amount) {
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean depositPlayer(@NotNull OfflinePlayer player, double amount) {
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public boolean setPlayer(@NotNull OfflinePlayer player, double amount) {
        double balance = economy.getBalance(player);
        if (balance > amount) {
            return withdrawPlayer(player, balance - amount);
        } else if (balance < amount) {
            return depositPlayer(player, amount - balance);
        }
        return true;
    }

    @NotNull
    public String format(double amount) {
        return economy.format(amount);
    }
}