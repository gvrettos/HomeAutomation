package eu.codingschool.homeautomation.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "DEVICE")
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "name")
	private String name;

	/**
	 * This will be either ON or OFF.
	 */
	@Column(name = "status")
	@Type(type="numeric_boolean")
	private boolean statusOn;

	/**
	 * It keeps a value for the specific device related to the type of the device. 
	 * For instance, for the oven we keep the temperature in Celsius degrees, for lighting we keep the percentage of the 
	 * illumination, etc.
	 */
	@Column(name = "information_value")
	private String informationValue;
	
	@ManyToOne
	@JoinColumn(name = "device_type_id")
	private DeviceType deviceType;
	
	@ManyToOne
	@JoinColumn(name = "room_id")
	private Room room;
	
	@ManyToMany(mappedBy = "devices")
	private Set<Person> persons;
	
	public Device() {
		
	}
	
	public Device(String name, boolean statusOn, String informationValue, DeviceType deviceType, Room room) {
		this.name = name;
		this.statusOn = statusOn;
		this.informationValue = informationValue;
		this.room = room;
		this.deviceType = deviceType;
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

	public boolean isStatusOn() {
		return statusOn;
	}

	public void setStatusOn(boolean statusOn) {
		this.statusOn = statusOn;
	}

	public String getInformationValue() {
		return informationValue;
	}

	public void setInformationValue(String informationValue) {
		this.informationValue = informationValue;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public Set<Person> getPersons() {
		return persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}
	
	public String getInformationText() {
		String str = new String();
		
		if (deviceType.getInformationType() != null) {
			str += deviceType.getInformationType();
		}
		if (deviceType.getUnitOfMeasure() != null) {
			str += " " + deviceType.getUnitOfMeasure();
		}
		
		return str;
		 
	}
	public String getInteractionType() {
		return deviceType.getInteractionType();
	}
}
