package com.github.yuttyann.scriptblockplus;

import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.yuttyann.scriptblockplus.collplugin.CollPlugins;
import com.github.yuttyann.scriptblockplus.command.ScriptBlockCommand;
import com.github.yuttyann.scriptblockplus.command.help.CommandView;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.listener.BlockListener;
import com.github.yuttyann.scriptblockplus.listener.PlayerInteractListener;
import com.github.yuttyann.scriptblockplus.listener.PlayerJoinQuitListener;
import com.github.yuttyann.scriptblockplus.listener.PlayerMoveListener;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class ScriptBlock extends JavaPlugin {

	public static ScriptBlock instance;
	private String encode;
	private HashMap<String, TabExecutor> commands;
	private HashMap<String, List<CommandView>> commandhelp;

	@Override
	public void onEnable() {
		instance = this;
		setupFile();
		if(!(Utils.getJavaVersion() >= 1.7))
		{
			Utils.sendPluginMessage(Messages.getOldJavaMessage());
			Utils.disablePlugin(this);
			return;
		}
		if (!CollPlugins.hasVault()) {
			Utils.sendPluginMessage(Messages.getNotVaultMessage());
			Utils.disablePlugin(this);
			return;
		}
		if (Utils.isPluginEnabled("ScriptBlock")) {
			Utils.disablePlugin(Utils.getPlugin("ScriptBlock"));
		}
		loadClass();
		loadCommand();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commands.get(command.getName()).onCommand(sender, command, label, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return commands.get(command.getName()).onTabComplete(sender, command, label, args);
	}

	public HashMap<String, List<CommandView>> getCommandHelp() {
		return commandhelp;
	}

	public String getEncode() {
		return encode;
	}

	public ScriptBlockAPI getAPI(Block block, ScriptType scriptType) {
		return new ScriptBlockAPI(block, scriptType);
	}

	private void setupFile() {
		if (Utils.isWindows() && !Utils.isUpperVersion_v19()) {
			encode = "s-jis";
		} else {
			encode = "utf-8";
		}
		String[] args = {"config", "messages"};
		Yaml.create(getDataFolder(), new StringBuilder(), encode, args);
		Files.reload();
	}

	private void loadClass() {
		new MapManager();
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
		getServer().getPluginManager().registerEvents(new Updater(this), this);
	}

	private void loadCommand() {
		commandhelp = new HashMap<String, List<CommandView>>();
		commands = new HashMap<String, TabExecutor>();
		commands.put("scriptblockplus", new ScriptBlockCommand());
	}
}