package com.github.yuttyann.scriptblockplus.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author ゆっちゃん
 * Jsonの文字列をMAPに変換するクラス
 */
public class JsonMap extends HashMap<Integer, Map<Object, Object>> implements Map<Integer, Map<Object, Object>> {

	private String json;

	/**
	 * コンストラクタ
	 * @param json
	 */
	public JsonMap(String json) {
		this.json = json;
		try {
			putJsonMap();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * MapにJsonを格納する
	 */
	@SuppressWarnings("unchecked")
	private void putJsonMap() throws ParseException {
		Set<Entry<Object, Object>> entrySet;
		Map<Object, Object> objectMap;
		JSONObject jsonObject;
		JSONParser jsonParser = new JSONParser();
		Object object = jsonParser.parse(json);
		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) object;
			Object object2;
			for (int i = 0, l = jsonArray.size(); i < l; i++) {
				object2 = jsonArray.get(i);
				if (object2 == null || object2.toString().length() == 0) {
					continue;
				}
				objectMap = new HashMap<Object, Object>();
				jsonObject = (JSONObject) jsonParser.parse(object2.toString());
				entrySet = jsonObject.entrySet();
				for(Entry<Object, Object> entry : entrySet) {
					objectMap.put(entry.getKey(), entry.getValue());
				}
				super.put(i, objectMap);
			}
		} else if (object instanceof JSONObject) {
			objectMap = new HashMap<Object, Object>();
			jsonObject = (JSONObject) object;
			entrySet = jsonObject.entrySet();
			for(Entry<Object, Object> entry : entrySet) {
				objectMap.put(entry.getKey(), entry.getValue());
			}
			super.put(0, objectMap);
		}
	}

	/**
	 * 配列を取得する
	 * @param jsonObject
	 * @param key
	 * @return JSONArray
	 */
	public JSONArray getJSONArray(JSONObject jsonObject, Object key) {
		return (JSONArray) jsonObject.get(key);
	}

	/**
	 * 配列を取得する
	 * @param object
	 * @return JSONArray
	 */
	public JSONArray getJSONArray(Object object) {
		return (JSONArray) object;
	}

	/**
	 * Mapを取得する
	 * @return Map
	 */
	public Map<Integer, Map<Object, Object>> getMap() {
		return this;
	}

	/**
	 * HashMapを取得する
	 * @return HashMap
	 */
	public HashMap<Integer, Map<Object, Object>> getHashMap() {
		return this;
	}

	/**
	 * 文字列を取得する
	 * @return HashMapの文字列
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}