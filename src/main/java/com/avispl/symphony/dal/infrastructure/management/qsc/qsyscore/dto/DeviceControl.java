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
	private boolean switchValue;
	@JsonAlias("Value")
	private float sliderValue;
	@JsonAlias("ValueMin")
	private float minValue;
	@JsonAlias("ValueMax")
	private float maxValue;
	@JsonAlias("StringMin")
	private String minValueString;
	@JsonAlias("StringMax")
	private String maxValueString;
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
	 * Retrieves {@link #switchValue}
	 *
	 * @return value of {@link #switchValue}
	 */
	public boolean isSwitchValue() {
		return switchValue;
	}

	/**
	 * Sets {@link #switchValue} value
	 *
	 * @param switchValue new value of {@link #switchValue}
	 */
	public void setSwitchValue(boolean switchValue) {
		this.switchValue = switchValue;
	}

	/**
	 * Retrieves {@link #sliderValue}
	 *
	 * @return value of {@link #sliderValue}
	 */
	public float getSliderValue() {
		return sliderValue;
	}

	/**
	 * Sets {@link #sliderValue} value
	 *
	 * @param sliderValue new value of {@link #sliderValue}
	 */
	public void setSliderValue(float sliderValue) {
		this.sliderValue = sliderValue;
	}

	/**
	 * Retrieves {@link #minValue}
	 *
	 * @return value of {@link #minValue}
	 */
	public float getMinValue() {
		return minValue;
	}

	/**
	 * Sets {@link #minValue} value
	 *
	 * @param minValue new value of {@link #minValue}
	 */
	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	/**
	 * Retrieves {@link #maxValue}
	 *
	 * @return value of {@link #maxValue}
	 */
	public float getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets {@link #maxValue} value
	 *
	 * @param maxValue new value of {@link #maxValue}
	 */
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
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

	/**
	 * Retrieves {@link #minValueString}
	 *
	 * @return value of {@link #minValueString}
	 */
	public String getMinValueString() {
		return minValueString;
	}

	/**
	 * Sets {@link #minValueString} value
	 *
	 * @param minValueString new value of {@link #minValueString}
	 */
	public void setMinValueString(String minValueString) {
		this.minValueString = minValueString;
	}

	/**
	 * Retrieves {@link #maxValueString}
	 *
	 * @return value of {@link #maxValueString}
	 */
	public String getMaxValueString() {
		return maxValueString;
	}

	/**
	 * Sets {@link #maxValueString} value
	 *
	 * @param maxValueString new value of {@link #maxValueString}
	 */
	public void setMaxValueString(String maxValueString) {
		this.maxValueString = maxValueString;
	}
}