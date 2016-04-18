package dao;

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

import excecutor.ExcecuteJdbc;
import excecutor.ExcecutorForJdbc;
import factories.BaceObjectFactory;

public abstract class BaseObjectDao<B extends BaseObject> implements DaoLayer<B> {

	public static final Logger logger = Logger.getLogger(BaseObjectDao.class);

	private DBConnection connectionPool = DBConnection.getInstance();
	private ExcecutorForJdbc excec = new ExcecutorForJdbc();
	private Class<B> genericClass;
	private SqlGenerator<B> sqlGenerator;
	private BaceObjectFactory<B> factory;
	@SuppressWarnings("unchecked")
	public BaseObjectDao() {
		ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
		genericClass = (Class<B>) t.getActualTypeArguments()[0];
		sqlGenerator = new SqlGenerator<B>(genericClass);
		factory = new BaceObjectFactory<B>(genericClass);
	}

	@Override
	public B get(int id) {
		logger.info("Получение обьекта из базы");
		String sql = sqlGenerator.generateSelectBuId(id);
		return excec.excecute(sql, new ExcecuteJdbc() {

			@SuppressWarnings("unchecked")
			@Override
			public B handle(PreparedStatement stmt) throws SQLException {
				stmt.executeQuery();
				ResultSet resultSet = stmt.getResultSet();
				resultSet.next();
				B obj = (B) factory.getObject(resultSet);
				return obj;
			}

		});
	}

	@Override
	public ArrayList<B> getList() {
		logger.info("Получение обьектов из базы");
		String sql = sqlGenerator.generateSelect();
		return excec.excecute(sql, new ExcecuteJdbc() {

			@SuppressWarnings("unchecked")
			@Override
			public ArrayList<B> handle(PreparedStatement stmt) throws SQLException {
				stmt.executeQuery();
				ResultSet resultSet = stmt.getResultSet();
				ArrayList<B> array = new ArrayList<B>();
				while (resultSet.next()) {
					array.add((B) factory.getObject(resultSet));
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
		excec.excecute(sql, new ExcecuteJdbc() {
			@Override
			public <T> T handle(PreparedStatement stmt) throws SQLException {
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
				stmt.executeUpdate(sql);
				return null;
			}

		});
	}

	@Override
	public void delete(B obj) {
		logger.info("удаление обьекта из базы");
		String sql = sqlGenerator.generateDelete();
		excec.excecute(sql, new ExcecuteJdbc() {
			@Override
			public <T> T handle(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, obj.getId());
				stmt.executeUpdate(sql);
				return null;
			}
		});
	}
}