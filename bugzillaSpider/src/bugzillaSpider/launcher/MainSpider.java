package bugzillaSpider.launcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import bugzillaSpider.spider.BugSpider;

public class MainSpider {

	public static void main(String[] args) {
		// 初始化代理池
		// ProxyHelper ph = ProxyHelper.getInstance();
		// ph.setEnable(false);
		BugSpider bs = new BugSpider();
		// 1039500
		// 18574
		// 37035
		int start = 0;
		try {
			BufferedReader bfr = new BufferedReader(new FileReader("tmp"));
			start = -1;
			try {
				start = Integer.parseInt(bfr.readLine());
			} catch (Exception e) {
				start = -1;
			}
			bfr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (start < 300000) {
			start = 300001;
		}
		for (int i = start; i <= 400000; i++) {
			bs.readBug(i);
		}
		System.out.println("DOWNLOAD OK!!!");
	}
}
