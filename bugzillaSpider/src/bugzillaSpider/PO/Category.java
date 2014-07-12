package bugzillaSpider.PO;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {
	private static String printFront = "";
	private String showName = new String();
	private String queryName = new String();
	private ArrayList<Category> list = new ArrayList<Category>();

	public Category(String showName, String queryName) {
		this.showName = showName;
		this.queryName = queryName;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public ArrayList<Category> getList() {
		return list;
	}

	public void setList(ArrayList<Category> list) {
		this.list = list;
	}

	public void printCategory() {
		System.out.println(printFront + showName + " - " + queryName);
		if (!list.isEmpty()) {
			printFront += "¡¤";
			for (Category cate : list) {
				cate.printCategory();
			}
			printFront = printFront.substring(0, printFront.length() - 1);
		}
	}
}
