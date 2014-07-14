package bugzillaSpider.launcher;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Test {

	public static void main(String[] args) {
		System.getProperties().setProperty("webdriver.chrome.driver",
				"D:/chromedriver.exe");
		WebDriver webDriver = new ChromeDriver();
		webDriver.get("http://www.baidu.com");
		String str = webDriver.getPageSource();
		System.out.println(str);
		webDriver.close();
	}
}