/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.hook.plugin;

import net.milkbowl.vault.economy.Economy;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus VaultEconomy クラス
 * @author yuttyann44581
 */
public final class VaultEconomy extends HookPlugin {

    public static final VaultEconomy INSTANCE = new VaultEconomy();

    private String name;
    private Economy economy;

    private VaultEconomy() { }

    @Override
    @NotNull
    public String getPluginName() {
        return "Vault";
    }

    @NotNull
    public VaultEconomy setupEconomy() {
        var provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider != null) {
            var vault = setVaultEconomy(provider.getProvider());
            if (vault.isEnabled()) {
                return vault;
            }
        }
        return setVaultEconomy(null);
    }

    private VaultEconomy setVaultEconomy(@Nullable Economy economy) {
        this.name = economy == null ? "None" : economy.getName();
        this.economy = economy;
        return this;
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