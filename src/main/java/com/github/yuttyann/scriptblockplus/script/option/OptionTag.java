package com.github.yuttyann.scriptblockplus.script.option;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface OptionTag {

    @NotNull
    String name();

    @NotNull
    String syntax();
}