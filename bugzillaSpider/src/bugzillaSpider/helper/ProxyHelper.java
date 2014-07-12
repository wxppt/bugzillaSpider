package bugzillaSpider.helper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyHelper {

	/**
	 * 列表，用于存储获取代理的基地址
	 */
	private ArrayList<String> urlList = new ArrayList<String>();
	/**
	 * 队列，用于存储没有经过验证的HTTP代理
	 */
	private LinkedList<Proxy> uncheckProxyList = new LinkedList<Proxy>();
	/**
	 * 列表，用于存储已经经过验证的代理
	 */
	private LinkedList<Proxy> proxyList = new LinkedList<Proxy>();

	/*
	 * 验证代理线程
	 */
	private Thread vT = null;

	/**
	 * 自动更新线程
	 */
	private Thread iT = null;
	/**
	 * 单例
	 */
	private static ProxyHelper instance = null;

	public static ProxyHelper getInstance() {
		if (instance == null) {
			instance = new ProxyHelper();
		}
		return instance;
	}

	public ProxyHelper() {
	}

	public void initProxyList() {
		// iT = new Thread(new Runnable() {
		// public void run() {
		urlList.add("http://www.xici.net.co/nn/");
//		urlList.add("http://www.xici.net.co/nt/");
//		urlList.add("http://www.xici.net.co/wn/");
//		urlList.add("http://www.xici.net.co/wt/");
		for (String baseUrl : urlList) {
			for (int i = 1; i < 5; i++) {
				try {
					SourceCodeHelper sch = new SourceCodeHelper(baseUrl + i);
					// System.out.println(baseUrl + i);
					String source = sch.getSourceCode();
					if (source == null) {
						continue;
					}
					source = source.replace("</td>", ":");
					source = source.replace("<td>", "");
					// System.out.println(source);
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
						// 向未验证队列中加入代理
						uncheckProxyList.offer(p);
						System.out.println("Add: " + tmpAddress[0] + "\t"
								+ tmpAddress[1]);
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
		// }
		// });
		// iT.start();
	}

	public void autoValidateProxy() {
		while (proxyList.size() < 10) {
			System.out.println("ProxyList:" + proxyList.size());
			System.out.println("WaitList:" + uncheckProxyList.size());
			Proxy p = uncheckProxyList.poll();
			if (p != null) {
				validateProxy(p);
			}
		}
		vT = new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (!uncheckProxyList.isEmpty()) {
						System.out.println("ProxyList:" + proxyList.size());
						System.out.println("WaitList:"
								+ uncheckProxyList.size());
						Proxy p = uncheckProxyList.poll();
						validateProxy(p);
					} else {
						System.out.println("WaitList is empty.");
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		// vT.start();
	}

	private void validateProxy(Proxy p) {
		try {
			URL u = new URL("http://"
					+ ((InetSocketAddress) p.address()).getHostString() + ":"
					+ ((InetSocketAddress) p.address()).getPort());
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.connect();
			proxyList.offer(p);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public void debug() {
		while (true) {
			try {
				System.out.println("ProxyList:" + proxyList.size());
				System.out.println("WaitList:" + uncheckProxyList.size());
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		}
	}

	public Proxy getProxy() {
		while (proxyList.size() < 10) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		}
		Proxy p = proxyList.poll();
		uncheckProxyList.offer(p);
		return p;
	}
}
