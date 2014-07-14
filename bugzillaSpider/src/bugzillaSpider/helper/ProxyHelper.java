package bugzillaSpider.helper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bugzillaSpider.constant.Const;

public class ProxyHelper {
	private DatabaseHelper dbhelper = new DatabaseHelper();
	private ArrayList<String> urlList = new ArrayList<String>();

	public ProxyHelper() {
		urlList.add("http://www.xici.net.co/nn/");
		urlList.add("http://www.xici.net.co/nt/");
		urlList.add("http://www.xici.net.co/wn/");
		urlList.add("http://www.xici.net.co/wt/");
	}

	public void addWebProxy() {
		for (String baseUrl : urlList) {
			for (int i = 1; i < 2; i++) {
				try {
					SourceCodeHelper sch = new SourceCodeHelper(baseUrl + i);
					String source = sch.getSourceCode(sch.getCharset());
					if (source == null) {
						continue;
					}
					source = source.replace("</td>", ":");
					source = source.replace("<td>", "");
					Pattern ip_port_p = Pattern
							.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,5}");
					Matcher ip_port_m = ip_port_p.matcher(source);
					boolean ifFind = false;
					while (ip_port_m.find()) {
						ifFind = true;
						String[] tmpAddress = ip_port_m.group().split(":");
						Proxy p = new Proxy(Proxy.Type.HTTP,
								new InetSocketAddress(tmpAddress[0],
										Integer.parseInt(tmpAddress[1])));
						dbhelper.addProxy(p);
					}
					// 如果找不到，说明该基地址已经扫描结束
					if (!ifFind) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	public void updateProxy(Proxy p, long conn_time, long read_time) {
		if (p != null) {
			dbhelper.updateProxy(p, (int) conn_time, (int) read_time);
		} else {
			System.out.println("UPDATE PROXY NULL ERROR!");
		}
	}

	public void validateProxy(int i) {
		ArrayList<Proxy> okList = dbhelper.selectTopProxy(i);
		if (okList == null) {
			System.out.println("VALIDATE OKLIST NULL ERROR!");
			return;
		} else if (okList.size() >= i) {
			System.out.println("ENOUGH PROXY!");
			return;
		}
		System.out.println("PROXY NUM = " + okList.size() + "");
		ArrayList<Proxy> list = dbhelper.selectUnvalidateProxy();
		if (list == null) {
			System.out.println("VALIDATE LIST NULL ERROR!");
			return;
		}
		if (list.size() < 5) {
			addWebProxy();
			list = dbhelper.selectUnvalidateProxy();
			if (list == null) {
				System.out.println("VALIDATE LIST NULL ERROR!");
				return;
			}
		}
		SourceCodeHelper sch = null;
		try {
			sch = new SourceCodeHelper(Const.ROOT_URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (sch == null) {
			return;
		}
		for (Proxy p : list) {
			sch.setProxy(p);
			InetSocketAddress add = (InetSocketAddress) p.address();
			System.out.println("VALIDATE PROXY: " + add.getHostString() + ":"
					+ add.getPort() + "");
			try {
				sch.getSourceCode(Const.DEFAULT_CHARSET);
				int conn_time = (int) sch.getConnTime();
				int read_time = (int) sch.getReadTime();
				dbhelper.updateProxy(p, conn_time, read_time);
				i--;
			} catch (Exception e) {
				dbhelper.deleteProxy(p);
				continue;
			}
			if (i == 0) {
				break;
			}
		}
	}

	public Proxy getProxy() {
		ArrayList<Proxy> list = dbhelper.selectTopProxy(10);
		if (list == null) {
			return null;
		}
		if (list.size() < 10) {
			validateProxy(30);
			list = dbhelper.selectTopProxy(10);
		}
		if (list == null) {
			return null;
		}
		int i = (int) (Math.random() * list.size());
		Proxy p = list.get(i);
		return p;
	}
}
