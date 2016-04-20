package dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import model.BaseObject;

public class HibernateDao<B extends BaseObject> implements DaoLayer<B> {

	private static SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
	private ParameterizedType genericType = (ParameterizedType) this.getClass().getGenericSuperclass();
	@SuppressWarnings("unchecked")
	private Class<B> genericClass = (Class<B>) genericType.getActualTypeArguments()[0];
	public  Session session = sessionFactory.openSession();
	@Override
	public B get(int id) {
		return session.load(genericClass, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<B> getList() {
			 return session.createQuery("from " + genericClass.getCanonicalName()).list();
	}

	@Override
	public void add(B obj) {
		session.save(obj);
	}

	@Override
	public void delete(B obj) {
		session.delete(obj);
	}

	@Override
	public void update(B obj) {
		session.update(obj);
	}
}