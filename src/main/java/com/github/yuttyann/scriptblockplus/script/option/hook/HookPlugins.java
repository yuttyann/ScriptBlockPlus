package com.github.yuttyann.scriptblockplus.script.option.hook;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public class HookPlugins {

	private static Boolean hasVault;
	private static Boolean hasWorldEdit;
	private static VaultEconomy vaultEconomy;
	private static VaultPermission vaultPermission;
	private static WorldEditSelection worldEditSelection;

	public static boolean hasVault() {
		if (hasVault == null) {
			hasVault = Utils.isPluginEnabled("Vault");
		}
		return hasVault;
	}

	public static boolean hasWorldEdit() {
		if (hasWorldEdit == null) {
			hasWorldEdit = Utils.isPluginEnabled("WorldEdit");
		}
		return hasWorldEdit;
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

	public static WorldEditSelection getWorldEditSelection() {
		if (worldEditSelection == null) {
			worldEditSelection = WorldEditSelection.setupWorldEditAPI();
		}
		return worldEditSelection;
	}
}