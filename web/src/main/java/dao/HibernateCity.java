package dao;


import org.springframework.stereotype.Component;

import model.City;

@Component
public class HibernateCity extends HibernateDao<City> {
	private static HibernateCity instance = null;

	private HibernateCity() {

	}
	
	public static HibernateCity getInstance() {
		if (instance == null) {
			instance = new HibernateCity();
		}
		return instance;
	}
}