package com.github.yuttyann.scriptblockplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.yuttyann.scriptblockplus.util.Utils;

public class PlayerSelector {

	private static final Pattern a = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");
	private static final Pattern b = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
	private static final Pattern c = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");

	protected static Player[] getPlayers(Location location, String s) {
		Matcher matcher = a.matcher(s);
		if (!matcher.matches()) {
			return null;
		}
		Map<String, String> map = h(matcher.group(2));
		String s1 = matcher.group(1);
		int i = c(s1);
		int j = d(s1);
		int k = f(s1);
		int l = e(s1);
		int i1 = g(s1);
		int j1 = -1;
		GameMode mode = null;
		Location loc = null;
		if (location != null) {
			loc = location.clone();
		}
		Map<String, Integer> map1 = a(map);
		String s2 = null;
		String s3 = null;
		boolean flag = false;
		if (map.containsKey("rm")) {
			i = a((String) map.get("rm"), i);
			flag = true;
		}
		if (map.containsKey("r")) {
			j = a((String) map.get("r"), j);
			flag = true;
		}
		if (map.containsKey("lm")) {
			k = a((String) map.get("lm"), k);
		}
		if (map.containsKey("l")) {
			l = a((String) map.get("l"), l);
		}
		if (map.containsKey("x") && location != null) {
			loc.setX(a((String) map.get("x"), location.getX()));
			flag = true;
		}
		if (map.containsKey("y") && location != null) {
			loc.setY(a((String) map.get("y"), location.getY()));
			flag = true;
		}
		if (map.containsKey("z") && location != null) {
			loc.setZ(a((String) map.get("z"), location.getZ()));
			flag = true;
		}
		if (map.containsKey("m")) {
			j1 = a((String) map.get("m"), j1);
			if (j1 == 0) {
				mode = GameMode.SURVIVAL;
			} else if (j1 == 1) {
				mode = GameMode.CREATIVE;
			} else if (j1 == 2) {
				mode = GameMode.ADVENTURE;
			}
		}
		if (map.containsKey("c")) {
			i1 = a((String) map.get("c"), i1);
		}
		if (map.containsKey("team")) {
			s3 = (String) map.get("team");
		}
		if (map.containsKey("name")) {
			s2 = (String) map.get("name");
		}
		World world = flag ? location.getWorld() : null;
		List<Player> list;
		if (!s1.equals("p") && !s1.equals("a")) {
			if (s1.equals("r")) {
				list = a(loc, i, j, 0, mode, k, l, map1, s2, s3, world);
				Collections.shuffle(list);
				list = list.subList(0, Math.min(i1, list.size()));
				return list != null && !list.isEmpty() ? (Player[]) list.toArray(new Player[0]) : new Player[0];
			} else {
				return null;
			}
		} else {
			list = a(loc, i, j, i1, mode, k, l, map1, s2, s3, world);
			return list != null && !list.isEmpty() ? (Player[]) list.toArray(new Player[0]) : new Player[0];
		}
	}

	private static Map<String, Integer> a(Map<String, String> map) {
		HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
		for (String s : map.keySet()) {
			if (s.startsWith("score_") && s.length() > "score_".length()) {
				String s1 = s.substring("score_".length());
				hashmap.put(s1, Integer.valueOf(a(map.get(s), 1)));
			}
		}
		return hashmap;
	}

	protected static boolean isList(String s) {
		Matcher matcher = a.matcher(s);
		if (matcher.matches()) {
			Map<String, String> map = h(matcher.group(2));
			String s1 = matcher.group(1);
			int i = g(s1);
			if (map.containsKey("c")) {
				i = a(map.get("c"), i);
			}
			return i != 1;
		} else {
			return false;
		}
	}

	protected static boolean isPattern(String s, String s1) {
		Matcher matcher = a.matcher(s);
		if (matcher.matches()) {
			String s2 = matcher.group(1);
			return s1 == null || s1.equals(s2);
		} else {
			return false;
		}
	}

	protected static boolean isPattern(String s) {
		return isPattern(s, (String) null);
	}

	private static final int c(String s) {
		return 0;
	}

	private static final int d(String s) {
		return 0;
	}

	private static final int e(String s) {
		return Integer.MAX_VALUE;
	}

	private static final int f(String s) {
		return 0;
	}

	private static final int g(String s) {
		return s.equals("a") ? 0 : 1;
	}

	private static Map<String, String> h(String s) {
		HashMap<String, String> hashmap = new HashMap<String, String>();
		if (s == null) {
			return hashmap;
		}
		Matcher matcher = b.matcher(s);
		int i = 0;
		int j;
		for (j = -1; matcher.find(); j = matcher.end()) {
			String s1 = null;
			switch (i++) {
			case 0:
				s1 = "x";
				break;
			case 1:
				s1 = "y";
				break;
			case 2:
				s1 = "z";
				break;
			case 3:
				s1 = "r";
			}
			if (s1 != null && matcher.group(1).length() > 0) {
				hashmap.put(s1, matcher.group(1));
			}
		}
		if (j < s.length()) {
			matcher = c.matcher(j == -1 ? s : s.substring(j));
			while (matcher.find()) {
				hashmap.put(matcher.group(1), matcher.group(2));
			}
		}
		return hashmap;
	}

	private static int a(String s, int i) {
		int j = i;
		try {
			j = Integer.parseInt(s);
		} catch (Throwable throwable) {}
		return j;
	}

	private static double a(String s, double d0) {

		double d1 = d0;

		try {
			d1 = Double.parseDouble(s);
		} catch (Throwable throwable) {}
		return d1;
	}

	private static List<Player> a(final Location location, int i, int j, int k, GameMode l, int i1, int j1, Map<String, Integer> map, String s, String s1, World world) {
		List<Player> list = new ArrayList<Player>();
		boolean flag = k < 0;
		boolean flag1 = s != null && s.startsWith("!");
		boolean flag2 = s1 != null && s1.startsWith("!");
		k = k >= 0 ? k : -k;
		if (flag1) {
			s = s.substring(1);
		}
		if (flag2) {
			s1 = s1.substring(1);
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			if ((world == null || player.getWorld() == world) && (s == null || flag1 != s.equalsIgnoreCase(player.getName()))) {
				if (s1 != null) {
					Scoreboard sb = player.getScoreboard();
					@SuppressWarnings("deprecation")
					Team team = sb.getPlayerTeam(player);
					String s2 = team == null ? "" : team.getName();
					if (flag2 == s1.equalsIgnoreCase(s2)) {
						continue;
					}
				}
				if (location != null && (i > 0 || j > 0)) {
					double f = location.distance(player.getLocation());
					if (i > 0 && f < i || j > 0 && f > j) {
						continue;
					}
				}
				if (a(player, map) && (l == null || l == player.getGameMode()) && (i1 <= 0 || player.getLevel() >= i1) && player.getLevel() <= j1) {
					list.add(player);
				}
			}
		}
		if (location != null) {
			Collections.sort(list, new Comparator<Player>() {
				public int compare(Player p1, Player p2) {
					double d1 = p1.getLocation().distance(location);
					double d2 = p2.getLocation().distance(location);
					if (d1 < d2) {
						return -1;
					} else if (d1 > d2) {
						return 1;
					}
					return 0;
				}
			});
		}
		if (flag) {
			Collections.reverse(list);
		}
		if (k > 0) {
			list = list.subList(0, Math.min(k, list.size()));
		}
		return list;
	}

	private static boolean a(Player player, Map<String, Integer> map) {
		if (map == null || map.size() == 0) {
			return true;
		}
		Iterator<Entry<String, Integer>> iterator = map.entrySet().iterator();
		Entry<String, Integer> entry;
		boolean flag;
		int i;
		do {
			if (!iterator.hasNext()) {
				return true;
			}
			entry = iterator.next();
			String s = entry.getKey();
			flag = false;
			if (s.endsWith("_min") && s.length() > 4) {
				flag = true;
				s = s.substring(0, s.length() - 4);
			}
			Scoreboard scoreboard = player.getScoreboard();
			Objective objective = scoreboard.getObjective(s);
			if (objective == null) {
				return false;
			}
			Score score = getScore(objective, player);
			i = score.getScore();
			if (i < (entry.getValue()).intValue() && flag) {
				return false;
			}
		} while (i <= (entry.getValue()).intValue() || flag);
		return false;
	}

	@SuppressWarnings("deprecation")
	private static Score getScore(Objective objective, Player player) {
		if (Utils.isUpperVersion_v178()) {
			return objective.getScore(player.getName());
		} else {
			Score score = objective.getScore(player);
			return score;
		}
	}

	protected static String getCommandBlockPattern(String command) {
		String[] arguments = command.split(" ");
		for (int i = 1; i < arguments.length; i++) {
			if (isPattern(arguments[i])) {
				return arguments[i];
			}
		}
		return null;
	}
}
