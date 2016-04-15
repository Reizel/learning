package dao;

import model.User;

public class UserDao extends BaseObjectDao<User> {
	
	private static UserDao instance = null;
	
	private UserDao(){
		
	}
	public static UserDao getInstance() {
		if (instance == null) {
			instance = new UserDao();
		}
		return instance;
	}
}