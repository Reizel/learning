package main;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.DaoLayer;

import model.BaseObject;
import model.City;
import model.User;

public class Main {

	public static final Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info("programm started!");

		@SuppressWarnings("resource")
		ApplicationContext actx = new ClassPathXmlApplicationContext("bean.xml");
		
		@SuppressWarnings("unchecked")
		DaoLayer<City> citydao = (DaoLayer<City>) actx.getBean("hibernateCity");

		ArrayList<City> arr = (ArrayList<City>) citydao.getList();
		logger.info(arr);
		for (BaseObject obj : arr) {
			System.out.println(obj);
		}
		@SuppressWarnings("unchecked")
		DaoLayer<User> userdao = (DaoLayer<User>) actx.getBean("hibernateUser");

//		User usr = new User();
//		usr.setName("John");
//		usr.setSurname("Smith");
//		usr.setAge(25);
//		usr.setAddres("Zalesskaya street");
//		usr.setSalary(100000);
//		usr.setCity(citydao.get(0));
//		userdao.add(usr);
		ArrayList<User> users = (ArrayList<User>)userdao.getList();
		for (BaseObject obj : users) {
			System.out.println(obj);
		}
		
	}
}
