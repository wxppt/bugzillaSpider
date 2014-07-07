package bugzillaSpider.helper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class SourceCodeHelper {
	private final String DEFAULT_CHARSET = "utf-8";
	private URL url = null;
	private HttpURLConnection urlConnection = null;
	private Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("117.59.217.245", 80));

	public SourceCodeHelper(String u) throws IOException {
		url = new URL(u);
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) throws IOException {
		this.url = url;
		urlConnection = (HttpURLConnection) url.openConnection(proxy);
	}

	public String getSourceCode() {
		String charset = getCharset();
		return readSourceCodeWithCharset(charset);
	}

	public String getCharset() {
		String charsetLine = null;
		BufferedReader bfr = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			bfr = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream(), DEFAULT_CHARSET));
			String line;
			while ((line = bfr.readLine()) != null) {
				if (line.toLowerCase().contains("charset")) {
					charsetLine = line.trim();
					break;
				}
			}
			bfr.close();
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
		String charset = getCharset(charsetLine);
		return charset;
	}

	private String readSourceCodeWithCharset(String charset) {
		BufferedReader bfr = null;
		String source = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection(proxy);
			bfr = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream(), charset));
			String line;
			while ((line = bfr.readLine()) != null) {
				source += line.trim();
			}
			bfr.close();
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
		return source;
	}
	
	private String getCharset(String source) {
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
	
	public static void main(String[] args) {
		String url = "http://www.baidu.com";
		try {
			SourceCodeHelper sch = new SourceCodeHelper(url);
			System.out.println(sch.getSourceCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}