package com.github.yuttyann.scriptblockplus.hook.protocol;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

/**
 * ScriptBlockPlus EntityCount クラス
 * @author yuttyann44581
 */
public class EntityCount {

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
            int entityId = ENTITY_COUNT.getInt(null);
            ENTITY_COUNT.set(null, ++entityId);
            return entityId;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return 0;
    }
}