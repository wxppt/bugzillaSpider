package bugzillaSpider.helper;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LogHelper {
	public static synchronized void logSkip(int id) {
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

	public static synchronized void logHistory(int id) {
		try {
			BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("d:/log.log", true), "utf-8"));
			bfw.write("HISTORY: " + id + "\r\n");
			bfw.close();
			System.out.println("LOG HISTORY ERROR: " + id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void logEmptyRead(int id) {
		try {
			BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("d:/log.log", true), "utf-8"));
			bfw.write("EMPTY: " + id + "\r\n");
			bfw.close();
			System.out.println("EMPTY READ ERROR: " + id);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
