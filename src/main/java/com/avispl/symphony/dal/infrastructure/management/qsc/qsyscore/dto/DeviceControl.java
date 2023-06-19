package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceControl
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/19/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceControl {
	@JsonAlias("Name")
	private String name;
	@JsonAlias("Type")
	private String type;
	@JsonAlias("Value")
	private Object value;
	@JsonAlias("String")
	private String string;
	@JsonAlias("Position")
	private float position;
	@JsonAlias("Direction")
	private String direction;

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets {@link #value} value
	 *
	 * @param value new value of {@link #value}
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Retrieves {@link #string}
	 *
	 * @return value of {@link #string}
	 */
	public String getString() {
		return string;
	}

	/**
	 * Sets {@link #string} value
	 *
	 * @param string new value of {@link #string}
	 */
	public void setString(String string) {
		this.string = string;
	}

	/**
	 * Retrieves {@link #position}
	 *
	 * @return value of {@link #position}
	 */
	public float getPosition() {
		return position;
	}

	/**
	 * Sets {@link #position} value
	 *
	 * @param position new value of {@link #position}
	 */
	public void setPosition(float position) {
		this.position = position;
	}

	/**
	 * Retrieves {@link #direction}
	 *
	 * @return value of {@link #direction}
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * Sets {@link #direction} value
	 *
	 * @param direction new value of {@link #direction}
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}
}