package factories;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.BaseObject;

public class BaceObjectFactory<B extends BaseObject> {
	Class<B> genericClass;

	public BaceObjectFactory(Class<B> objClass) {
		genericClass = objClass;
	}

	public B getObject(ResultSet res) throws SQLException {
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
}
