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

    /**
     * 経済プラグインを読み込みます。
     * @return {@link VaultEconomy} - インスタンス
     */
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

    /**
     * 経済プラグインが有効なのかどうか。
     * @return {@link boolean} - 有効な場合は{@code true}
     */
    public boolean isEnabled() {
        return economy != null && economy.isEnabled();
    }

    /**
     * 経済プラグインの名前を取得します。
     * @return {@link String} - 経済プラグインの名前
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * プレイヤーが、指定された金額を所持しているのかどうか。
     * @param player - プレイヤー
     * @param amount - 金額
     * @return {@link boolean} - 所持している場合は{@code true}
     */
    public boolean has(@NotNull OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }

    /**
     * プレイヤーの所持金から、指定された金額を引き落とします。
     * @param player - プレイヤー
     * @param amount - 金額
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean withdrawPlayer(@NotNull OfflinePlayer player, double amount) {
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    /**
     * プレイヤーの所持金に、指定された金額を入金します。
     * @param player - プレイヤー
     * @param amount - 金額
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean depositPlayer(@NotNull OfflinePlayer player, double amount) {
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    /**
     * プレイヤーの所持金を取得します。
     * @param player - プレイヤー
     * @return {@link double} - 所持金
     */
    public double getBalance(@NotNull OfflinePlayer player) {
        return economy.getBalance(player);
    }

    /**
     * 指定した金額を、経済プラグインの形式に変換します。
     * @param amount - 金額
     * @return {@link String} - 変換された文字列
     */
    @NotNull
    public String format(double amount) {
        return economy.format(amount);
    }
}