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
package com.github.yuttyann.scriptblockplus.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.base.Predicates;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Backup クラス
 * @author yuttyann44581
 */
public class Backup extends SimpleFileVisitor<Path> {

    private final Path target;
    private final Path source;
    private final Predicate<String> filter;
 
    /**
     * コンストラクタ
     * @param backup - バックアップフォルダ
     */
    public Backup(@NotNull File backup) {
        this(backup, Predicates.alwaysTrue());
    }

    /**
     * コンストラクタ
     * @param backup - バックアップフォルダ
     * @param filter - ファイルフィルター
     */
    public Backup(@NotNull File backup, @NotNull Predicate<String> filter) {
        this.target = new File(backup, Utils.getFormatTime("yyyy-MM-dd HH-mm-ss")).toPath();
        this.source = backup.getParentFile().toPath();
        this.filter = filter;
    }

    /**
     * バックアップの保存場所を取得します。
     * @return {@link Path} - バックアップの保存場所
     */
    @NotNull
    public Path getTarget() {
        return target;
    }

    /**
     * バックアップフォルダの保存場所を取得します。
     * @return {@link Path} - バックアップフォルダの保存場所
     */
    @NotNull
    public Path getSource() {
        return source;
    }

    @Override
    @NotNull
    public FileVisitResult visitFile(@NotNull Path path, @NotNull BasicFileAttributes attributes) throws IOException {
        if (!filter.test(path.toString())) {
            var targetFile = target.resolve(source.relativize(path));
            var parentDir = targetFile.getParent();
            Files.createDirectories(parentDir);
            Files.copy(path, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return FileVisitResult.CONTINUE;
    }
}