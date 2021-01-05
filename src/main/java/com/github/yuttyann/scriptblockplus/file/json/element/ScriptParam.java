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