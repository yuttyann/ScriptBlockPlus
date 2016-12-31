package com.github.yuttyann.scriptblockplus.option;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.collplugin.VaultEconomy;

public class MoneyCost {

	private double amount, result;
	private boolean isSuccess;

	public MoneyCost(double amount) {
		this.amount = amount;
	}

	public MoneyCost(String amount) {
		this.amount = Double.parseDouble(amount);
	}

	public double getCost() {
		return amount;
	}

	public double getResult() {
		return result;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public boolean payment(Player player) {
		VaultEconomy economy = CollPlugins.getVaultEconomy();
		if (economy.has(player, getCost())) {
			economy.withdrawPlayer(player, getCost());
			isSuccess = true;
		} else {
			result = getCost() - economy.getBalance(player);
		}
		return isSuccess;
	}
}