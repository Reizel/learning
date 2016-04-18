package dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;

import excecutor.ExcecuteHibernate;
import excecutor.ExcecutorForHibernate;
import model.BaseObject;

public abstract class HibernateDao<B extends BaseObject> implements DaoLayer<B> {

	private ExcecutorForHibernate excec = new ExcecutorForHibernate();
	private ParameterizedType genericType = (ParameterizedType) this.getClass().getGenericSuperclass();

	@Override
	public B get(int id) {
		@SuppressWarnings("unchecked")
		Class<B> genericClass = (Class<B>) genericType.getActualTypeArguments()[0];
		return excec.excecute(new ExcecuteHibernate() {
			@SuppressWarnings("unchecked")
			@Override
			public B handle(Session session) {
				return session.get(genericClass, id);
			}

		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<B> getList() {
		Class<B> genericClass = (Class<B>) genericType.getActualTypeArguments()[0];
		return excec.excecute(new ExcecuteHibernate() {
			@Override
			public List<B> handle(Session session) {
				return session.createQuery("from " + genericClass.getCanonicalName()).list();
			}
		});
	}

	@Override
	public void add(B obj) {
		excec.excecute(new ExcecuteHibernate() {
			@SuppressWarnings("unchecked")
			@Override
			public Object handle(Session session) {
				session.save(obj);
				return null;
			}
		});
	}

	@Override
	public void delete(B obj) {
		excec.excecute(new ExcecuteHibernate() {
			@SuppressWarnings("unchecked")
			@Override
			public Object handle(Session session) {
				session.delete(obj);
				return null;
			}
		});
	}

	@Override
	public void update(B obj) {
		excec.excecute(new ExcecuteHibernate() {
			@SuppressWarnings("unchecked")
			@Override
			public Object handle(Session session) {
				session.update(obj);
				return null;
			}
		});
	}
}