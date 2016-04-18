package excecutor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ExcecutorForHibernate {
	private static SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

	public <T> T excecute(ExcecuteHibernate handler) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			handler.handle(session);
		} finally {
			tx.commit();
			session.close();
		}
		return null;
	}

}
