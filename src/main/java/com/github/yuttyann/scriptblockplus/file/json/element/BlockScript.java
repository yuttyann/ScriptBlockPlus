package com.github.yuttyann.scriptblockplus.file.json.element;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * ScriptBlockPlus BlockScript クラス
 * @author yuttyann44581
 */
public class BlockScript {

	@SerializedName("scripttype")
	@Expose
	private final ScriptType scriptType;

	@SerializedName("scripts")
	@Expose
	private final HashMap<String, ScriptParam> scripts = new HashMap<>();

	public BlockScript(@NotNull ScriptType scriptType) {
		this.scriptType = scriptType;
	}

	public boolean has(@NotNull Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		ScriptParam scriptParam = scripts.get(fullCoords);
		if (scriptParam == null) {
			return false;
		}
		return scriptParam.getAuthor().size() > 0;
	}

	public void remove(@NotNull Location location) {
		scripts.remove(BlockCoords.getFullCoords(location));
	}

	@NotNull
	public ScriptParam get(@NotNull Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		ScriptParam scriptParam = scripts.get(fullCoords);
		if (scriptParam == null) {
			scripts.put(fullCoords, scriptParam = new ScriptParam());
		}
		return scriptParam;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	@Override
	public int hashCode() {
		return scriptType.hashCode();
	}
}