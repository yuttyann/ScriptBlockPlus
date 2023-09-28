/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.hook.plugin.VaultEconomy;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ScriptBlockPlus Calculation オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "if", syntax = "@if ", description = "<value1> <operator> <value2> [failure]")
public final class IfAction extends BaseOption {

    public static final Pattern REALNUMBER_PATTERN = Pattern.compile("^-?(0|[1-9]\\d*)(\\.\\d+|)$");

    @Override
    protected Result isValid() throws Exception {
        var space = split(getOptionValue(), ' ', true);
        var player = getSBPlayer().toPlayer();
        var value1 = parse(player, space.get(0));
        var value2 = parse(player, space.get(2));
        var operator = space.get(1);

        if (result(operator, value1, value2)) {
            return Result.SUCCESS;
        }
        if (space.size() > 3) {
            var result = StringUtils.createString(space, 3);
            result = StringUtils.replace(result, "%value1%", value1);
            result = StringUtils.replace(result, "%value2%", value2);
            Utils.sendColorMessage(player, result);
        }
        return Result.FAILURE;
    }

    @SuppressWarnings("deprecation")
    @NotNull
    private Object parse(@NotNull Player player, @NotNull String source) throws Exception {
        if (REALNUMBER_PATTERN.matcher(source).matches()) {
            return Double.parseDouble(source);
        }
        if (source.startsWith("%player_count_") && source.endsWith("%")) {
            source = source.substring("%player_count_".length(), source.length() - 1);
            var slash = split(source, '/', false);
            if (slash.size() < 1 || slash.size() > 2) {
                return 0;
            }
            var scriptKey = slash.size() == 1 ? getScriptKey() : ScriptKey.valueOf(slash.get(0));
            var blockCoords = BlockCoords.fromString(slash.size() == 1 ? slash.get(0) : slash.get(1));
            return PlayerCountJson.get(getUniqueId()).load(scriptKey, blockCoords).getAmount();
        }
        if (source.startsWith("%player_others_in_range_") && source.endsWith("%")) {
            source = source.substring("%player_others_in_range_".length(), source.length() - 1);
            var world = player.getLocation().getWorld();
            int count = 0, result = Integer.parseInt(source) * Integer.parseInt(source);
            for (var worldPlayer : world.getPlayers()) {
                if (player != worldPlayer && player.getLocation().distanceSquared(worldPlayer.getLocation()) <= result) {
                    count++;
                }
            }
            return count;
        }
        if (source.startsWith("%player_ping_") && source.endsWith("%")) {
            if (!NetMinecraft.hasNMS()) {
                return 0;
            }
            source = source.substring("%player_ping_".length(), source.length() - 1);
            var target = Bukkit.getPlayer(source);
            return target == null ? 0 : NetMinecraft.hasNMS() ? NMSHelper.getPing(target) : 0;
        }
        if (source.startsWith("%server_online_") && source.endsWith("%")) {
            source = source.substring("%server_online_".length(), source.length() - 1);
            return Utils.getWorld(source).getPlayers().size();
        }
        if (source.startsWith("%objective_score_") && source.endsWith("%")) {
            source = source.substring("%objective_score_".length(), source.length() - 1);
            var objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(source);
            return objective == null ? 0 : objective.getScore(player.getName()).getScore();
        }
        switch (source) {
            case "%server_online%":
                return Bukkit.getOnlinePlayers().size();
            case "%server_offline%":
                return Bukkit.getOfflinePlayers().length;
            case "%player_count%":
                return PlayerCountJson.get(getUniqueId()).load(getScriptKey(), getBlockCoords()).getAmount();
            case "%player_ping%":
                return NetMinecraft.hasNMS() ? NMSHelper.getPing(player) : 0;
            case "%player_x%":
                return player.getLocation().getBlockX();
            case "%player_y%":
                return player.getLocation().getBlockY();
            case "%player_z%":
                return player.getLocation().getBlockZ();
            case "%player_bed_x%":
                return player.getBedSpawnLocation() == null ? 0 : player.getBedSpawnLocation().getBlockX();
            case "%player_bed_y%":
                return player.getBedSpawnLocation() == null ? 0 : player.getBedSpawnLocation().getBlockY();
            case "%player_bed_z%":
                return player.getBedSpawnLocation() == null ? 0 : player.getBedSpawnLocation().getBlockZ();
            case "%player_compass_x%":
                return player.getCompassTarget().getBlockX();
            case "%player_compass_y%":
                return player.getCompassTarget().getBlockY();
            case "%player_compass_z%":
                return player.getCompassTarget().getBlockZ();
            case "%player_gamemode%":
                return player.getGameMode().getValue();
            case "%player_world_time%":
                return player.getWorld().getTime();
            case "%player_exp%":
                return player.getExp();
            case "%player_exp_to_level%":
                return player.getExpToLevel();
            case "%player_level%":
                return player.getLevel();
            case "%player_fly_speed%":
                return player.getFlySpeed();
            case "%player_food_level%":
                return player.getFoodLevel();
            case "%player_health%":
                return player.getHealth();
            case "%player_health_scale%":
                return player.getHealthScale();
            case "%player_last_damage%":
                return player.getLastDamage();
            case "%player_max_health%":
                return player.getMaxHealth();
            case "%player_max_air%":
                return player.getMaximumAir();
            case "%player_max_no_damage_ticks%":
                return player.getMaximumNoDamageTicks();
            case "%player_no_damage_ticks%":
                return player.getNoDamageTicks();
            case "%player_time%":
                return player.getPlayerTime();
            case "%player_time_offset%":
                return player.getPlayerTimeOffset();
            case "%player_remaining_air%":
                return player.getRemainingAir();
            case "%player_saturation%":
                return player.getSaturation();
            case "%player_sleep_ticks%":
                return player.getSleepTicks();
            case "%player_ticks_lived%":
                return player.getTicksLived();
            case "%player_seconds_lived%":
                return player.getTicksLived() * 20;
            case "%player_minutes_lived%":
                return (player.getTicksLived() * 20) / 60;
            case "%player_total_exp%":
                return player.getTotalExperience();
            case "%player_walk_speed%":
                return player.getWalkSpeed();
            case "%vault_eco_balance%":
                return VaultEconomy.INSTANCE.isEnabled() ? VaultEconomy.INSTANCE.getBalance(player) : 0;
            default:
                return source;
        }
    }

    private boolean result(@NotNull String operator, @NotNull Object value1, @NotNull Object value2) {
        if (!(value1 instanceof Number && value2 instanceof Number)) {
            switch (operator) {
                case "==":
                    return Objects.equals(value1, value2);
                case "!=":
                    return !Objects.equals(value1, value2);
                default:
                    return false;
            }
        }
        double v1 = Double.parseDouble(value1.toString());
        double v2 = Double.parseDouble(value2.toString());
        switch (operator) {
        case "<":
            return v1 < v2;
        case "<=":
            return v1 <= v2;
        case ">":
            return v1 > v2;
        case ">=":
            return v1 >= v2;
        case "==":
            return v1 == v2;
        case "!=":
            return v1 != v2;
        default:
            return false;
        }
    }
}