package bugzillaSpider.helper;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import bugzillaSpider.constant.Const;

public class MysqlHelper {
	private synchronized Connection getConnection() {
		String url = "jdbc:mysql://localhost:3306/bugzilla?&characterEncoding=utf8";
		String user = "root";
		String password = "123123";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			try {
				Connection connection = DriverManager.getConnection(url, user,
						password);
				return connection;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}

	public void addProxy(Proxy p) {
		Connection connection = getConnection();
		PreparedStatement statement = null;
		InetSocketAddress add = (InetSocketAddress) p.address();
		try {
			statement = connection
					.prepareStatement("insert into proxy(ip,port,read_time) values(?,?,?,?)");
			statement.setString(1, add.getHostString());
			statement.setInt(2, add.getPort());
			statement.setInt(3, Const.MAX_TIME);
			statement.execute();
			System.out.println("ADD PROXY (" + add.getHostString() + ":"
					+ add.getPort() + ")");
		} catch (SQLException e) {
			if (e.getMessage().contains("Duplicate entry")) {
				System.out.println("EXISTED PROXY (" + add.getHostString()
						+ ":" + add.getPort() + ")");
			}
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteProxy(Proxy p) {
		Connection connection = getConnection();
		PreparedStatement statement = null;
		InetSocketAddress add = (InetSocketAddress) p.address();
		try {
			statement = connection
					.prepareStatement("delete from proxy where ip=? AND port=?");
			statement.setString(1, add.getHostString());
			statement.setInt(2, add.getPort());
			statement.execute();
			System.out.println("DELETE PROXY (" + add.getHostString() + ":"
					+ add.getPort() + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateProxy(Proxy p, int read_time) {
		Connection connection = getConnection();
		PreparedStatement statement = null;
		InetSocketAddress add = (InetSocketAddress) p.address();
		try {
			statement = connection
					.prepareStatement("update proxy set read_time=? where ip=? AND port=?");
			statement.setInt(1, read_time);
			statement.setString(2, add.getHostString());
			statement.setInt(3, add.getPort());
			statement.execute();
			System.out.println("UPDATE PROXY (" + add.getHostString() + ":"
					+ add.getPort() + "): " + "READ=" + read_time);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Proxy> selectTopProxy(int i) {
		ArrayList<Proxy> list = new ArrayList<Proxy>();
		Connection connection = getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection
					.prepareStatement("select ip,port from proxy where read_time <> ? order by read_time limit ?");
			statement.setInt(1, Const.MAX_TIME);
			statement.setInt(2, i);
			rs = statement.executeQuery();
			while (rs.next()) {
				Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						rs.getString(1), rs.getInt(2)));
				list.add(p);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public ArrayList<Proxy> selectUnvalidateProxy() {
		ArrayList<Proxy> list = new ArrayList<Proxy>();
		Connection connection = getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection
					.prepareStatement("select ip,port from proxy where read_time=?");
			statement.setInt(1, Const.MAX_TIME);
			rs = statement.executeQuery();
			while (rs.next()) {
				Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						rs.getString(1), rs.getInt(2)));
				list.add(p);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}