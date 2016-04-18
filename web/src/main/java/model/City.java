package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cities")
public class City implements BaseObject {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;

	@Column(name = "cityname")
	private String name;

	public City() {
		
	}

	public City(String name) {
		this.id = -1;
		this.name = name;
	}

	public City(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "id=" + this.id + "| name=" + this.name;
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
