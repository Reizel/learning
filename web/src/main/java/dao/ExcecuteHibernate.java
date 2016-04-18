package dao;

import org.hibernate.Session;

@FunctionalInterface
public interface ExcecuteHibernate {
	public  <T>T  handle(Session session);
}
