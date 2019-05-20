package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.Location;
import org.bukkit.World;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.IAssist;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Execute extends BaseOption {

	public Execute() {
		super("execute", "@execute:");
	}

	@Override
	public Option newInstance() {
		return new Execute();
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		String[] coords = StringUtils.split(array[1], ",");
		ScriptType scriptType = ScriptType.valueOf(array[0].toUpperCase());
		World world = Utils.getWorld(coords[0]);
		double x = Integer.parseInt(coords[1]);
		double y = Integer.parseInt(coords[2]);
		double z = Integer.parseInt(coords[3]);
		Location location = new Location(world, x, y, z);
		ScriptRead scriptRead = new ScriptRead(new IAssist(getPlugin(), scriptType), getPlayer(), location);
		if (!scriptRead.getScriptData().checkPath()) {
			Utils.sendMessage(getPlayer(), SBConfig.getErrorScriptFileCheckMessage());
			return false;
		}
		scriptRead.read(0);
		return true;
	}
}