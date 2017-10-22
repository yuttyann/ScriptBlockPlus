package com.github.yuttyann.scriptblockplus.script.endprocess;

import java.util.List;

import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;

public interface SBRead {

	public Plugin getPlugin();

	public SBPlayer getSBPlayer();

	public BlockCoords getBlockCoords();

	public MapManager getMapManager();

	public OptionManager getOptionManager();

	public EndProcessManager getEndProcessManager();

	public String getOptionValue();

	public List<String> getScripts();

	public ScriptType getScriptType();

	public ScriptData getScriptData();

	public int getScriptIndex();
}