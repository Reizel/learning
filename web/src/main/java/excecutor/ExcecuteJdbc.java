package excecutor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ExcecuteJdbc {
	public <T> T handle(PreparedStatement stmt) throws SQLException;
}
