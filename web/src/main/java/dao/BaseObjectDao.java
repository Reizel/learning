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
		B obj = null;
		ConnectionItem connection = null;
		ResultSet result = null;
		Statement st = null;
		try {
			connection = connectionPool.getConnectionItem();
			st = connection.getConn().createStatement();
			st.executeQuery(sqlGenerator.generateSelectBuId(id));
			result = st.getResultSet();
			result.next();
			obj = (B) baceObjectFactory(result);
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
		return obj;
	}

	@Override
	public ArrayList<B> getList() {
		logger.info("Получение обьектов из базы");
		ArrayList<B> arr = new ArrayList<B>();
		String sql = sqlGenerator.generateSelect();
		ConnectionItem connection = null;
		ResultSet result = null;
		Statement st = null;
		try {
			connection = connectionPool.getConnectionItem();
			st = connection.getConn().createStatement();
			st.executeQuery(sql.toString());
			result = st.getResultSet();
			while (result.next()) {
				arr.add((B) baceObjectFactory(result));
			}

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
		return arr;
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
			initAddStatement(st, obj);
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
		ConnectionItem connection = null;
		PreparedStatement st = null;
		try {
			connection = connectionPool.getConnectionItem();
			st = connection.getConn().prepareStatement(sql);
			initUpdateStatement(st, obj);
			st.executeUpdate();
		} catch (SQLException e) {
			logger.warn(e);
		} finally {
			if (connection != null) {
				try {
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
	public void delete(B obj) {
		logger.info("удаление обьекта из базы");
		String sql = sqlGenerator.generateDelete();
		ConnectionItem connection = null;
		PreparedStatement st = null;
		try {
			connection = connectionPool.getConnectionItem();
			st = connection.getConn().prepareStatement(sql);
			st.setInt(1, obj.getId());
			st.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					if (st != null)
						st.close();
					connectionPool.close(connection);
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
		}
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

	protected void initAddStatement(PreparedStatement st, B obj) throws SQLException {
		int i = 1;
		for (Field f : obj.getClass().getDeclaredFields()) {
			if (f.getAnnotation(javax.persistence.Id.class) != null) {
				continue;
			}
			try {
				f.setAccessible(true);
				st.setObject(i, f.get(obj));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			i++;
		}
	}

	protected void initUpdateStatement(PreparedStatement st, B obj) throws SQLException {
		initAddStatement(st, obj);
		st.setInt(st.getFetchSize(), obj.getId());
	}
}