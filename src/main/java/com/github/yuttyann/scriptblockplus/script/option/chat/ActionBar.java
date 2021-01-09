package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

/**
 * ScriptBlockPlus ActionBar オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "actionbar", syntax = "@actionbar:")
public class ActionBar extends BaseOption implements Runnable {

    private int tick, stay;
    private String message;
    private BukkitTask task;

    @Override
    @NotNull
    public Option newInstance() {
        return new ActionBar();
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), '/');
        this.message = StringUtils.setColor(array[0]);
        if (array.length > 1) {
            this.stay = Integer.parseInt(array[1]);
            this.task = Bukkit.getScheduler().runTaskTimer(ScriptBlock.getInstance(), this, 0, 20);
        } else {
            send(getSBPlayer(), message);
        }
        return true;
    }

    @Override
    public void run() {
        try {
            if (!getSBPlayer().isOnline() || tick++ >= stay) {
                task.cancel();
            }
            send(getSBPlayer(), task.isCancelled() ? "" : message);
        } catch (Exception e) {
            task.cancel();
        }
    }

    public static void send(@NotNull SBPlayer sbPlayer, @NotNull String message) {
        if (Utils.isCBXXXorLater("1.12.2")) {
            var command = "title " + sbPlayer.getName() + " actionbar {\"text\":\"" + message + "\"}";
            Utils.tempPerm(sbPlayer, Permission.MINECRAFT_COMMAND_TITLE, () -> Utils.dispatchCommand(sbPlayer, command));
        } else if (ProtocolLib.INSTANCE.has()) {
            try {
                ProtocolLib.INSTANCE.sendActionBar(sbPlayer.getPlayer(), message);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (PackageType.HAS_NMS) {
            try {
                PackageType.sendActionBar(sbPlayer.getPlayer(), message);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
}