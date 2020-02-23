package com.github.yuttyann.scriptblockplus.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerCount {

	@SerializedName("uuid")
	@Expose
	private final UUID uuid;

	@SerializedName("infos")
	@Expose
	private List<PlayerCountInfo> infos = null;

	public PlayerCount(@NotNull UUID uuid) {
		this.uuid = uuid;
		try {
			PlayerCount playerCount = load(uuid);
			infos = playerCount == null ? new ArrayList<>() : playerCount.infos;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() throws IOException {
		Gson gson = new Gson();
		String json = gson.toJson(this);
		FileUtils.write(getFile(uuid), json, Charsets.UTF_8);
	}

	@Nullable
	public static PlayerCount load(@NotNull UUID uuid) throws IOException {
		File file = getFile(uuid);
		if (!file.exists()) {
			return null;
		}
		Gson gson = new Gson();
		String json = FileUtils.readToString(getFile(uuid), Charsets.UTF_8);
		return gson.fromJson(json, PlayerCount.class);
	}

	public void set(@NotNull BlockCoords blockCoords, @NotNull ScriptType scriptType, int amount) {
		action(blockCoords, scriptType, p -> p.setAmount(amount));
	}

	public void add(@NotNull BlockCoords blockCoords, @NotNull ScriptType scriptType) {
		action(blockCoords, scriptType, p -> p.add());
	}

	public void subtract(@NotNull BlockCoords blockCoords, @NotNull ScriptType scriptType) {
		action(blockCoords, scriptType, p -> p.subtract());
	}

	private void action(@NotNull BlockCoords blockCoords, @NotNull ScriptType scriptType, @NotNull Consumer<PlayerCountInfo> action) {
		Thread thread = new Thread(() -> {
			try {
				action.accept(getInfo(blockCoords, scriptType));
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@NotNull
	public PlayerCountInfo getInfo(@NotNull BlockCoords blockCoords, @NotNull ScriptType scriptType) {
		String fullCoords = blockCoords.getFullCoords();
		int hash = getHashCode(fullCoords, scriptType);
		PlayerCountInfo info = StreamUtils.fOrElse(infos, p -> p.hashCode() == hash, null);
		if (info == null) {
			infos.add(info = new PlayerCountInfo(fullCoords, scriptType));
		}
		return info;
	}

	@NotNull
	private static File getFile(@NotNull UUID uuid) {
		String path = "json/playercount/" + uuid.toString() + ".json";
		File file = new File(ScriptBlock.getInstance().getDataFolder(), path);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		return file;
	}

	private int getHashCode(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
		int hash = 1;
		int prime = 31;
		hash = prime * hash + fullCoords.hashCode();
		hash = prime * hash + scriptType.hashCode();
		return hash;
	}
}