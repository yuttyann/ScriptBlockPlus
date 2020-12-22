package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.reflection.ClassType;
import com.github.yuttyann.scriptblockplus.file.json.FieldExclusion;
import com.github.yuttyann.scriptblockplus.file.json.annotation.Exclude;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonOptions;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.google.common.base.Charsets;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
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
    protected final File file;

    @Exclude
    private final Class<?>[] classes;

    @SerializedName("id")
    @Expose
    protected final String id;

    @SerializedName("elements")
    @Expose
    protected final List<T> list = new ArrayList<>();

    @SerializedName("infos")
    @Expose(serialize = false)
    protected List<T> oldList = null;

    public Json(@NotNull UUID uuid) {
        this(uuid.toString());
    }

    public Json(@NotNull String id) {
        // ファイル名等に使う
        this.id = id;

        // アノテーションからパラメータを取得
        JsonOptions jsonOptions = getClass().getAnnotation(JsonOptions.class);
        this.file = getFile(jsonOptions);
        this.classes = jsonOptions.classes();

        // JSONをデシリアライズ
        try {
            @SuppressWarnings("unchecked")
            Optional<Json<T>> json = Optional.ofNullable((Json<T>) loadFile());
            json.ifPresent(j -> list.addAll(Optional.ofNullable(j.oldList).orElseGet(() -> j.list)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final boolean exists() {
        return file.exists();
    }

    @Nullable
    private Json<?> loadFile() throws IOException {
        if (!file.exists()) {
            return null;
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8))) {
            GsonBuilder builder = new GsonBuilder().setExclusionStrategies(new FieldExclusion());
            return builder.create().fromJson(reader, getClass());
        }
    }

    public void saveFile() {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try {
            try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8))) {
                writer.setIndent("  ");

                GsonBuilder builder = new GsonBuilder().setExclusionStrategies(new FieldExclusion());
                builder.create().toJson(this, getClass(), writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        Optional<T> value = list.stream().filter(t -> t.hashCode() == hash).findFirst();
        if (!value.isPresent()) {
            T instance = newInstance(args);
            list.add(instance);
            return instance;
        }
        return value.get();
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
        list.removeIf(t -> t.hashCode() == hash);
        saveFile();
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
        String path = jsonOptions.path() + SBFiles.S + jsonOptions.file();
        path = StringUtils.replace(path, "/", SBFiles.S);
        path = StringUtils.replace(path, "{id}", id);
        return new File(ScriptBlock.getInstance().getDataFolder(), path);
    }

    @NotNull
    public static List<String> getNameList(@NotNull Class<? extends Json<?>> jsonClass) {
        JsonOptions jsonOptions = jsonClass.getAnnotation(JsonOptions.class);
        File folder = new File(ScriptBlock.getInstance().getDataFolder(), jsonOptions.path());
        List<String> list = new ArrayList<>();
        if (folder.exists()) {
            int length = ".json".length();
            StreamUtils.forEach(folder.list(), s -> list.add(s.substring(0, s.length() - length)));
        }
        return list;
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
        Json<?> json = (Json<?>) obj;
        return Objects.equals(id, json.id) && Objects.equals(list, json.list);
    }
}