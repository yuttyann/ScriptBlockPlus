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
package com.github.yuttyann.scriptblockplus.hook.protocol;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

/**
 * ScriptBlockPlus EntityCount クラス
 * <p>
 * エンティティのIDを生成します。
 * @author yuttyann44581
 */
public final class EntityCount {

    private static final Field ENTITY_COUNT;

    static {
        Field field = null;
        try {
            field = PackageType.NMS.getField(true, "Entity", "entityCount");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        } finally {
            ENTITY_COUNT = field;
        }
    }

    public static synchronized int next() {
        try {
            if (Utils.isCBXXXorLater("1.14")) {
                return ((AtomicInteger) ENTITY_COUNT.get(null)).incrementAndGet();
            }
            int nextId = ENTITY_COUNT.getInt(null) + 1;
            ENTITY_COUNT.set(null, nextId);
            return nextId;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return 0;
    }
}