package eu.codingschool.homeautomation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	/**
	 * This is the kind of the information we will keep for a device type. 
	 * For instance, for oven we keep 'temperature', for lighting the 'illumination percentage'.
	 */
	@Column(name = "information_type")
	private String informationType;
	
	@Column(name = "unit_of_measure")
	private String unitOfMeasure;
	
	@Column(name = "min_value")
	private Integer minValue;
	
	@Column(name = "max_value")
	private Integer maxValue;
	
	@Column(name = "interaction_type")
	private String interactionType;
	
	@Column(name = "icon")
	private String icon;
	
	@Column(name = "group_color")
	private String groupColor;

	public DeviceType() {
		
	}
	
	public DeviceType(String type, String informationType) {
		this.type = type;
		this.informationType = informationType;
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

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public Integer getMinValue() {
		return minValue;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getGroupColor() {
		return groupColor;
	}

	public void setGroupColor(String groupColor) {
		this.groupColor = groupColor;
	}

}
