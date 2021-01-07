package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
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

    private static final ScriptBlockAPI API = ScriptBlock.getInstance().getAPI();

    @Override
    @NotNull
    public Option newInstance() {
        return new Execute();
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), '/');
        var wxyz = StringUtils.split(array[1], ',');
        var scriptKey = ScriptKey.valueOf(array[0].toUpperCase());
        int x = Integer.parseInt(wxyz[1]), y = Integer.parseInt(wxyz[2]), z = Integer.parseInt(wxyz[3]);
        return API.read(getPlayer(), new Location(Utils.getWorld(wxyz[0]), x, y, z), scriptKey, 0);
    }
}