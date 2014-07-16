package bugzillaSpider.launcher;

import bugzillaSpider.helper.ProxyHelper;
import bugzillaSpider.spider.BugSpider;

public class MainSpider {

	public static void main(String[] args) {
		// 初始化代理池
		ProxyHelper ph = ProxyHelper.getInstance();
		ph.setEnable(false);
		BugSpider bs = new BugSpider();
		for (int i = 10000; i <= 1039500; i++) {
			bs.readBug(i);
		}
	}
}
