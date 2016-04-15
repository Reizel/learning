package servlets;

import java.io.IOException;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.DaoLayer;

import model.City;


@WebServlet("/App")
public class App extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	//CityDao cities = new CityDao();
	ApplicationContext actx = new ClassPathXmlApplicationContext("bean.xml");
	
	@SuppressWarnings("unchecked")
	DaoLayer<City> cities = (DaoLayer<City>) actx.getBean("citydao");
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html, charset='utf-8'");
		
		PrintWriter writer = resp.getWriter();
		
		writer.append(
				"<!DOCTYPE html>" +
				"<html>" +
				"<head>" +
				"     <title>my app</title>" +
				"</head>" +
				"<body>" +
				this.viewCities() +
				"     <form action='"+req.getContextPath()+"/App' method='post'>" +
				"         Name : <input type='text' name='name'>"+
				"         <input type='submit' value='Add city'>"+
				"     <form>"+
				"</body>" +
				"</html>"
		);
		writer.flush();
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html, charset='utf-8'");
		cities.add(new City(req.getParameter("name")));
		doGet(req, resp);
	}
	
	private String viewCities() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border : 1px solid black'>");
		for (City city : this.cities.getList()) {
			sb.append("<tr>");
				sb.append("<td style='border : 1px solid black'>").append(city.getId()).append("</td>");
				sb.append("<td style='border : 1px solid black'>").append(city.getName()).append("</td>");
				sb.append("<td style='border : 1px solid black'>Edit</td>");
				sb.append("<td style='border : 1px solid black'>Delete</td>");
			sb.append("</tr>");
			System.out.println(city);
		}
		sb.append("</table>");
		return sb.toString();
	}
}
