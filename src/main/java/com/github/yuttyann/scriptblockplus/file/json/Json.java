package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBFiles;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
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
import java.util.function.Predicate;

public abstract class Json<T> {

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
        this.id = id;
        try {
            Optional<Json<T>> json = Optional.ofNullable((Json<T>) loadFile());
            json.ifPresent(j -> list.addAll(Optional.ofNullable(j.oldList).orElseGet(() -> j.list)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private Json loadFile() throws IOException {
        File file = getFile();
        if (!file.exists()) {
            return null;
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8))) {
            return new Gson().fromJson(reader, getClass());
        }
    }

    public void saveFile() {
        File file = getFile();
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try {
            try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8))) {
                writer.setIndent("  ");
                new Gson().toJson(this, getClass(), writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public T load() {
        if (list.size() == 0) {
            list.add(newInstance(ArrayUtils.EMPTY_OBJECT_ARRAY));
        }
        return list.get(0);
    }

    @NotNull
    public T load(@NotNull Object... args) {
        int paramHash = hashCode(args);
        Predicate<T> equal = t -> t.hashCode() == paramHash;
        Optional<T> value = list.stream().filter(equal).findFirst();
        if (!value.isPresent()) {
            list.add((value = Optional.of(newInstance(args))).get());
        }
        return value.get();
    }

    protected int hashCode(@NotNull Object... args) {
        return Objects.hash(args);
    }

    @NotNull
    protected abstract T newInstance(@NotNull Object... args);

    public void action(@NotNull Consumer<T> action, @NotNull Object... args) {
        action.accept(args.length == 0 ? load() : load(args));
        saveFile();
    }

    public void remove(@NotNull T value) {
        list.remove(value);
        saveFile();
    }

    public boolean exists() {
        return getFile().exists();
    }

    private File getFile() {
        return new File(ScriptBlock.getInstance().getDataFolder(), getPath());
    }

    @NotNull
    private String getPath() {
        JsonDirectory jsonDirectory = getClass().getAnnotation(JsonDirectory.class);
        String path = jsonDirectory.path() + SBFiles.S + jsonDirectory.file();
        path = StringUtils.replace(path, "/", SBFiles.S);
        path = StringUtils.replace(path, "{id}", id);
        return path;
    }

    @NotNull
    public static List<String> getIdList(@NotNull Class<? extends Json> jsonClass) {
        JsonDirectory jsonDirectory = jsonClass.getAnnotation(JsonDirectory.class);
        File folder = new File(ScriptBlock.getInstance().getDataFolder(), jsonDirectory.path());
        List<String> list = new ArrayList<>();
        if (folder.exists()) {
            int length = ".json".length();
            StreamUtils.forEach(folder.list(), s -> list.add(s.substring(0, s.length() - length)));
        }
        return list;
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Json)) {
            return false;
        }
        Json json = (Json) obj;
        return Objects.equals(id, json.id) && Objects.equals(list, json.list);
    }
}