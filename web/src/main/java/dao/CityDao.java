package dao;

import model.City;

public class CityDao extends BaseObjectDao<City> {

	private static CityDao instance = null;

	private CityDao() {

	}

	public static CityDao getInstance() {
		if (instance == null) {
			instance = new CityDao();
		}
		return instance;
	}
}