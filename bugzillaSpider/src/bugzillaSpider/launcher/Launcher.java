package bugzillaSpider.launcher;

import bugzillaSpider.constant.BugZilla;


public class Launcher {
	public static void main(String[] args) throws Exception {
		CategorySpider catesp = new CategorySpider();
		catesp.readCategory();
		BugZilla.ROOT_CATE.printCategory();
		catesp.saveCategory();
	}
}
