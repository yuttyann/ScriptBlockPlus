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
package com.github.yuttyann.scriptblockplus.file.json.derived.element;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.basic.OneJson.OneElement;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.gson.InstanceCreator;
import com.google.gson.annotations.SerializedName;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus BlockScript クラス
 * @author yuttyann44581
 */
public final class BlockScript extends OneElement<BlockCoords> {

    public static final InstanceCreator<BlockScript> INSTANCE = t -> new BlockScript(BlockCoords.ZERO);

    public static final String SELECTOR = "selector", NAMETAG ="nametag", AMOUNT = "amount";

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

    @SerializedName("nametag")
    private String nameTag;

    @SerializedName("values")
    private Map<String, ValueHolder> values;

    /**
     * コンストラクタ
     * @param blockCoords - 座標
     */
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

    /**
     * ワールドを取得します。
     * @return {@link World} - ワールド
     */
    @NotNull
    public World getWorld() {
        return blockCoords.getWorld();
    }

    /**
     * 座標を取得します。
     * @return {@link BlockCoords} - 座標
     */
    @NotNull
    public BlockCoords getBlockCoords() {
        return BlockCoords.copy(blockCoords);
    }

    /**
     * 作者の一覧を設定します。
     * @param authors - 作者の一覧
     */
    public void setAuthors(@NotNull Collection<UUID> authors) {
        this.author = new LinkedHashSet<>(authors);
    }

    /**
     * 作者の一覧を取得します。
     * @return {@link Set}&lt;{@link UUID}&gt; - 作者の一覧
     */
    @NotNull
    public Set<UUID> getAuthors() {
        return author == null ? this.author = new LinkedHashSet<>() : author;
    }

    /**
     * スクリプトの一覧を設定します。
     * @param authors - スクリプトの一覧
     */
    public void setScripts(@NotNull Collection<String> scripts) {
        this.script = new ArrayList<>(scripts);
    }

    /**
     * スクリプトの一覧を取得します。
     * @return {@link List}&lt;{@link String}&gt; - スクリプトの一覧
     */
    @NotNull
    public List<String> getScripts() {
        return script == null ? this.script = new ArrayList<>() : script;
    }

    /**
     * 編集時刻を設定します。
     * @param date - 時刻
     */
    public void setLastEdit(@NotNull Date date) {
        this.lastedit = Utils.DATE_FORMAT.format(date);
    }

    /**
     * 編集時刻({@code yyyy/MM/dd HH:mm:ss})を設定します。
     * @param lastedit - 時刻
     */
    public void setLastEdit(@NotNull String date) {
        if (StringUtils.isNotEmpty(lastedit)) {
            try {
                setLastEdit(Utils.DATE_FORMAT.parse(date));
            } catch (ParseException e) {
                setLastEdit(new Date());
            }
        }
    }

    /**
     * 編集時刻を取得します。
     * @return {@link Date} - 時刻
     */
    @Nullable
    public Date getLastEdit() {
        if (StringUtils.isNotEmpty(lastedit)) {
            try {
                return Utils.DATE_FORMAT.parse(lastedit);
            } catch (ParseException e) { }
        }
        return null;
    }

    /**
     * 値のデータ構造が存在し、要素がある場合は{@code true}を返します。
     * @return {@code boolean} - 値のデータ構造が存在し、要素がある場合は{@code true}
     */
    public boolean hasValues() {
        return values != null && !values.isEmpty();
    }

    /**
     * 値のデータ構造を設定します。
     * @param values - データ構造
     */
    public void setValues(@Nullable Map<String, ValueHolder> values) {
        this.values = values;
    }

    /**
     * 値のデータ構造を取得します。
     * @return {@link Map}&lt;{@link String}, {@link ValueHolder}&gt; - データ構造
     */
    @NotNull
    public Map<String, ValueHolder> getValues() {
        return values == null ? this.values = new HashMap<>() : values;
    }

    /**
     * スクリプトのデータ構造に任意の値を追加、保存します。
     * <p>
     * 値に{@code null}を指定することで設定を削除することができます。
     * <p>
     * 利用できる値の型については、{@link ValueType}を参照してください。
     * <p>
     * <pre>
     * 以下のような形式で保存されます("values"を参照)
     * {
     *   "author": [
     *     "00000000-0000-0000-0000-000000000000"
     *   ],
     *   "blockcoords": "world,0,0,0",
     *   "lastedit": "0000/00/00 00:00:00",
     *   "script": [
     *     "@command ...."
     *   ],
     *   "values": {
     *     "key": "type:value",
     *     "example1": "integer:10",
     *     "example2": "string:example"
     *   }
     * }
     * </pre>
     * @param key - キー
     * @param value - 値
     * @return {@link Optional}&lt;{@link ValueHolder}&gt; - 保存した値
     */
    @NotNull
    public Optional<ValueHolder> setValue(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            if (!hasValues()) {
                return Optional.empty();
            }
            getValues().remove(key);
            if (getValues().isEmpty()) {
                setValues(null);
            }
            return Optional.empty();
        } else {
            var valueHolder = getValues().get(key);
            if (valueHolder == null) {
                getValues().put(key, valueHolder = new ValueHolder(value));
            } else {
                valueHolder.setValue(value);
            }
            return Optional.of(valueHolder);
        }
    }

    /**
     * 指定したキーの値を取得します。
     * <pre>
     * 例えば、以下のデータの場合は"example"を指定することで"int型"の数値"2525"を取得できます。
     * "values": {
     *   "example": "integer:2525"
     * }
     * </pre>
     * @param key - キー
     * @return {@link Optional}&lt;{@link ValueHolder}&gt; - 値
     */
    @NotNull
    public Optional<ValueHolder> getValue(@NotNull String key) {
        return hasValues() ? Optional.ofNullable(getValues().get(key)) : Optional.empty();
    }

    /**
     * 指定したキーの値を取得します。
     * <p>
     * {@code このメソッドは必ず値を返し、値が見つからない場合は空の値を返します。}
     * <pre>
     * 例えば、以下のデータの場合は"example"を指定することで"int型"の数値"2525"を取得できます。
     * "values": {
     *   "example": "integer:2525"
     * }
     * </pre>
     * @param key - キー
     * @return {@link ValueHolder} - 値
     */
    @NotNull
    public ValueHolder getSafeValue(@NotNull String key) {
        if (hasValues()) {
            var valueHolder = getValues().get(key);
            return valueHolder == null ? ValueHolder.EMPTY : valueHolder;
        }
        return ValueHolder.EMPTY;
    }
}