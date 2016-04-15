package dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import model.BaseObject;


public abstract  class HibernateDao<B extends BaseObject> implements DaoLayer<B>{
	
	private static SessionFactory sessionFactory  = new Configuration().configure().buildSessionFactory();
	
	@Override
	public B get(int id) {
		final Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
		@SuppressWarnings("unchecked")
		Class<B> genericClass = (Class<B>) t.getActualTypeArguments()[0];
		try {
			return  (B) session.get(genericClass, id);
			
		} finally {
			tx.commit();
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<B> getList() {
		final Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
		Class<B> genericClass = (Class<B>) t.getActualTypeArguments()[0];
		try {
			return session.createQuery("from "+ genericClass.getCanonicalName()).list();
		} finally {
			tx.commit();
			session.close();
		}
	}

	@Override
	public void add(B obj) {
		final Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.save(obj);
		} finally {
			tx.commit();
			session.close();
		}	
	}

	@Override
	public void delete(B obj) {
		final Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.delete(obj);
		} finally {
			tx.commit();
			session.close();
		}
	}

	@Override
	public void update(B obj) {
		final Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.update(obj);
		} finally {
			tx.commit();
			session.close();
		}
	}
}
