package com.github.yuttyann.scriptblockplus.file.json.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gson.annotations.SerializedName;

/**
 * ScriptBlockPlus LegacyName 注釈
 * <p>
 * Gsonのバージョンが古い場合に呼び出される。
 * <p>
 * {@link SerializedName#alternate()}と同等の機能を持っている。
 * @author yuttyann44581
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface LegacyName {

    /**
    * @return the alternative names of the field when it is deserialized.
    */
    String[] alternate() default {};
}