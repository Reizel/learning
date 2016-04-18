package dao;

import java.sql.Statement;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import services.ConnectionItem;
import services.DBConnection;
import services.SqlGenerator;

import model.BaseObject;

import org.apache.log4j.Logger;

public abstract class BaseObjectDao<B extends BaseObject> implements DaoLayer<B> {

	public static final Logger logger = Logger.getLogger(BaseObjectDao.class);

	private DBConnection connectionPool = new DBConnection(3);

	private Class<B> genericClass;
	private SqlGenerator<B> sqlGenerator;

	@SuppressWarnings("unchecked")
	public BaseObjectDao() {
		ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
		this.genericClass = (Class<B>) t.getActualTypeArguments()[0];
		sqlGenerator = new SqlGenerator<B>(genericClass);
	}

	@Override
	public B get(int id) {
		logger.info("Получение обьекта из базы");
		String sql = sqlGenerator.generateSelectBuId(id);
		return ExcecuteQuary(sql, new ExcecuteQuaryHandler() {
			@SuppressWarnings("unchecked")
			@Override
			public B handle(ResultSet resultSet) throws SQLException {
				B obj = null;
				resultSet.next();
				obj = (B) baceObjectFactory(resultSet);
				return obj;
			}
		});
	}

	@Override
	public ArrayList<B> getList() {
		logger.info("Получение обьектов из базы");
		String sql = sqlGenerator.generateSelect();
		return ExcecuteQuary(sql, new ExcecuteQuaryHandler() {
			@SuppressWarnings("unchecked")
			@Override
			public ArrayList<B> handle(ResultSet resultSet) throws SQLException {
				ArrayList<B> array = new ArrayList<B>();
				while (resultSet.next()) {
					array.add((B) baceObjectFactory(resultSet));
				}
				return array;
			}
		});
	}

	@Override
	public void add(B obj) {
		logger.info("Добавление обьекта в базу");
		String sql = sqlGenerator.generateInsert();
		ConnectionItem connection = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			connection = connectionPool.getConnectionItem();
			st = connection.getConn().prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
			int i = 1;
			for (Field f : obj.getClass().getDeclaredFields()) {
				if (f.getAnnotation(javax.persistence.Id.class) != null) {
					continue;
				}
				try {
					// так делать плохо... но пока оставлю
					f.setAccessible(true);
					st.setObject(i, f.get(obj));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				i++;
			}
			st.executeUpdate();
			rs = st.getGeneratedKeys();
			rs.next();
			obj.setId(rs.getInt(1));

		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					if (rs != null)
						rs.close();
					if (st != null)
						st.close();
					connectionPool.close(connection);
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
		}
	}

	@Override
	public void update(B obj) {
		logger.info("обновление обьекта в базе");
		String sql = sqlGenerator.generateUpdate();
		ExcecuteUpdate(sql, new ExcecuteUpdateHandler() {
			@Override
			public void handle(PreparedStatement stmt) throws SQLException {
				int i = 1;
				for (Field f : obj.getClass().getDeclaredFields()) {
					if (f.getAnnotation(javax.persistence.Id.class) != null) {
						continue;
					}
					try {
						// так делать плохо... но пока оставлю
						f.setAccessible(true);
						stmt.setObject(i, f.get(obj));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					i++;
				}
				stmt.setInt(i, obj.getId());
			}
		});
	}

	@Override
	public void delete(B obj) {
		logger.info("удаление обьекта из базы");
		String sql = sqlGenerator.generateDelete();
		ExcecuteUpdate(sql, new ExcecuteUpdateHandler() {
			@Override
			public void handle(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, obj.getId());
			}
		});
	}

	protected B baceObjectFactory(ResultSet res) throws SQLException {
		ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
		@SuppressWarnings("unchecked")
		Class<B> genericClass = (Class<B>) t.getActualTypeArguments()[0];
		B obj = null;
		try {
			obj = genericClass.newInstance();
			for (Field f : obj.getClass().getDeclaredFields()) {
				// так делать плохо... но пока оставлю
				f.setAccessible(true);
				f.set(obj, res.getObject(f.getAnnotation(javax.persistence.Column.class).name()));
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}

	private <T> T ExcecuteQuary(String sql, ExcecuteQuaryHandler handler) {
		ConnectionItem connection = null;
		ResultSet result = null;
		Statement st = null;
		try {
			connection = connectionPool.getConnectionItem();
			st = connection.getConn().createStatement();
			st.executeQuery(sql);
			result = st.getResultSet();
			return handler.handle(result);
		} catch (SQLException e) {
			logger.warn(e);
		} finally {
			try {
				if (result != null)
					result.close();
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

	private <T> T ExcecuteUpdate(String sql, ExcecuteUpdateHandler handler) {
		ConnectionItem connection = null;
		PreparedStatement st = null;
		try {
			connection = connectionPool.getConnectionItem();
			st = connection.getConn().prepareStatement(sql);
			handler.handle(st);
			st.executeUpdate(sql);
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