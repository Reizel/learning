package services;

import java.sql.*;

public class DBConnection {

	private ConnectionItem[] connectionPool;
	private int freeConnections = 0;
	private String url = "jdbc:postgresql://localhost:5432/my_db";
	private String userName = "postgres";
	private String password = "111111";
	private static DBConnection instance = null;

	public static DBConnection getInstance() {
		if (instance == null)
			instance = new DBConnection(3);
		return instance;
	}

	private DBConnection(int poolQuantity) {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		connectionPool = new ConnectionItem[poolQuantity];
		try {
			for (int i = 0; i < connectionPool.length; i++) {
				connectionPool[i] = new ConnectionItem(i, DriverManager.getConnection(url, userName, password));
				freeConnections++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ConnectionItem getConnectionItem() throws SQLException {
		ConnectionItem connection = null;
		synchronized (connectionPool) {
			while (freeConnections == 0) {
				try {
					connectionPool.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < connectionPool.length; i++) {
				if (connectionPool[i].isFree()) {
					connection = connectionPool[i];
					connectionPool[i].setFree(false);
					break;
				}
			}
		}
		return connection;
	}

	public void close(ConnectionItem conn) throws SQLException {
		synchronized (connectionPool) {
			conn.setFree(true);
			freeConnections++;
			connectionPool.notify();
		}
	}
}
