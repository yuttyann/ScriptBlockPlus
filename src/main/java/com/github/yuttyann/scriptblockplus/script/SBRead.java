package com.github.yuttyann.scriptblockplus.script;

import java.util.List;

import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;

public interface SBRead {

	public Plugin getPlugin();

	public SBPlayer getSBPlayer();

	public String getOptionValue();

	public BlockCoords getBlockCoords();

	public List<String> getScripts();

	public ScriptType getScriptType();

	public ScriptData getScriptData();

	public int getScriptIndex();
}