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
package com.github.yuttyann.scriptblockplus.hook.plugin;

import java.util.ArrayList;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
import com.github.yuttyann.scriptblockplus.utils.ArrayUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;

/**
 * ScriptBlockPlus DiscordSRV クラス
 * @author yuttyann44581
 */
public class DiscordSRV extends HookPlugin {

    public static final DiscordSRV INSTANCE = new DiscordSRV();

    private DiscordSRV() { }

    @Override
    @NotNull
    public String getPluginName() {
        return "DiscordSRV";
    }

    /**
     * プラグインが有効な場合にtrueを返します。
     * @return {@code boolean} プラグインが有効な場合は{@code true}
     */
    public boolean isEnabled() {
        return has() && github.scarsz.discordsrv.DiscordSRV.getPlugin().isEnabled();
    }

    /**
     * メンバーを取得します。
     * @param uuid - プレイヤーの{@link UUID}
     * @return {@link Member} - メンバー
     */
    @Nullable
    public Member getMemberById(@NotNull UUID uuid) {
        var discord = github.scarsz.discordsrv.DiscordSRV.getPlugin();
        var linkManager = discord.getAccountLinkManager();
        if (linkManager == null) {
            return null;
        }
        var discordId = linkManager.getDiscordId(uuid);
        return discordId == null ? null : DiscordUtil.getMemberById(discordId);
    }

    /**
     * 接続中のボイスチャンネルIDを取得します。
     * @param uuid - プレイヤーの{@link UUID}
     * @return {@link String} - ボイスチャンネルID
     */
    @Nullable
    public String getVoiceChannelId(@NotNull UUID uuid) {
        var member = getMemberById(uuid);
        if (member == null) {
            return null;
        }
        var channel = member.getVoiceState().getChannel();
        return channel == null ? null : channel.getId();
    }

    /**
     * ユーザーが所持するロールを取得します。
     * @param uuid - プレイヤーの{@link UUID}
     * @return {@link String}[] - ロールIDの配列
     */
    @NotNull
    public String[] getRoles(@NotNull UUID uuid) {
        var member = getMemberById(uuid);
        if (member == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return StreamUtils.toArray(member.getRoles(), r -> r.getId(), String[]::new);
    }

    /**
     * ユーザーが所持する権限を取得します。
     * @param uuid - プレイヤーの{@link UUID}
     * @return {@link Permission}[] - 権限の配列
     */
    @NotNull
    public Permission[] getPermissions(@NotNull UUID uuid) {
        var member = getMemberById(uuid);
        return member == null ? new Permission[0] : member.getPermissions().toArray(Permission[]::new);
    }

    /**
     * ユーザーにロールを追加します。
     * @param uuid - プレイヤーの{@link UUID}
     * @param roleIds - ロールID
     */
    @Nullable
    public void addRoleToMember(@NotNull UUID uuid, @NotNull String... roleIds) {
        var member = getMemberById(uuid);
        if (member == null) {
            return;
        }
        var jda = member.getJDA();
        if (jda == null) {
            return;
        }
        var roles = new ArrayList<Role>(roleIds.length);
        for (var roleId : roleIds) {
            var role = jda.getRoleById(roleId);
            if (role != null) { roles.add(role); }
        }
        if (roles.size() > 0) {
            DiscordUtil.addRolesToMember(member, roles.toArray(Role[]::new));
        }
    }

    /**
     * ユーザーのロールを削除します。
     * @param uuid - プレイヤーの{@link UUID}
     * @param roleIds - ロールID
     */
    @Nullable
    public void removeRoleToMember(@NotNull UUID uuid, @NotNull String... roleIds) {
        var member = getMemberById(uuid);
        if (member == null) {
            return;
        }
        var jda = member.getJDA();
        if (jda == null) {
            return;
        }
        var roles = new ArrayList<Role>(roleIds.length);
        for (var roleId : roleIds) {
            var role = jda.getRoleById(roleId);
            if (role != null) { roles.add(role); }
        }
        if (roles.size() > 0) {
            DiscordUtil.removeRolesFromMember(member, roles.toArray(Role[]::new));
        }
    }
}