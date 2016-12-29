package com.github.yuttyann.scriptblockplus.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FileDownload {

	protected static Document getDocument(String name) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(getInputStream(name));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	protected static InputStream getInputStream(String name) throws IOException {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("http://xml.yuttyann44581.net/uploads//").append(name).append(".xml");
			URLConnection urlconn = new URL(builder.toString()).openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) urlconn;
			httpconn.setAllowUserInteraction(false);
			httpconn.setInstanceFollowRedirects(true);
			httpconn.setRequestMethod("GET");
			httpconn.connect();
			int httpStatusCode = httpconn.getResponseCode();
			if (httpStatusCode != HttpURLConnection.HTTP_OK) {
				httpconn.disconnect();
				return null;
			} else {
				return httpconn.getInputStream();
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (ProtocolException e) {
			throw e;
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

	protected static void fileDownload(String url, File file) throws IOException {
		InputStream input = null;
		FileOutputStream output = null;
		try {
			URLConnection urlconn = new URL(url).openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) urlconn;
			httpconn.setAllowUserInteraction(false);
			httpconn.setInstanceFollowRedirects(true);
			httpconn.setRequestMethod("GET");
			httpconn.connect();
			int httpStatusCode = httpconn.getResponseCode();
			if (httpStatusCode != HttpURLConnection.HTTP_OK) {
				httpconn.disconnect();
				return;
			}
			input = httpconn.getInputStream();
			output = new FileOutputStream(file, false);
			byte[] buf = new byte[4096];
			int readByte = 0;
			while ((readByte = input.read(buf)) != -1) {
				output.write(buf, 0, readByte);
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (ProtocolException e) {
			throw e;
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (output != null) {
				try {
					output.flush();
					output.close();
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
	}
}
