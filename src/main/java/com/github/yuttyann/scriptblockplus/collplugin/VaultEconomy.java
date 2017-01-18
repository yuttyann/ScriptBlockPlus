package com.github.yuttyann.scriptblockplus.collplugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class VaultEconomy {

	private Economy economy;

	private VaultEconomy() {
		throw new AssertionError();
	}

	protected static VaultEconomy setupEconomy() {
		if (!CollPlugins.hasVault()) {
			return null;
		}
		ServicesManager services = Bukkit.getServer().getServicesManager();
		RegisteredServiceProvider<Economy> provider = services.getRegistration(Economy.class);
		if (provider != null) {
			VaultEconomy vault = new VaultEconomy();
			vault.economy = provider.getProvider();
			try {
				if (vault.economy.isEnabled()) {
					return vault;
				}
			} catch (Exception e) {
				return vault;
			}
		}
		return null;
	}

	public double getBalance(OfflinePlayer player) {
		return economy.getBalance(player);
	}

	public boolean has(OfflinePlayer player, double amount) {
		return economy.has(player, amount);
	}

	public boolean withdrawPlayer(OfflinePlayer player, double amount) {
		EconomyResponse response = economy.withdrawPlayer(player, amount);
		return response.transactionSuccess();
	}

	public boolean depositPlayer(OfflinePlayer player, double amount) {
		EconomyResponse response = economy.depositPlayer(player, amount);
		return response.transactionSuccess();
	}

	public boolean setPlayer(OfflinePlayer player, double amount) {
		double balance = economy.getBalance(player);
		if (balance > amount) {
			return withdrawPlayer(player, (balance - amount));
		} else if (balance < amount) {
			return depositPlayer(player, (amount - balance));
		}
		return true;
	}

	public String format(double amount) {
		return economy.format(amount);
	}
}
