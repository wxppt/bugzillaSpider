package bugzillaSpider.spider;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bugzillaSpider.constant.Const;
import bugzillaSpider.helper.LogHelper;
import bugzillaSpider.helper.MongoHelper;
import bugzillaSpider.helper.SourceCodeHelper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class BugSpider {
	// private ProxyHelper ph = ProxyHelper.getInstance();
	private MongoHelper mh = new MongoHelper();

	public void readBug(int id) {
		System.out.println("====READING BUG: " + id + "====");
		String urlStr = Const.bugXmlUrl("" + id);
		if (!mh.existBug("" + id)) {

			int tryTimes = 3;
			while (tryTimes > 0) {
				try {
					URL u = new URL(urlStr);
					HttpsURLConnection conn = (HttpsURLConnection) u
							.openConnection();
					Document dom = Jsoup.parse(conn.getInputStream(), "utf-8",
							urlStr);
					Elements skip1 = dom.getElementsByAttributeValue("error",
							"InvalidBugId");
					Elements skip2 = dom.getElementsByAttributeValue("error",
							"NotPermitted");

					// sch.setProxy(p);
					if (skip1.isEmpty() && skip2.isEmpty()) {

						System.out.print("FILTER SOURCECODE...");
						DBObject bug = filterBugInfo(dom);
						System.out.println("OK");

						System.out.print("GETTING HISTORY...");
						List<DBObject> his = getHistory("" + id);
						if (his == null) {
							his = new ArrayList<DBObject>();
							LogHelper.logHistory(id);
						}
						bug.put("history", his);
						System.out.println("OK");

						System.out.print("FILTER COMMENT...");
						ArrayList<DBObject> commentList = filterComment(dom);
						System.out.println("OK");
						System.out.print("PASSING COMMENT...");
						for (DBObject comm : commentList) {
							if (!mh.existComment(comm)) {
								comm.put("bug_id", "" + id);
								mh.addComment(comm);
							} else {
								System.out.println("COMMENT ALREADY EXISTED: "
										+ comm.get("commentid"));
							}
						}
						System.out.println("OK");

						System.out.print("PASSING SOURCECODE...");
						bug.put("url", urlStr);
						mh.addBug(bug);
						LogHelper.logThis(id + "");
						System.out.println("OK");
						// ph.updateProxy(p, sch.getReadTime());
					} else {
						System.out.println("EMPTY BUG!");
					}
					tryTimes = 0;
				} catch (Exception e) {
					System.out.println("ERROR: " + e.getMessage());
					System.out.println("TRY AGAIN: " + tryTimes);
					// ph.updateProxy(p, Const.MAX_TIME);
					tryTimes--;
					if (tryTimes == 0) {
						LogHelper.logSkip(id);
					}
					continue;
				}
			}
		} else {
			System.out.println("BUG ALREADY EXISTED: " + id);
		}
	}

	public DBObject filterBugInfo(Document dom) {
		DBObject bug = new BasicDBObject();

		String[] tags = { "bug_id", "creation_ts", "short_desc", "delta_ts",
				"reporter_accessible", "cclist_accessible",
				"classification_id", "classification", "product", "component",
				"version", "rep_platform", "op_sys", "bug_status",
				"resolution", "bug_file_loc", "status_whiteboard", "keywords",
				"priority", "bug_severity", "target_milestone",
				"everconfirmed", "reporter", "assigned_to", "votes" };

		String[] listTags = { "cc", "commentid", "attachid" };
		for (int i = 0; i < tags.length; i++) {
			Elements eles = dom.getElementsByTag(tags[i]);
			for (Element ele : eles) {
				String text = ele.text();
				// System.out.println(tags[i] + ": " + text);
				bug.put(tags[i], text);
				break;
			}
		}
		for (int i = 0; i < listTags.length; i++) {
			List<String> list = new ArrayList<String>();
			Elements eles = dom.getElementsByTag(listTags[i]);
			for (Element ele : eles) {
				String text = ele.text();
				if (!list.contains(text)) {
					// System.out.println(listTags[i] + ": " + text);
					list.add(text);
				}
			}
			bug.put(listTags[i], list);
		}
		// System.out.println(bug);
		return bug;
	}

	public List<DBObject> getHistory(String id) {
		System.out.println("====READING HISTORY: " + id + "====");
		String urlStr = Const.bugHistoryUrl("" + id);
		String source = null;
		// 初始化代理
		// Proxy p = ph.getProxy();

		int tryTimes = 3;
		while (tryTimes > 0) {
			try {
				SourceCodeHelper sch = new SourceCodeHelper(urlStr);
				// sch.setProxy(p);
				source = sch.getSourceCode(Const.DEFAULT_CHARSET);

				if (source != null && !source.contains("InvalidBugId")) {
					source = source.replace("urlbase", " urlbase").replace(
							"maintainer", " maintainer");

					System.out.print("FILTER SOURCECODE...");
					List<DBObject> history = filterHistory(source);
					System.out.println("OK");

					tryTimes = 0;

					return history;
				} else {
					System.out.println("CAN NOT READ SOURCECODE!");
					System.out.println("TRY AGAIN: " + tryTimes);
					tryTimes--;
				}
			} catch (Exception e) {
				System.out.println("ERROR: " + e.getMessage());
				System.out.println("TRY AGAIN: " + tryTimes);
				// ph.updateProxy(p, Const.MAX_TIME);
				tryTimes--;
				continue;
			}
		}
		return null;
	}

	public List<DBObject> filterHistory(String xmlSource) {
		List<DBObject> list = new ArrayList<DBObject>();
		Document dom = Jsoup.parse(xmlSource);
		// System.out.println(xmlSource);
		Elements eles = dom.getElementsByTag("tbody");
		for (Element ele : eles) {
			String text = ele.text();
			if (text.contains("Who") && text.contains("When")) {
				Elements tableEles = ele.getElementsByTag("td");
				int times = 0;
				for (int i = 0; i < tableEles.size(); i++) {
					Element tableCell = tableEles.get(i);
					if (tableCell.hasAttr("rowspan")) {
						DBObject dbobj = new BasicDBObject();
						times = Integer.parseInt(tableCell.attr("rowspan"));
						dbobj.put("who", tableEles.get(i).text());
						i++;
						dbobj.put("when", tableEles.get(i).text());
						List<DBObject> subList = new ArrayList<DBObject>();
						for (int j = 0; j < times; j++) {
							DBObject subdbobj = new BasicDBObject();
							i++;
							subdbobj.put("what", tableEles.get(i).text());
							i++;
							subdbobj.put("removed", tableEles.get(i).text());
							i++;
							subdbobj.put("added", tableEles.get(i).text());
							subList.add(subdbobj);
						}
						dbobj.put("detail", subList);
						list.add(dbobj);
					} else {
						System.out.println("一看就是程序写错了");
					}
				}
			}
		}
		// System.out.println(list);
		return list;
	}

	public ArrayList<DBObject> filterComment(Document dom) {
		ArrayList<DBObject> list = new ArrayList<DBObject>();
		Elements commeles = dom.getElementsByTag("long_desc");
		for (Element commele : commeles) {
			DBObject commobj = new BasicDBObject();
			String[] tags = { "commentid", "who", "bug_when", "thetext" };
			for (int i = 0; i < tags.length; i++) {
				String text = commele.getElementsByTag(tags[i]).text();
				commobj.put(tags[i], text);
				if (tags[i].equals("thetext")) {
					List<String> refidList = new ArrayList<String>();
					if (text.contains("(In reply to comment #")) {
						Pattern p = Pattern
								.compile("(In reply to comment #[0-9]*)");
						Matcher m = p.matcher(text);
						while (m.find()) {
							int commentPos = Integer.parseInt(m.group()
									.toLowerCase().replaceAll("[a-z]", "")
									.replace("#", "").trim());
							String refCommid = commeles.get(commentPos)
									.getElementsByTag("commentid").get(0)
									.text();
							if (!refidList.contains(refCommid)) {
								refidList.add(refCommid);
							}
						}
					}
					commobj.put("ref_commentid", refidList);
				}
			}
			list.add(commobj);
		}
		// System.out.println(list);
		return list;
	}
}
