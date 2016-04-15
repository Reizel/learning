package model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User implements BaseObject {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name")
	private String name;
	@Column(name = "surname")
	private String surname;
	@Column(name = "age")
	private int age;
	@Column(name = "addres")
	private String addres = null;
	@Column(name = "salary")
	private int salary;
	@ManyToOne
	@JoinColumn(name = "city_id")
	private City city;

	public User() {

	}

	public User(String name, String surname, int age, String addres, int salary) {

		this.name = name;
		this.surname = surname;
		this.age = age;
		this.addres = addres;
		this.salary = salary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getAge() {
		return age;

	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAddres() {
		return addres;
	}

	public void setAddres(String addres) {
		this.addres = addres;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}
}