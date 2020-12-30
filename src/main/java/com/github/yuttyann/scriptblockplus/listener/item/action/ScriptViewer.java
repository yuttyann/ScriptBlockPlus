package com.github.yuttyann.scriptblockplus.listener.item.action;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.listener.item.ChangeSlot;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.listener.item.RunItem;
import com.github.yuttyann.scriptblockplus.listener.item.action.task.GlowTask;
import com.github.yuttyann.scriptblockplus.listener.item.action.task.LookTask;
import com.github.yuttyann.scriptblockplus.listener.item.action.task.ParticleTask;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * ScriptBlockPlus ScriptViewer クラス
 * 
 * @author yuttyann44581
 */
public class ScriptViewer extends ItemAction {

    public static final Set<SBPlayer> PLAYERS = new HashSet<>();

    static {
        if (ProtocolLib.INSTANCE.has()) {
            new LookTask().runTaskTimer(ScriptBlock.getInstance(), 0L, 2L);
            new GlowTask().runTaskTimer(ScriptBlock.getInstance(), 0L, 20L);
        } else {
            new ParticleTask().runTaskTimer(ScriptBlock.getInstance(), 0L, 10L);
        }
    }

    public ScriptViewer() {
        super(ItemUtils.getScriptViewer());
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_SCRIPT_VIEWER.has(permissible);
    }

    @Override
    public void slot(@NotNull ChangeSlot changeSlot) {

    }

    @Override
    public void run(@NotNull RunItem runItem) {
        var sbPlayer = SBPlayer.fromPlayer(runItem.getPlayer());
        switch (runItem.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                PLAYERS.add(sbPlayer);
                SBConfig.SCRIPT_VIEWER_START.send(sbPlayer);
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                PLAYERS.remove(sbPlayer);
                StreamUtils.ifAction(ProtocolLib.INSTANCE.has(), () -> ProtocolLib.GLOW_ENTITY.destroyAll(sbPlayer));
                SBConfig.SCRIPT_VIEWER_STOP.send(sbPlayer);
                break;
            default:
        }
    }
}