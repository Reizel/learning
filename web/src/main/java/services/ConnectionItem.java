package services;

import java.sql.Connection;

public class ConnectionItem {
	private int id = 0;

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public int getId() {
		return id;
	}

	private Connection conn;
	private boolean free;

	ConnectionItem(int id, Connection conn) {
		this.id = id;
		this.conn = conn;
		free = true;
	}
}