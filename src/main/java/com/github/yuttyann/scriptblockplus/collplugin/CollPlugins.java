package com.github.yuttyann.scriptblockplus.collplugin;

import com.github.yuttyann.scriptblockplus.util.Utils;

public class CollPlugins {

	private static Boolean hasVault;
	private static VaultEconomy vaultEconomy;
	private static VaultPermission vaultPermission;

	public static boolean hasVault() {
		if (hasVault == null) {
			hasVault = Utils.isPluginEnabled("Vault");
		}
		return hasVault;
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
