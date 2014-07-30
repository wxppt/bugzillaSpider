package bugzillaSpider.spider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bugzillaSpider.PO.Category;
import bugzillaSpider.constant.BugZilla;
import bugzillaSpider.constant.Const;
import bugzillaSpider.helper.SourceCodeHelper;

public class CategorySpider {
	public void initCategory() throws IOException {
		SourceCodeHelper sch = new SourceCodeHelper(Const.PRODUCT_CATE_URL);
		String s = sch.getSourceCode(SourceCodeHelper.DEFAULT_CHARSET);
		Pattern p = Pattern
				.compile("< {0,2}select {0,2}name {0,2}= {0,2}\"product\" {0,2}>.+?< {0,2}/ {0,2}select {0,2}>");
		Matcher m = p.matcher(s);
		BugZilla.ROOT_CATE.getCateList().clear();
		while (m.find()) {
			String selectSec = m.group();
			Pattern groupp = Pattern
					.compile("< {0,2}optgroup {0,2}label {0,2}= {0,2}\".+?\" {0,2}>.+?< {0,2}/ {0,2}optgroup {0,2}>");
			Matcher groupm = groupp.matcher(selectSec);
			while (groupm.find()) {
				String groupSec = groupm.group();
				String groupName = groupSec.split("\"")[1];
				Category newGroupCate = new Category(groupName, groupName);
				Pattern optp = Pattern
						.compile("<option {0,2}value {0,2}= {0,2}\".+?\" {0,2}>.+?< {0,2}/ {0,2}option {0,2}>");
				Matcher optm = optp.matcher(groupSec);
				while (optm.find()) {
					String optSec = optm.group();
					String queryName = optSec.split("\"")[1];
					String showName = optSec.split(">")[1].split("<")[0];
					Category newOptCate = new Category(showName, queryName);
					System.out.println(queryName);
					newOptCate.getBugList().addAll(getBugList(queryName));
					newGroupCate.getCateList().add(newOptCate);
				}
				BugZilla.ROOT_CATE.getCateList().add(newGroupCate);
			}
		}
		// BugZilla.ROOT_CATE.printCategory();
	}

	public ArrayList<String> getBugList(String queryCatename)
			throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		SourceCodeHelper sch = new SourceCodeHelper(
				Const.fullProductUrl(queryCatename));
		String source = sch.getSourceCode("utf-8");
		Pattern p = Pattern.compile("> {0,3}[0-9]{3,10} {0,3}<");
		Matcher m = p.matcher(source);
		while (m.find()) {
			String id = m.group().replaceAll(">|<", "").trim();
			System.out.println(queryCatename + " " + id);
			list.add(id);
		}
		System.out.println("Size: " + list.size());
		return list;
	}

	public void saveCategory() throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				Const.CATEGORY_SAVE_PATH));
		oos.writeObject(BugZilla.ROOT_CATE);
		oos.close();
	}

	public void readCategory() throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				Const.CATEGORY_SAVE_PATH));
		Object obj = ois.readObject();
		if (obj instanceof Category) {
			BugZilla.ROOT_CATE.getCateList().clear();
			BugZilla.ROOT_CATE.getCateList().addAll(
					((Category) obj).getCateList());
		} else {
			System.err.println("cate_read_error");
		}
		ois.close();
	}
}
