package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.reflection.ClassType;
import com.github.yuttyann.scriptblockplus.file.json.FieldExclusion;
import com.github.yuttyann.scriptblockplus.file.json.annotation.Exclude;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonOptions;
import com.google.common.base.Charsets;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus Json クラス
 * @param <T> 値の型
 * @author yuttyann44581
 */
public abstract class Json<T> {

    @Exclude
    private static final Map<Integer, List<?>> LIST_CACHE = new HashMap<>();

    @Exclude
    private final File file;

    @Exclude
    private final Class<?>[] classes;

    @Exclude
    private final JsonOptions jsonOptions;

    @SerializedName("id")
    private final String id;

    @SerializedName(value = "elements", alternate = { "infos" })
    private List<T> list = new ArrayList<>();

    {
        this.jsonOptions = getClass().getAnnotation(JsonOptions.class);
    }

    public Json(@NotNull UUID uuid) {
        this(uuid.toString());
    }

    @SuppressWarnings("unchecked")
    public Json(@NotNull String id) {
        this.id = id;
        this.file = getFile(jsonOptions);
        this.classes = jsonOptions.classes();

        int hash = hashCode();
        if (LIST_CACHE.containsKey(hash)) {
            this.list = (List<T>) LIST_CACHE.get(hash);
        } else {
            try {
                Optional.ofNullable((Json<T>) loadFile()).ifPresent(j -> list.addAll(j.list));
            } catch (IOException e) {
                e.printStackTrace();
            }
            LIST_CACHE.put(hash, list);
        }
    }

    public static void clear() {
        LIST_CACHE.clear();
    }

    @NotNull
    protected String getId() {
        return id;
    }

    @NotNull
    protected File getFile() {
        return file;
    }

    @Nullable
    private Json<?> loadFile() throws IOException {
        if (!file.exists()) {
            return null;
        }
        var parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (var reader = new JsonReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8))) {
            return new GsonBuilder().setExclusionStrategies(new FieldExclusion()).create().fromJson(reader, getClass());
        }
    }

    public void saveFile() {
        var parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try {
            try (var writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8))) {
                writer.setIndent("  ");
                new GsonBuilder().setExclusionStrategies(new FieldExclusion()).create().toJson(this, getClass(), writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile() {
        try {
            LIST_CACHE.remove(hashCode());
        } finally {
            file.delete();
        }
    }

    @NotNull
    public final T load() {
        if (classes.length > 0) {
            throw new IllegalArgumentException("Please specify the parameter " + Arrays.toString(classes));
        }
        if (list.isEmpty()) {
            list.add(newInstance(ArrayUtils.EMPTY_OBJECT_ARRAY));
        }
        return list.get(0);
    }

    @NotNull
    public final T load(@NotNull Object... args) {
        if (!equalParams(args)) {
            String equal = Arrays.toString(ClassType.getReference(args)) + " != " + Arrays.toString(classes);
            throw new IllegalArgumentException("Classes do not match " + equal);
        }
        int hash = hashCode(args);
        var value = list.stream().filter(t -> t.hashCode() == hash).findFirst();
        if (!value.isPresent()) {
            T instance = newInstance(args);
            list.add(instance);
            return instance;
        }
        return value.get();
    }

    public final boolean exists() {
        return file.exists();
    }

    public final boolean has() {
        if (classes.length > 0) {
            throw new IllegalArgumentException("Please specify the parameter " + Arrays.toString(classes));
        }
        return !list.isEmpty();
    }

    public final boolean has(@NotNull Object... args) {
        if (!equalParams(args)) {
            String equal = Arrays.toString(ClassType.getReference(args)) + " != " + Arrays.toString(classes);
            throw new IllegalArgumentException("Classes do not match " + equal);
        }
        int hash = hashCode(args);
        return list.stream().filter(t -> t.hashCode() == hash).findFirst().isPresent();
    }

    protected int hashCode(@NotNull Object[] args) {
        return Objects.hash(args);
    }

    @NotNull
    protected abstract T newInstance(@NotNull Object[] args);

    public final void action(@NotNull Consumer<T> action, @NotNull Object... args) {
        action.accept(args.length == 0 ? load() : load(args));
        saveFile();
    }

    public final void remove(@NotNull T value) {
        int hash = value.hashCode();
        if (list.removeIf(t -> t.hashCode() == hash)) {
            saveFile();
        }
    }

    private boolean equalParams(@NotNull Object[] args) {
        if (args.length != classes.length) {
            return false;
        }
        for (int i = 0; i < args.length; i++) {
            if (!classes[i].isAssignableFrom(args[i].getClass())) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    private File getFile(@NotNull JsonOptions jsonOptions) {
        var path = jsonOptions.path() + SBFiles.S + jsonOptions.file();
        path = StringUtils.replace(path, "/", SBFiles.S);
        path = StringUtils.replace(path, "{id}", id);
        return new File(ScriptBlock.getInstance().getDataFolder(), path);
    }

    @NotNull
    public static String[] getNames(@NotNull Class<? extends Json<?>> jsonClass) {
        var jsonOptions = jsonClass.getAnnotation(JsonOptions.class);
        var folder = new File(ScriptBlock.getInstance().getDataFolder(), jsonOptions.path());
        if (folder.exists()) {
            var files = folder.list();
            var names = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                var fileName = files[i];
                var extension = fileName.lastIndexOf('.');
                if (extension > 0) {
                    names[i] = fileName.substring(0, extension);
                }
            }
            return names;
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Json)) {
            return false;
        }
        var json = (Json<?>) obj;
        return Objects.equals(id, json.id) && Objects.equals(list, json.list);
    }
}