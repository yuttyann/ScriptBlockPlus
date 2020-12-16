package com.github.yuttyann.scriptblockplus.file.json.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * ScriptBlockPlus JsonOptions 注釈
 * @author yuttyann44581
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface JsonOptions {

    @NotNull
    String path();

    @NotNull
    String file();

    @NotNull
    Class<?>[] classes() default { };
}