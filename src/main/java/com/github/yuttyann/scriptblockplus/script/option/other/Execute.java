package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Execute オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "execute", syntax = "@execute:")
public class Execute extends BaseOption {

	@Override
	@NotNull
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
		return ScriptBlock.getInstance().getAPI().scriptRead(getPlayer(), location, scriptType, 0);
	}
}