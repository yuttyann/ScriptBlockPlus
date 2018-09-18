package com.github.yuttyann.scriptblockplus.commandblock;

import org.bukkit.Location;
import org.bukkit.World;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class TileEntityCommand implements ClassHelper {

	private final Object tileEntityCommand;

	private Object comamndBlock;

	public TileEntityCommand() throws ReflectiveOperationException {
		tileEntityCommand = PackageType.NMS.newInstance(d);
	}

	public void setWorld(World world) throws ReflectiveOperationException {
		Object nmsWorld = PackageType.CB.invokeMethod(world, j, "getHandle");
		Class<?> nmsWorldClass = PackageType.NMS.getClass("World");
		String methodName = Utils.isCBXXXorLater("1.13") ? "setWorld" : "a";
		PackageType.NMS.getMethod(d, methodName, nmsWorldClass).invoke(tileEntityCommand, nmsWorld);
	}

	public void setLocation(Location location) throws ReflectiveOperationException {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String methodName = Utils.isCBXXXorLater("1.10") ? "setPosition" : "a";
		Object instance = PackageType.NMS.newInstance(g, x, y, z);
		PackageType.NMS.invokeMethod(tileEntityCommand, d, methodName, instance);
	}

	public void setName(String name) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.13")) {
			Object cName = PackageType.NMS.newInstance(e, name);
			PackageType.NMS.getMethod(a, "setName", PackageType.NMS.getClass(f)).invoke(getCommandBlock(), cName);
		} else {
			PackageType.NMS.invokeMethod(getCommandBlock(), a, "setName", name);
		}
	}

	public Object getCommandBlock() throws ReflectiveOperationException {
		if (comamndBlock == null) {
			comamndBlock = PackageType.NMS.invokeMethod(tileEntityCommand, d, "getCommandBlock");
		}
		return comamndBlock;
	}

	public Object a() {
		return tileEntityCommand;
	}
}