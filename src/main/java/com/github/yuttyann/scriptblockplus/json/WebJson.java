package com.github.yuttyann.scriptblockplus.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author ゆっちゃん
 * Web上のJsonを取得するクラス
 */
public class WebJson {

	private String url;

	/**
	 * コンストラクタ
	 * @param url
	 */
	public WebJson(String url) {
		if (isURL(url)) {
			this.url = url;
		}
	}

	/**
	 * 文字列がURLなのか確認する
	 * @param url WebサイトのURL
	 * @return 文字列がURLかどうか
	 */
	public boolean isURL(String url) {
		return url.startsWith("http://") || url.startsWith("https://");
	}

	/**
	 * URLを取得する
	 * @return WebサイトのURL
	 */
	public URL getURL() {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * JsonMapを取得する
	 * @return JsonMap
	 */
	public JsonMap getJsonMap() {
		return new JsonMap(toString());
	}

	/**
	 * 文字列を取得する
	 * @return Jsonの文字列
	 */
	@Override
	public String toString() {
		String json = null;
		InputStream input = null;
		InputStreamReader inReader = null;
		BufferedReader buReader = null;
		try {
			URLConnection urlconn = getURL().openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) urlconn;
			httpconn.setRequestMethod("GET");
			httpconn.setInstanceFollowRedirects(false);
			httpconn.connect();
			input = httpconn.getInputStream();
			inReader = new InputStreamReader(input);
			buReader = new BufferedReader(inReader);
			String line;
			StringBuilder builder = new StringBuilder();
			while ((line = buReader.readLine()) != null) {
				builder.append(line);
			}
			return json = builder.toString();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buReader != null) {
				try {
					buReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
	}
}