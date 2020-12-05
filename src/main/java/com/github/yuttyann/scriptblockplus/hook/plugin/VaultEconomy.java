package com.github.yuttyann.scriptblockplus.hook.plugin;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus VaultEconomy クラス
 * @author yuttyann44581
 */
public final class VaultEconomy extends HookPlugin {

	public static final VaultEconomy INSTANCE = VaultEconomy.setupEconomy();

	private final Economy economy;
	private final String name;

	private VaultEconomy(@Nullable Economy economy) {
		this.economy = economy;
		this.name = economy == null ? "None" : economy.getName();
	}

	@Override
	@NotNull
	public String getPluginName() {
		return "Vault";
	}

	@NotNull
	static VaultEconomy setupEconomy() {
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
		EconomyResponse response = economy.withdrawPlayer(player, amount);
		return response.transactionSuccess();
	}

	public boolean depositPlayer(@NotNull OfflinePlayer player, double amount) {
		EconomyResponse response = economy.depositPlayer(player, amount);
		return response.transactionSuccess();
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