package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * ScriptBlockPlus PlayerCount クラス
 * @author yuttyann44581
 */
public class PlayerCount extends Json<PlayerCountInfo> {

	public PlayerCount(@NotNull UUID uuid) {
		super(uuid);
	}

	public void set(@NotNull BlockCoords blockCoords, @NotNull ScriptType scriptType, final int amount) {
		action(getInfo(blockCoords, scriptType), p -> p.setAmount(amount));
	}

	public void add(@NotNull BlockCoords blockCoords, @NotNull ScriptType scriptType) {
		action(getInfo(blockCoords, scriptType), PlayerCountInfo::add);
	}

	public void subtract(@NotNull BlockCoords blockCoords, @NotNull ScriptType scriptType) {
		action(getInfo(blockCoords, scriptType), PlayerCountInfo::subtract);
	}

	@NotNull
	public PlayerCountInfo getInfo(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		int hash = Objects.hash(fullCoords, scriptType);
		PlayerCountInfo info = StreamUtils.fOrElse(list, p -> p.hashCode() == hash, null);
		if (info == null) {
			list.add(info = new PlayerCountInfo(fullCoords, scriptType));
		}
		return info;
	}
}