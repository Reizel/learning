package main;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.DaoLayer;

import model.BaseObject;
import model.City;

public class Main {

	public static final Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info("programm started!");

		@SuppressWarnings("resource")
		ApplicationContext actx = new ClassPathXmlApplicationContext("bean.xml");
		@SuppressWarnings("unchecked")
		DaoLayer<City> citydao = (DaoLayer<City>) actx.getBean("citydao");

		ArrayList<City> arr = (ArrayList<City>) citydao.getList();
		logger.info(arr);
		for (BaseObject obj : arr) {
			System.out.println(obj);
		}
	}
}
