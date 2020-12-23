package com.github.yuttyann.scriptblockplus.command;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ScriptBlockPlus CommandData クラス
 * @author yuttyann44581
 */
public class CommandData {

    private String message;
    private boolean isPrefix;

    private List<String> permissions = new ArrayList<>();

    public CommandData(@Nullable String message, boolean isPrefix) {
        this(message, isPrefix, (String) null);
    }

    public CommandData(@Nullable String message, @Nullable String... permission) {
        this(message, true, permission);
    }

    public CommandData(@Nullable String message, boolean isPrefix, @Nullable String... permission) {
        setMessage(message);
        setPrefix(isPrefix);
        addPermission(permission);
    }

    @NotNull
    public CommandData setMessage(@Nullable String message) {
        this.message = message;
        return this;
    }

    @NotNull
    public CommandData setPrefix(boolean isPrefix) {
        this.isPrefix = isPrefix;
        return this;
    }

    @NotNull
    public CommandData addPermission(@Nullable String... permission) {
        Optional<String[]> value = Optional.ofNullable(permission);
        if (value.isPresent() && value.get().length > 0) {
            StreamUtils.forEach(value.get(), permissions::add);
        }
        return this;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public List<String> getPermissions() {
        return permissions;
    }

    public boolean isPrefix() {
        return isPrefix;
    }

    public final boolean hasMessage() {
        return StringUtils.isNotEmpty(message);
    }

    public boolean hasPermission(@NotNull CommandSender sender) {
        if (permissions == null || permissions.size() == 0) {
            return true;
        }
        return permissions.stream().anyMatch(s -> Permission.has(sender, s));
    }
}