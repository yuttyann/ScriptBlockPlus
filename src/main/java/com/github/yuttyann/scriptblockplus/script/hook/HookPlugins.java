package com.github.yuttyann.scriptblockplus.script.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HookPlugins {

	private static Boolean hasVault;
	private static Boolean hasPlaceholderAPI;
	private static VaultEconomy vaultEconomy;
	private static VaultPermission vaultPermission;

	@Nullable
	public static <T extends Plugin> T getPlugin(@NotNull String name) {
		return (T) Bukkit.getPluginManager().getPlugin(name);
	}

	public static boolean hasVault() {
		if (hasVault == null) {
			hasVault = Bukkit.getPluginManager().isPluginEnabled("Vault");
		}
		return hasVault;
	}

	public static boolean hasPlaceholderAPI() {
		if (hasPlaceholderAPI == null) {
			hasPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
		}
		return hasPlaceholderAPI;
	}

	public static VaultEconomy getVaultEconomy() {
		if (vaultEconomy == null) {
			vaultEconomy = VaultEconomy.setupEconomy();
		}
		return vaultEconomy;
	}

	public static VaultPermission getVaultPermission() {
		if (vaultPermission == null) {
			vaultPermission = VaultPermission.setupPermission();
		}
		return vaultPermission;
	}
}