package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus Json クラス
 * @author yuttyann44581
 */
public abstract class Json<T> {

    private static final String THREAD_NAME = "Json Thread : " + Utils.getPluginName(ScriptBlock.getInstance());

    @SerializedName("uuid")
    @Expose
    protected final UUID uuid;

    @SerializedName("infos")
    @Expose
    protected List<T> list = null;

    protected Json(@NotNull UUID uuid) {
        this.uuid = uuid;
        try {
            Json<T> json = (Json<T>) load(uuid);
            this.list = json == null ? new ArrayList<>() : json.list;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
        File file = getFile(uuid);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, Charsets.UTF_8))
        ) {
            writer.write(new Gson().toJson(this));
        }
    }

    protected void action(@Nullable T t, @NotNull Consumer<T> action) {
        Thread thread = new Thread(() -> {
            try {
                action.accept(t);
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            thread.setName(THREAD_NAME);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void remove(@NotNull T t) {
        Thread thread = new Thread(() -> {
            try {
                list.remove(t);
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            thread.setName(THREAD_NAME);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private Json load(@NotNull UUID uuid) throws IOException {
        File file = getFile(uuid);
        if (!file.exists()) {
            return null;
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] data = new byte[(int) file.length()];
            bis.read(data);
            return new Gson().fromJson(new String(data, Charsets.UTF_8), getClass());
        }
    }

    @NotNull
    public final File getFile(@NotNull UUID uuid) {
        String path = "json/" + getClass().getSimpleName().toLowerCase() + "/" + uuid.toString() + ".json";
        return new File(ScriptBlock.getInstance().getDataFolder(), path);
    }
}
