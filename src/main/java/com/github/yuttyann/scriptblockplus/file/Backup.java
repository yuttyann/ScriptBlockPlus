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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Predicates;

/**
 * FBS-Common Backup
 * @author yuttyann44581
 */
public class Backup extends SimpleFileVisitor<Path> {

    public static final SimpleDateFormat SHORT_DATE_FORMAT;

    static {
        SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        // SHORT_DATE_FORMAT.setTimeZone(Utils.DATE_FORMAT.getTimeZone());
    }

    private final Path from, to;
    private final Predicate<String> filter;

    public Backup(@NotNull SBFile backup) {
        this(backup, Predicates.alwaysTrue());
    }

    public Backup(@NotNull SBFile backup, @NotNull Predicate<String> filter) {
        var name = SHORT_DATE_FORMAT.format(new Date());
        var size = backup.listNames((file, path) -> path.startsWith(name)).size() + 1;
        this.from = backup.getParentFile().toPath();
        this.to = new SBFile(backup, name + '-' + size).toPath();
        this.filter = filter;
    }

    @NotNull
    public Path getFrom() {
        return from;
    }

    @NotNull
    public Path getTo() {
        return to;
    }

    @Override
    @NotNull
    public FileVisitResult visitFile(@NotNull Path path, @NotNull BasicFileAttributes attributes) throws IOException {
        if (!filter.test(path.toString())) {
            var targetFile = to.resolve(from.relativize(path));
            var parentDir = targetFile.getParent();
            Files.createDirectories(parentDir);
            Files.copy(path, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return FileVisitResult.CONTINUE;
    }
}