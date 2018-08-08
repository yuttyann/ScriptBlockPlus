package com.github.yuttyann.scriptblockplus.commandblock;

import org.bukkit.Location;
import org.bukkit.World;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class TileEntityCommand implements ClassListener {

	private final Object tileEntityCommand;

	private Object comamndBlock;

	public TileEntityCommand() throws ReflectiveOperationException {
		tileEntityCommand = PackageType.NMS.newInstance(CLASS_NAME[1]);
	}

	public void setWorld(World world) throws ReflectiveOperationException {
		Object nmsWorld = PackageType.CB.invokeMethod(world, CLASS_NAME[7], "getHandle");
		Class<?> nmsWorldClass = PackageType.NMS.getClass("World");
		PackageType.NMS.getMethod(CLASS_NAME[2], "a", nmsWorldClass).invoke(tileEntityCommand, nmsWorld);
	}

	public void setLocation(Location location) throws ReflectiveOperationException {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String methodName = Utils.isCBXXXorLater("1.10") ? "setPosition" : "a";
		Object instance = PackageType.NMS.newInstance(CLASS_NAME[5], x, y, z);
		PackageType.NMS.invokeMethod(tileEntityCommand, CLASS_NAME[2], methodName, instance);
	}

	public void setName(String name) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.13")) {
			Object cName = PackageType.NMS.newInstance(CLASS_NAME[4], name);
			PackageType.NMS.invokeMethod(getCommandBlock(), CLASS_NAME[1], "setName", cName);
		} else {
			PackageType.NMS.invokeMethod(getCommandBlock(), CLASS_NAME[1], "setName", name);
		}
	}

	public Object getCommandBlock() throws ReflectiveOperationException {
		if (comamndBlock == null) {
			comamndBlock = PackageType.NMS.invokeMethod(tileEntityCommand, CLASS_NAME[2], "getCommandBlock");
		}
		return comamndBlock;
	}

	public Object a() {
		return tileEntityCommand;
	}
}