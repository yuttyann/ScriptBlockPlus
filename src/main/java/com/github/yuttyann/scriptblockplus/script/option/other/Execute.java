package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.Location;
import org.bukkit.World;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Execute extends BaseOption {

	public Execute() {
		super("execute", "@execute:");
	}

	@Override
	public boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		String[] coords = StringUtils.split(array[1], "-");
		ScriptType scriptType = ScriptType.valueOf(array[0].toUpperCase());
		World world = Utils.getWorld(coords[0]);
		double x = Integer.parseInt(coords[1]);
		double y = Integer.parseInt(coords[2]);
		double z = Integer.parseInt(coords[3]);
		Location location = new Location(world, x, y, z);

		ScriptBlockAPI scriptBlockAPI = ScriptBlock.getInstance().getAPI(location, scriptType);
		if (!scriptBlockAPI.checkPath()) {
			Utils.sendMessage(getSBPlayer(), SBConfig.getErrorScriptFileCheckMessage());
			return false;
		}
		scriptBlockAPI.scriptRead(getPlayer());
		return true;
	}
}