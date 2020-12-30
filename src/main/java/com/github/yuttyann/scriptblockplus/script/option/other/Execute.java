package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
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
        var array = StringUtils.split(getOptionValue(), "/");
        var coords = StringUtils.split(array[1], ",");
        int x = Integer.parseInt(coords[1]);
        int y = Integer.parseInt(coords[2]);
        int z = Integer.parseInt(coords[3]);
        var world = Utils.getWorld(coords[0]);
        var location = new Location(world, x, y, z);
        var scriptType = ScriptType.valueOf(array[0].toUpperCase());
        return ScriptBlock.getInstance().getAPI().read(getPlayer(), location, scriptType, 0);
    }
}