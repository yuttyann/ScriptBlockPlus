package com.github.yuttyann.scriptblockplus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.enums.PackageType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class PlayerSelector {

	private static final Object[] OBJECT_ARRAY = ArrayUtils.EMPTY_OBJECT_ARRAY;
	private static final Class<?>[] CLASS_ARRAY = ArrayUtils.EMPTY_CLASS_ARRAY;

	private static Map<String, Object> map = null;

	static class TileEntityCommand {

		private static Object newInstance() throws ReflectiveOperationException {
			return ((Class<?>) map.get("Class_TileEntityCommand")).newInstance();
		}

		private static void setWorld(Object titleEntityCommand, World world) throws ReflectiveOperationException {
			Method setWorld = (Method) map.get("TileEntityCommand_setWorld");
			Method getHandle = (Method) map.get("CraftWorld_getHandle");
			setWorld.invoke(titleEntityCommand, getHandle.invoke(world, OBJECT_ARRAY));
		}

		private static void setLocation(Object titleEntityCommand, Location location) throws ReflectiveOperationException {
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			Object setLocation = map.get("TileEntityCommand_setLocation");
			if (setLocation instanceof Method) {
				Method setBlockPosition = (Method) setLocation;
				Constructor<?> blockPosition = ((Constructor<?>) map.get("Constructor_BlockPosition"));
				setBlockPosition.invoke(titleEntityCommand, blockPosition.newInstance(x, y, z));
			} else {
				Field[] xyz = (Field[]) setLocation;
				xyz[0].set(titleEntityCommand, x);
				xyz[1].set(titleEntityCommand, y);
				xyz[2].set(titleEntityCommand, z);
			}
		}

		private static Object getCommandBlock(Object titleEntityCommand) throws ReflectiveOperationException {
			return ((Method) map.get("TileEntityCommand_getCommandBlock")).invoke(titleEntityCommand, OBJECT_ARRAY);
		}
	}

	protected static void load() throws ReflectiveOperationException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Pattern", Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$"));

		Class<?> nmsEntityClass = PackageType.NMS.getClass("Entity");
		map.put("NMSEntity_getBukkitEntity", nmsEntityClass.getMethod("getBukkitEntity", CLASS_ARRAY));

		Class<?> playerSelectorClass = PackageType.NMS.getClass("PlayerSelector");
		Class<?> iCommandListenerClass = PackageType.NMS.getClass("ICommandListener");
		Method getPlayers;
		if (Utils.isCB18orLater()) {
			getPlayers = playerSelectorClass.getMethod("getPlayers", iCommandListenerClass, String.class, Class.class);
		} else {
			getPlayers = playerSelectorClass.getMethod("getPlayers", iCommandListenerClass, String.class);
		}
		map.put("PlayerSelector_getPlayers", getPlayers);

		Class<?> tileEntityCommandClass = PackageType.NMS.getClass("TileEntityCommand");
		Class<?> craftWorldClass = PackageType.CB.getClass("CraftWorld");
		Class<?> worldClass = PackageType.NMS.getClass("World");
		map.put("Class_TileEntityCommand", tileEntityCommandClass);
		map.put("CraftWorld_getHandle", craftWorldClass.getMethod("getHandle", CLASS_ARRAY));
		map.put("TileEntityCommand_setWorld", tileEntityCommandClass.getMethod("a", worldClass));

		String methodName;
		Object setLocation;
		if (Utils.isCB18orLater()) {
			Class<?> blockPositionClass = PackageType.NMS.getClass("BlockPosition");
			methodName = Utils.isCB110orLater() ? "setPosition" : "a";
			setLocation = tileEntityCommandClass.getMethod(methodName, blockPositionClass);
			map.put("Constructor_BlockPosition", blockPositionClass.getConstructor(int.class, int.class, int.class));
		} else {
			Field[] xyz = new Field[] {
				tileEntityCommandClass.getField("x"),
				tileEntityCommandClass.getField("y"),
				tileEntityCommandClass.getField("z")
			};
			setLocation = xyz;
		}
		map.put("TileEntityCommand_setLocation", setLocation);

		methodName = Utils.isCB1710orLater() ? "getCommandBlock" : "a";
		map.put("TileEntityCommand_getCommandBlock", tileEntityCommandClass.getMethod(methodName, CLASS_ARRAY));

		PlayerSelector.map = map;
	}

	public static String getCommandBlockPattern(String command) {
		String[] array = StringUtils.split(command, " ");
		for (int i = 1; i < array.length; i++) {
			if (isPattern(array[i])) {
				return array[i];
			}
		}
		return null;
	}

	public static boolean isPattern(String s, String s1) {
		Matcher matcher = ((Pattern) map.get("Pattern")).matcher(s);
		if (matcher.matches()) {
			String s2 = matcher.group(1);
			return s1 == null || s1.equals(s2);
		}
		return false;
	}

	public static boolean isPattern(String s) {
		return isPattern(s, (String) null);
	}

	public static List<Player> getPlayers(Location location, String s) {
		try {
			Object iCommandListener = getBlockCommandListener(location);
			if (iCommandListener != null) {
				return getPlayers(iCommandListener, s);
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<Player> getPlayers(Object iCommandListener, String s) throws ReflectiveOperationException {
		Method getPlayers = (Method) map.get("PlayerSelector_getPlayers");
		List<Player> players;
		if (Utils.isCB18orLater()) {;
			Class<?> nmsEntityClass = PackageType.NMS.getClass("Entity");
			List<?> entities = (List<?>) getPlayers.invoke(null, iCommandListener, s, nmsEntityClass);
			players = new ArrayList<Player>(entities.size());
			for (Object nmsEntity : entities) {
				Entity entity = (Entity) getBukkitEntity(nmsEntity);
				if (entity instanceof Player) {
					players.add((Player) entity);
				}
			}
		} else {
			Object[] entities = (Object[]) getPlayers.invoke(null, iCommandListener, s);
			players = new ArrayList<Player>(entities.length);
			for (Object nmsEntity : entities) {
				players.add((Player) getBukkitEntity(nmsEntity));
			}
		}
		return players;
	}

	private static Object getBukkitEntity(Object nmsEntity) throws ReflectiveOperationException {
		return ((Method) map.get("NMSEntity_getBukkitEntity")).invoke(nmsEntity, OBJECT_ARRAY);
	}

	private static Object getBlockCommandListener(Location location) throws ReflectiveOperationException {
		Object titleEntityCommand = TileEntityCommand.newInstance();
		TileEntityCommand.setWorld(titleEntityCommand, location.getWorld());
		TileEntityCommand.setLocation(titleEntityCommand, location);
		return TileEntityCommand.getCommandBlock(titleEntityCommand);
	}
}