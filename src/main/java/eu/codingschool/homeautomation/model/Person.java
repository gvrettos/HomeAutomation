package eu.codingschool.homeautomation.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PERSON")
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "surname")
	private String surname;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "role")
	private String role;

	@ManyToMany
	@JoinTable(
		name = "PERSON_DEVICE",
		joinColumns = @JoinColumn(name="person_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name="device_id", referencedColumnName = "id")
	)
	private Set<Device> devices;
	
	public Person() {
		this.devices= new HashSet<Device>();
	}

	public Person(String name, String surname, String email, String password, String role) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.role = role;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Set<Device> getDevices() {
		return devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}
	
	public void addDevice(Device device) {
		if (this.devices != null) {
			this.devices.add(device);
		}
	}
	
	public void removeAllDevices() {
		if (this.devices != null) {
			this.devices.clear();
		}
	}
}
