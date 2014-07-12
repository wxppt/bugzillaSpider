package bugzillaSpider.helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import bugzillaSpider.PO.Category;
import bugzillaSpider.constant.Const;

public class SourceCodeHelper {

	public static final String DEFAULT_CHARSET = "utf-8";
	private String urlStr = null;
	private URL url = null;
	private Proxy proxy = null;
	private HttpURLConnection conn = null;

	public SourceCodeHelper(String urlStr, Proxy proxy) throws IOException {
		this.urlStr = urlStr;
		url = new URL(urlStr);
		this.proxy = proxy;
	}

	public SourceCodeHelper(String urlStr) throws IOException {
		this.urlStr = urlStr;
		url = new URL(urlStr);
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	private void openConnection() throws IOException {
		if (urlStr.toLowerCase().startsWith("https")) {
			if (proxy == null) {
				conn = (HttpsURLConnection) url.openConnection();
			} else {
				conn = (HttpsURLConnection) url.openConnection(proxy);
			}
		} else {
			if (proxy == null) {
				conn = (HttpURLConnection) url.openConnection();
			} else {
				conn = (HttpURLConnection) url.openConnection(proxy);
			}
		}
	}

	public boolean testConnection() {
		if (conn != null) {
			try {
				conn.connect();
				return true;
			} catch (IOException e) {
			}
		}
		return false;
	}

	private void closeConnection() {
		if (conn != null) {
			conn.disconnect();
		}
	}

	public String getCharset() {
		String charsetLine = null;
		BufferedReader bfr = null;
		try {
			openConnection();
			bfr = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), DEFAULT_CHARSET));
			String line;
			while ((line = bfr.readLine()) != null) {
				if (line.toLowerCase().contains("charset")) {
					charsetLine = line.trim();
					break;
				}
			}
			bfr.close();
			closeConnection();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("error_charset");
		} finally {
			try {
				bfr.close();
			} catch (Exception e) {
			}
		}
		String charset = filterCharset(charsetLine);
		return charset;
	}

	public String getSourceCode() {
		return getSourceCode(getCharset());
	}

	public String getSourceCode(String charset) {
		BufferedReader bfr = null;
		String source = null;
		try {
			openConnection();
			bfr = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), charset));
			String line;
			while ((line = bfr.readLine()) != null) {
				source += line.trim();
			}
			bfr.close();
			closeConnection();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("error_read");
		} finally {
			try {
				bfr.close();
			} catch (Exception e) {
			}
		}
		if (source.startsWith("null")) {
			source = source.substring(4);
		}
		return source;
	}

	private String filterCharset(String source) {
		String charset = "utf-8";
		if (source != null) {
			String[] spstr = source.split("<");
			for (int i = 0; i < spstr.length; i++) {
				spstr[i] = spstr[i].toLowerCase();
				if (spstr[i].contains("meta") && spstr[i].contains("charset")) {
					spstr[i] = spstr[i].split("charset")[1];
					spstr[i] = spstr[i].replace("\"", "");
					spstr[i] = spstr[i].replace("/", "");
					spstr[i] = spstr[i].replace("\\", "");
					spstr[i] = spstr[i].replace("=", "");
					spstr[i] = spstr[i].replace(">", "");
					spstr[i] = spstr[i].trim();
					charset = spstr[i];
					break;
				}
			}
		}
		return charset;
	}

	public static void main(String[] args) throws IOException {
	}
}