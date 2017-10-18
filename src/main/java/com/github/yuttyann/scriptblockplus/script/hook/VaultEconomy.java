package com.github.yuttyann.scriptblockplus.script.hook;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public final class VaultEconomy {

	private Economy economy;

	private VaultEconomy(Economy economy) {
		this.economy = economy;
	}

	protected static VaultEconomy setupEconomy() {
		ServicesManager services = Bukkit.getServicesManager();
		RegisteredServiceProvider<Economy> provider = services.getRegistration(Economy.class);
		if (provider != null) {
			VaultEconomy vault = new VaultEconomy(provider.getProvider());
			if (vault.isEnabled()) {
				return vault;
			}
		}
		return new VaultEconomy(null);
	}

	public boolean isEnabled() {
		return economy != null && economy.isEnabled();
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