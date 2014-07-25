package bugzillaSpider.launcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import bugzillaSpider.spider.BugSpider;

public class MainSpider {

	public static void main(String[] args) {
		BugSpider bs = new BugSpider();
		int start = 10000;
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
		if (start < 10000) {
			start = 10000;
		}
		for (int i = start; i <= 1039500; i++) {
			bs.readBug(i);
		}
		System.out.println("DOWNLOAD OK!!!");
	}
}
