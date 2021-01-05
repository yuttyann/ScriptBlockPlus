package com.github.yuttyann.scriptblockplus.listener.trigger;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.listener.TriggerListener;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * ScriptBlockPlus HitTrigger クラス
 * @author yuttyann44581
 */
public class HitTrigger extends TriggerListener<ProjectileHitEvent> {

    private static final String KEY_HIT = Utils.randomUUID();

    public HitTrigger(@NotNull ScriptBlock plugin) {
        super(plugin, ScriptKey.HIT, EventPriority.HIGH);
    }

    @Override
    @Nullable
    public Trigger create(@NotNull ProjectileHitEvent event) {
        var block = getHitBlock(event);
        var shooter = event.getEntity().getShooter();
        if (block == null || !(shooter instanceof Player)) {
            return null;
        }
        return new Trigger((Player) shooter, block, event);
    }

    @Override
    @NotNull
    protected Result handle(@NotNull Trigger trigger) {
        switch (trigger.getProgress()) {
            case EVENT:
                var objectMap = SBPlayer.fromPlayer(trigger.getPlayer()).getObjectMap();
                int oldEntityId = objectMap.has(KEY_HIT) ? objectMap.getInt(KEY_HIT) : -1;
                int nowEntityId = trigger.getEvent().getEntity().getEntityId();
                if (oldEntityId == -1) {
                    objectMap.put(KEY_HIT, nowEntityId);
                    return Result.SUCCESS;
                }
                objectMap.remove(KEY_HIT);
                return oldEntityId == nowEntityId ? Result.FAILURE : Result.SUCCESS;
            default:
                return super.handle(trigger);
        }
    }

    @Nullable
    private Block getHitBlock(@NotNull ProjectileHitEvent event) {
        Predicate<Method> isGetHitBlock = m -> m.getName().equals("getHitBlock");
        if (Stream.of(event.getClass().getMethods()).anyMatch(isGetHitBlock)) {
            return event.getHitBlock();
        }
        var start = event.getEntity().getLocation().toVector();
        var direction = event.getEntity().getVelocity().normalize();
        var iterator = new BlockIterator(event.getEntity().getWorld(), start, direction, 0.0D, 4);
        var hitBlock = (Block) null;
        while (iterator.hasNext()) {
            hitBlock = iterator.next();
            if (hitBlock.getType() != Material.AIR) {
                break;
            }
        }
        return hitBlock;
    }
}