package excecutor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import services.ConnectionItem;
import services.DBConnection;

public class ExcecutorForJdbc {

	public static final Logger logger = Logger.getLogger(ExcecutorForJdbc.class);
	private DBConnection connectionPool = DBConnection.getInstance();

	public <T> T excecute(String sql, ExcecuteJdbc handler) {
		ConnectionItem connection = null;
		PreparedStatement st = null;
		try {
			connection = connectionPool.getConnectionItem();
			st = connection.getConn().prepareStatement(sql);
			handler.handle(st);
		} catch (SQLException e) {
			logger.warn(e);
		} finally {
			try {
				if (st != null)
					st.close();
				if (connection != null)
					connectionPool.close(connection);
			} catch (SQLException e) {
				logger.warn(e);
			}
		}
		return null;
	}
}