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
package com.github.yuttyann.scriptblockplus.file.json.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.basic.OneJson.OneElement;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus BlockScript クラス
 * @author yuttyann44581
 */
public final class BlockScript extends OneElement<BlockCoords> {

    @SerializedName("blockcoords")
    private final BlockCoords blockCoords;

    @SerializedName("author")
    private Set<UUID> author;

    @SerializedName("script")
    private List<String> script;

    @SerializedName("lastedit")
    private String lastedit;

    @SerializedName("selector")
    private String selector;

    @SerializedName("amount")
    private int amount = -1;

    public BlockScript(@NotNull BlockCoords blockCoords) {
        this.blockCoords = Objects.requireNonNull(blockCoords);
    }

    @Override
    @NotNull
    protected BlockCoords getA() {
        return blockCoords;
    }

    @Override
    public boolean isElement(@NotNull BlockCoords blockCoords) {
        return this.blockCoords.compare(blockCoords);
    }

    public void setAuthors(@NotNull Collection<UUID> authors) {
        this.author = new LinkedHashSet<>(authors);
    }

    @NotNull
    public Set<UUID> getAuthors() {
        return author == null ? this.author = new LinkedHashSet<>() : author;
    }

    public void setScripts(@NotNull Collection<String> scripts) {
        this.script = new ArrayList<>(scripts);
    }

    @NotNull
    public List<String> getScripts() {
        return script == null ? this.script = new ArrayList<>() : script;
    }

    public void setLastEdit(@NotNull String time) {
        this.lastedit = time;
    }

    @Nullable
    public String getLastEdit() {
        return lastedit;
    }

    public void setSelector(@Nullable String selector) {
        this.selector = selector;
    }

    @Nullable
    public String getSelector() {
        return selector;
    }

    public synchronized void setAmount(int amount) {
        this.amount = amount;
    }

    public synchronized void addAmount(int amount) {
        this.amount += amount;
    }

    public synchronized void subtractAmount(int amount) {
        this.amount = Math.max(this.amount - amount, 0);
    }

    public synchronized int getAmount() {
        return amount;
    }
}