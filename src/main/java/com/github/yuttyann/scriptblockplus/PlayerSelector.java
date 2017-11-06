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

	static final class TileEntityCommand {

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
				Constructor<?> constructor = ((Constructor<?>) map.get("Constructor_BlockPosition"));
				setBlockPosition.invoke(titleEntityCommand, constructor.newInstance(x, y, z));
			} else {
				Field[] xyz = (Field[]) setLocation;
				xyz[0].set(titleEntityCommand, x);
				xyz[1].set(titleEntityCommand, y);
				xyz[2].set(titleEntityCommand, z);
			}
		}

		private static Object getCommandBlock(Object titleEntityCommand) throws ReflectiveOperationException {
			Method getCommandBlock = (Method) map.get("TileEntityCommand_getCommandBlock");
			return getCommandBlock.invoke(titleEntityCommand, OBJECT_ARRAY);
		}
	}

	protected static void load() throws ReflectiveOperationException {
		map = new HashMap<String, Object>();
		map.put("Pattern", Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$"));

		Class<?> nmsEntity = PackageType.MINECRAFT_SERVER.getClass("Entity");
		if (Utils.isCB18orLater()) {
			map.put("Class_NMSEntity", nmsEntity);
		}
		map.put("NMSEntity_getBukkitEntity", nmsEntity.getMethod("getBukkitEntity", CLASS_ARRAY));

		Class<?> iCommandListener = PackageType.MINECRAFT_SERVER.getClass("ICommandListener");
		Class<?> playerSelector = PackageType.MINECRAFT_SERVER.getClass("PlayerSelector");
		Method getPlayers;
		if (Utils.isCB18orLater()) {
			getPlayers = playerSelector.getMethod("getPlayers", iCommandListener, String.class, Class.class);
		} else {
			getPlayers = playerSelector.getMethod("getPlayers", iCommandListener, String.class);
		}
		map.put("PlayerSelector_getPlayers", getPlayers);

		Class<?> tileEntityCommand = PackageType.MINECRAFT_SERVER.getClass("TileEntityCommand");
		Class<?> craftWorld = PackageType.CRAFTBUKKIT.getClass("CraftWorld");
		Class<?> world = PackageType.MINECRAFT_SERVER.getClass("World");
		map.put("Class_TileEntityCommand", tileEntityCommand);
		map.put("CraftWorld_getHandle", craftWorld.getMethod("getHandle", CLASS_ARRAY));
		map.put("TileEntityCommand_setWorld", tileEntityCommand.getMethod("a", world));

		Object setLocation;
		if (Utils.isCB18orLater()) {
			Class<?> blockPosition = PackageType.MINECRAFT_SERVER.getClass("BlockPosition");
			String methodName = Utils.isCB110orLater() ? "setPosition" : "a";
			setLocation = tileEntityCommand.getMethod(methodName, blockPosition);
			map.put("Constructor_BlockPosition", blockPosition.getConstructor(int.class, int.class, int.class));
		} else {
			Field[] xyz = new Field[] {
				tileEntityCommand.getField("x"),
				tileEntityCommand.getField("y"),
				tileEntityCommand.getField("z")
			};
			setLocation = xyz;
		}
		map.put("TileEntityCommand_setLocation", setLocation);

		Method getCommandBlock;
		if (Utils.isCB1710orLater()) {
			getCommandBlock = tileEntityCommand.getMethod("getCommandBlock", CLASS_ARRAY);
		} else {
			getCommandBlock = tileEntityCommand.getMethod("a", CLASS_ARRAY);
		}
		map.put("TileEntityCommand_getCommandBlock", getCommandBlock);
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

	public static Player[] getPlayers(Location location, String s) {
		try {
			Object iCommandListener = getBlockCommandListener(location);
			if (iCommandListener != null) {
				List<Player> players = getPlayers(iCommandListener, s);
				return players.size() > 0 ? players.toArray(new Player[players.size()]) : new Player[0];
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
			Class<?> class_NMSEntity = (Class<?>) map.get("Class_NMSEntity");
			List<?> entities = (List<?>) getPlayers.invoke(null, iCommandListener, s, class_NMSEntity);
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
		Method getBukkitEntity = (Method) map.get("NMSEntity_getBukkitEntity");
		Object entity = getBukkitEntity.invoke(nmsEntity, OBJECT_ARRAY);
		return Utils.isCB18orLater() ? (Entity) entity : (Player) entity;
	}

	private static Object getBlockCommandListener(Location location) throws ReflectiveOperationException {
		Object titleEntityCommand = TileEntityCommand.newInstance();
		TileEntityCommand.setWorld(titleEntityCommand, location.getWorld());
		TileEntityCommand.setLocation(titleEntityCommand, location);
		return TileEntityCommand.getCommandBlock(titleEntityCommand);
	}
}