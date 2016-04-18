package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ExcecuteUpdateHandler {
	public  void  handle(PreparedStatement stmt) throws SQLException;
}

