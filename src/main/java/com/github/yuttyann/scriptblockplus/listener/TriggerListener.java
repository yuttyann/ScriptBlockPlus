package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.TriggerEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus TriggerListener クラス
 * @param <E> イベントの型
 * @author yuttyann44581
 */
public abstract class TriggerListener<E extends Event> implements Listener {

    private final Plugin plugin;
    private final ScriptType scriptType;

    public TriggerListener(@NotNull Plugin plugin, @NotNull ScriptType scriptType) {
        this.plugin = plugin;
        this.scriptType = scriptType;
    }

    public static final <T extends TriggerListener<? extends Event>> void register(T listener) {
        Bukkit.getPluginManager().registerEvents(listener, listener.getPlugin());
    }
    
    @NotNull
    public final Plugin getPlugin() {
        return plugin;
    }

    @NotNull
    public final ScriptType getScriptType() {
        return scriptType;
    }

    @Nullable
    public TriggerEvent getTriggerEvent(@NotNull Trigger trigger) {
        return new TriggerEvent(trigger.getPlayer(), trigger.getBlock(), getScriptType());
    }

    @Nullable
    public abstract Trigger createTrigger(@NotNull E event);

    protected final class Trigger {

        private final Player player;
        private final Block block;
        private final E event;

        public Trigger(@NotNull Player player, @NotNull Block block, @NotNull E event) {
            this.player = player;
            this.block = block;
            this.event = event;
            handle(event);
        }

        @NotNull
        public Player getPlayer() {
            return player;
        }

        @NotNull
        public Block getBlock() {
            return block;
        }

        @NotNull
        public E getEvent() {
            return event;
        }

        public void handle(@NotNull E event) {
            var location = getBlock().getLocation();
            if (BlockScriptJson.has(location, scriptType)) {
                var triggerEvent = getTriggerEvent(this);
                if (triggerEvent == null) {
                    return;
                }
                Bukkit.getPluginManager().callEvent(triggerEvent);
                if (triggerEvent.isCancelled()) {
                    return;
                }
                if (!Permission.has(player, scriptType, false)) {
                    SBConfig.NOT_PERMISSION.send(player);
                    return;
                }
                new ScriptRead(player, location, scriptType).read(0);
            }
        }
    }
}