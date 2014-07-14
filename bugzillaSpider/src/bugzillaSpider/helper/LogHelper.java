package bugzillaSpider.helper;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LogHelper {
	public static void logSkip(int id) {
		try {
			BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("d:/log.log", true), "utf-8"));
			bfw.write("SKIP: " + id + "\r\n");
			bfw.close();
			System.out.println("LOG SKIP: " + id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
