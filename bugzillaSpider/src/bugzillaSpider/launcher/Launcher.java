package bugzillaSpider.launcher;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Proxy;

import bugzillaSpider.constant.Const;
import bugzillaSpider.helper.LogHelper;
import bugzillaSpider.helper.ProxyHelper;
import bugzillaSpider.helper.SourceCodeHelper;

public class Launcher {
	public static void main(String[] args) throws IOException {
		ProxyHelper ph = new ProxyHelper();
		ph.addWebProxy();
		ph.validateProxy(30);
		System.out.println("OK");
		int trys = 3;
		for (int i = 10742; i < 1000000; i++) {
			System.out.println(i);
			String id = "" + i;
			String urlStr = Const.bugXmlUrl(id);
			String source = null;
			Proxy p = ph.getProxy();
			try {
				SourceCodeHelper sch = new SourceCodeHelper(urlStr);
				sch.setProxy(p);
				source = sch.getSourceCode(Const.DEFAULT_CHARSET);
				if (source != null && !source.contains("InvalidBugId")) {
					source = source.replace("urlbase", " urlbase").replace(
							"maintainer", " maintainer");
					System.out.print("SAVING SOURCECODE...");
					saveFile(id, source);
					System.out.println("OK");
				} else {
					System.out.println("Invalid Bug Id!");
				}
				ph.updateProxy(p, sch.getConnTime(), sch.getReadTime());
				trys = 3;
			} catch (Exception e) {
				if (trys-- > 0) {
					i--;
					ph.updateProxy(p, Const.MAX_TIME, Const.MAX_TIME);
					System.out.println("Try Again");
					continue;
				}
				trys = 3;
				LogHelper.logSkip(i);
				continue;
			}
		}
	}

	public static void saveFile(String id, String content) throws IOException {
		BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("d:/bugZilla/bugs/" + id + ".xml"),
				"utf-8"));
		bfw.write(content);
		bfw.close();
	}
}
