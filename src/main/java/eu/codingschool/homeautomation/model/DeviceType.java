package eu.codingschool.homeautomation.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "DEVICE_TYPE")
public class DeviceType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "type")
	private String type;
	
	@Column(name = "information_type")
	private String informationType;
	
	// TODO Removed, according to Sokratis' comment on Slack 
//	@OneToMany(mappedBy = "deviceType")
//	private Set<Device> devices;

	public DeviceType() {
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInformationType() {
		return informationType;
	}

	public void setInformationType(String informationType) {
		this.informationType = informationType;
	}

//	public Set<Device> getDevices() {
//		return devices;
//	}
//
//	public void setDevices(Set<Device> devices) {
//		this.devices = devices;
//	}

}
