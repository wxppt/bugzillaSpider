package bugzillaSpider.helper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeHelper {
	private static ChromeHelper instance = null;
	private WebDriver webDriver = null;

	private ChromeHelper() {
		System.getProperties().setProperty("webdriver.chrome.driver",
				"d:/chromedriver.exe");
		webDriver = new ChromeDriver();
	}

	public static ChromeHelper getInstance() {
		if (instance == null) {
			instance = new ChromeHelper();
		}
		return instance;
	}

	public String getSourceCode(String url) {
		webDriver.get(url);
		return webDriver.getPageSource();
	}

	public String getTitle() {
		return webDriver.getTitle();
	}

	public String getCurrentUrl() {
		return webDriver.getCurrentUrl();
	}

	public void exit() {
		webDriver.quit();
		webDriver = null;
		instance = null;
	}
}
