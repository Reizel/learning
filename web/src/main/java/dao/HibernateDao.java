package dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import model.BaseObject;

public class HibernateDao<B extends BaseObject> implements DaoLayer<B> {

	private SessionFactory sessionFactory;
	private ParameterizedType genericType = (ParameterizedType) this.getClass().getGenericSuperclass();
	@SuppressWarnings("unchecked")
	private Class<B> genericClass = (Class<B>) genericType.getActualTypeArguments()[0];

	public HibernateDao() {

	}

	public HibernateDao(Class<B> genericClass) {
		this.genericClass = genericClass;
	}
	@Transactional(readOnly=true)
	@Override
	public B get(int id) {
		return sessionFactory.getCurrentSession().load(genericClass, id);
	}

	@Transactional(readOnly=true)
	@SuppressWarnings("unchecked")
	@Override
	public List<B> getList() {
		return sessionFactory.getCurrentSession().createQuery("from " + genericClass.getCanonicalName()).list();	
	}
	@Transactional
	@Override
	public void add(B obj) {
		sessionFactory.getCurrentSession().save(obj);
	}
	@Transactional
	@Override
	public void delete(B obj) {
		sessionFactory.getCurrentSession().delete(obj);
	}
	@Transactional
	@Override
	public void update(B obj) {
		sessionFactory.getCurrentSession().update(obj);
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}