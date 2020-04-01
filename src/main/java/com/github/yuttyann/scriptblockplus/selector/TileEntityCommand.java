package com.github.yuttyann.scriptblockplus.selector;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus TileEntityCommand NMSクラス
 * @author yuttyann44581
 */
public final class TileEntityCommand {

	private final Object tileEntityCommand;

	private Object comamndBlock;

	public TileEntityCommand() throws ReflectiveOperationException {
		tileEntityCommand = PackageType.NMS.newInstance("TileEntityCommand");
	}

	public void setWorld(@NotNull World world) throws ReflectiveOperationException {
		Object nmsWorld = PackageType.CB.invokeMethod(world, "CraftWorld", "getHandle");
		Class<?> nmsWorldClass = PackageType.NMS.getClass("World");
		String methodName = Utils.isCBXXXorLater("1.13") ? "setWorld" : "a";
		PackageType.NMS.getMethod("TileEntityCommand", methodName, nmsWorldClass).invoke(tileEntityCommand, nmsWorld);
	}

	public void setLocation(@NotNull Location location) throws ReflectiveOperationException {
		double x = location.getX(), y = location.getY(), z = location.getZ();
		String methodName = Utils.isCBXXXorLater("1.10") ? "setPosition" : "a";
		Object instance = PackageType.NMS.newInstance("BlockPosition", x, y, z);
		PackageType.NMS.invokeMethod(tileEntityCommand, "TileEntityCommand", methodName, instance);
	}

	public void setName(@NotNull String name) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.13")) {
			Object cName = PackageType.NMS.newInstance("ChatComponentText", name);
			Class<?> component = PackageType.NMS.getClass("IChatBaseComponent");
			PackageType.NMS.getMethod("CommandBlockListenerAbstract", "setName", component).invoke(getCommandBlock(), cName);
		} else {
			PackageType.NMS.invokeMethod(getCommandBlock(), "CommandBlockListenerAbstract", "setName", name);
		}
	}

	@NotNull
	public Object getCommandBlock() throws ReflectiveOperationException {
		if (comamndBlock == null) {
			comamndBlock = PackageType.NMS.invokeMethod(tileEntityCommand, "TileEntityCommand", "getCommandBlock");
		}
		return comamndBlock;
	}

	@NotNull
	public Object a() {
		return tileEntityCommand;
	}
}