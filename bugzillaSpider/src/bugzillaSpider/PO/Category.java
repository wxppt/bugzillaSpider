package bugzillaSpider.PO;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {
	private static String printFront = "";
	private String showName = new String();
	private String queryName = new String();
	private ArrayList<Category> categorylist = new ArrayList<Category>();
	private ArrayList<String> bugList = new ArrayList<String>();

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

	public ArrayList<Category> getCateList() {
		return categorylist;
	}

	public void setCateList(ArrayList<Category> categorylist) {
		this.categorylist = categorylist;
	}

	public ArrayList<String> getBugList() {
		return bugList;
	}

	public void setBugList(ArrayList<String> bugList) {
		this.bugList = bugList;
	}

	public void printCategory() {
		System.out.println(printFront + showName + " - " + queryName);
		if (!categorylist.isEmpty()) {
			printFront += "¡¤";
			for (Category cate : categorylist) {
				cate.printCategory();
			}
			printFront = printFront.substring(0, printFront.length() - 1);
		}
	}
}
