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

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * ScriptBlockPlus ScriptParam クラス
 * @author yuttyann44581
 */
public class ScriptParam {

    @SerializedName("author")
    private Set<UUID> author = new LinkedHashSet<>();

    @SerializedName("script")
    private List<String> script = new LinkedList<>();

    @SerializedName("lastedit")
    private String lastedit;

    @SerializedName("amount")
    private int amount = -1;

    @NotNull
    public Set<UUID> getAuthor() {
        return author;
    }

    public void setAuthor(@NotNull Set<UUID> author) {
        this.author = author;
    }

    public List<String> getScript() {
        return script;
    }

    public void setScript(@NotNull List<String> script) {
        this.script = script;
    }

    @NotNull
    public String getLastEdit() {
        return lastedit;
    }

    public void setLastEdit(@NotNull String time) {
        this.lastedit = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    public void subtractAmount(int amount) {
        this.amount = Math.max(this.amount - amount, 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, script, lastedit);
    }
}