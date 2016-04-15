package dao;


import model.User;

public class HibernateUser extends HibernateDao<User>{
	private static HibernateUser instance = null;

	private HibernateUser(){
		
	}
	
	public static HibernateUser getInstance() {
		if (instance == null) {
			instance = new HibernateUser();
		}
		return instance;
	}
}
