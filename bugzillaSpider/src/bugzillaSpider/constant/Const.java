package bugzillaSpider.constant;

public class Const {
	private static String FULL_SEARCH_LIST_URL = "https://bugzilla.mozilla.org/buglist.cgi?bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&field0-0-0=product&field0-0-1=component&field0-0-2=alias&field0-0-3=short_desc&field0-0-4=status_whiteboard&field0-0-5=cf_crash_signature&query_format=advanced&type0-0-0=substring&type0-0-1=substring&type0-0-2=substring&type0-0-3=substring&type0-0-4=substring&type0-0-5=substring&value0-0-0=KEYWORDS&value0-0-1=KEYWORDS&value0-0-2=KEYWORDS&value0-0-3=KEYWORDS&value0-0-4=KEYWORDS&value0-0-5=KEYWORDS&order=bug_status%2Cpriority%2Cassigned_to%2Cbug_id&limit=0";
	private static String RAPID_SEARCH_LIST_URL = "https://bugzilla.mozilla.org/buglist.cgi?quicksearch=KEYWORDS";
	private static String FULL_PRODUCT_LIST_URL = "https://bugzilla.mozilla.org/buglist.cgi?product=KEYWORDS&query_format=advanced&order=bug_status%2Cpriority%2Cassigned_to%2Cbug_id&limit=0";
	private static String RAPID_PRODUCT_LIST_URL = "https://bugzilla.mozilla.org/buglist.cgi?product=KEYWORDS";
	private static String BUG_XML_URL = "https://bugzilla.mozilla.org/show_bug.cgi?ctype=xml&id=KEYWORDS";
	private static String BUG_HISTORY_URL = "https://bugzilla.mozilla.org/show_activity.cgi?id=KEYWORDS";

	public static final String ROOT_URL = "https://bugzilla.mozilla.org";
	public static final String PRODUCT_CATE_URL = "https://bugzilla.mozilla.org/page.cgi?id=productdashboard.html";

	public static final String CATEGORY_SAVE_PATH = "d:/bugZilla/category.dat";
	public static final String DEFAULT_CHARSET = "utf-8";

	public static final int MAX_TIME = 999999;

	public static String fullSearchUrl(String keywords) {
		return FULL_SEARCH_LIST_URL.replace("KEYWORDS", keywords);
	}

	public static String rapidSearchUrl(String keywords) {
		return RAPID_SEARCH_LIST_URL.replace("KEYWORDS", keywords);
	}

	public static String fullProductUrl(String keywords) {
		return FULL_PRODUCT_LIST_URL.replace("KEYWORDS", keywords);
	}

	public static String bugXmlUrl(String keywords) {
		return BUG_XML_URL.replace("KEYWORDS", keywords);
	}

	public static String bugHistoryUrl(String keywords) {
		return BUG_HISTORY_URL.replace("KEYWORDS", keywords);
	}

	public static String rapidProductUrl(String keywords) {
		return RAPID_PRODUCT_LIST_URL.replace("KEYWORDS", keywords);
	}

	public static String cattoRootUrl(String subUrl) {
		return ROOT_URL + "/" + subUrl;
	}
}
