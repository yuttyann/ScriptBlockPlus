package com.github.yuttyann.scriptblockplus.script.option.other;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Execute extends BaseOption {

	public Execute() {
		super("execute", "@execute:");
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

		Player player = getPlayer();
		ScriptBlockAPI scriptBlockAPI = ScriptBlock.getInstance().getAPI(location, scriptType);
		if (!scriptBlockAPI.checkPath()) {
			Utils.sendMessage(player, SBConfig.getErrorScriptFileCheckMessage());
			return false;
		}
		scriptBlockAPI.scriptRead(player);
		return true;
	}
}