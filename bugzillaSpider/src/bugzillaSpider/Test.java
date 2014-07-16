package bugzillaSpider;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import bugzillaSpider.constant.Const;

public class Test {
	public static void main(String[] args) {
		// Æô¶¯ä¯ÀÀÆ÷Ä£ÄâÆ÷
		System.getProperties().setProperty("webdriver.chrome.driver",
				"d:/chromedriver.exe");
		WebDriver webDriver = new ChromeDriver();
		
		webDriver
				.get(Const.fullProductUrl(""));
		String s = webDriver.getPageSource();
		System.out.println(s);
		webDriver.close();
		webDriver.quit();
	}
}
