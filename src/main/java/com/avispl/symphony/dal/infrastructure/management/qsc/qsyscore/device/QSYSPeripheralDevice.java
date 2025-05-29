/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.DropDown;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.Slider;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.DeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * DeviceInfo
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
public abstract class QSYSPeripheralDevice implements DeviceBehavior {
	private Map<String, String> stats;
	private List<AdvancedControllableProperty> advancedControllableProperties;
	private String name;
	private String type;
	private String id;

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
	 * Retrieves {@link #stats}
	 *
	 * @return value of {@link #stats}
	 */
	public Map<String, String> getStats() {
		return stats;
	}

	/**
	 * Sets {@link #stats} value
	 *
	 * @param stats new value of {@link #stats}
	 */
	public void setStats(Map<String, String> stats) {
		this.stats = stats;
	}

	/**
	 * Retrieves {@link #advancedControllableProperties}
	 *
	 * @return value of {@link #advancedControllableProperties}
	 */
	public List<AdvancedControllableProperty> getAdvancedControllableProperties() {
		return advancedControllableProperties;
	}

	/**
	 * Sets {@link #advancedControllableProperties} value
	 *
	 * @param advancedControllableProperties new value of {@link #advancedControllableProperties}
	 */
	public void setAdvancedControllableProperties(List<AdvancedControllableProperty> advancedControllableProperties) {
		this.advancedControllableProperties = advancedControllableProperties;
	}

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
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Create a preripheral device
	 */
	public QSYSPeripheralDevice() {
		stats = new HashMap<>();
		advancedControllableProperties = new ArrayList<>();
	}

	public void updateStatusMessage() {
		String deviceStatus = this.getStats().get(QSYSCoreConstant.STATUS);
		if (deviceStatus != null) {
			String[] splitStatus = deviceStatus.split(QSYSCoreConstant.HYPHEN, 2);
			if (splitStatus.length == 2) {
				this.getStats().put(QSYSCoreConstant.STATUS, splitStatus[0]);
				this.getStats().put(QSYSCoreConstant.STATUS_MESSAGE, splitStatus[1]);
			} else {
				this.getStats().remove(QSYSCoreConstant.STATUS_MESSAGE);
			}
		}

		deviceStatus = this.getStats().get(QSYSCoreConstant.STATUS_LED);
		if (deviceStatus != null) {
			String[] splitStatus = deviceStatus.split(QSYSCoreConstant.HYPHEN, 2);
			if (splitStatus.length == 2) {
				this.getStats().put(QSYSCoreConstant.STATUS_LED, splitStatus[0]);
				this.getStats().put(QSYSCoreConstant.STATUS_LED_MESSAGE, splitStatus[1]);
			} else {
				this.getStats().remove(QSYSCoreConstant.STATUS_LED_MESSAGE);
			}
		}

	}

	/**
	 * capitalize the first character of the string
	 *
	 * @param input input string
	 * @return string after fix
	 */
	public String uppercaseFirstCharacter(String input) {
		char firstChar = input.charAt(0);
		return Character.toUpperCase(firstChar) + input.substring(1);
	}

	/**
	 * Add addAdvancedControlProperties if advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats store all statistics
	 * @param property the property is item advancedControllableProperties
	 * @throws IllegalStateException when exception occur
	 */
	public void addAdvancedControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property, String value) {
		if (property != null) {
			advancedControllableProperties.removeIf(controllableProperty -> controllableProperty.getName().equals(property.getName()));

			String propertyValue = StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.EMPTY;
			stats.put(property.getName(), propertyValue);

			advancedControllableProperties.add(property);
		}
	}

	/**
	 * Create switch is control property for metric
	 *
	 * @param name the name of property
	 * @param status initial status (0|1)
	 * @return AdvancedControllableProperty switch instance
	 */
	public AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
		advancedControllableProperty.setName(name);
		advancedControllableProperty.setValue(status);
		advancedControllableProperty.setType(toggle);
		advancedControllableProperty.setTimestamp(new Date());

		return advancedControllableProperty;
	}

	/**
	 * Create dropdown advanced controllable property
	 *
	 * @param name the name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty dropdown instance
	 */
	public AdvancedControllableProperty createDropdown(String name, String[] values, String initialValue) {
		DropDown dropDown = new DropDown();
		dropDown.setOptions(values);
		dropDown.setLabels(values);

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}

	/**
	 * Create AdvancedControllableProperty slider instance
	 *
	 * @param stats extended statistics
	 * @param name name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty slider instance
	 */
	public AdvancedControllableProperty createSlider(Map<String, String> stats, String name, String labelStart, String labelEnd, Float rangeStart, Float rangeEnd, Float initialValue) {
		stats.put(name, initialValue.toString());
		Slider slider = new Slider();
		slider.setLabelStart(labelStart);
		slider.setLabelEnd(labelEnd);
		slider.setRangeStart(rangeStart);
		slider.setRangeEnd(rangeEnd);

		return new AdvancedControllableProperty(name, new Date(), slider, initialValue);
	}

	/**
	 * Formats the given metric name by inserting "CurrentValue" before the first opening parenthesis.
	 * @param metricName The original metric name.
	 * @return The formatted metric name with "CurrentValue" added before the first parenthesis.
	 */
	public String getFormattedMetricNameSlider(String metricName){
		metricName = metricName.replaceFirst("\\(", "CurrentValue(");
		return metricName;
	}

	/**
	 * Formats the metric name based on the given control data.
	 *
	 * @param metric containing the metric and property information.
	 * @param control containing the control name used for formatting.
	 * @return the formatted metric name.
	 */
	public String getFormattedMetricName(DeviceMetric metric, JsonNode control) {
		String[] splitProperty = metric.getProperty().split(QSYSCoreConstant.FORMAT_STRING);
		if (splitProperty.length > 1) {
			return String.format(metric.getMetric(),
					control.get(QSYSCoreConstant.CONTROL_NAME).asText()
							.replace(splitProperty[0], QSYSCoreConstant.EMPTY)
							.replace(splitProperty[1], QSYSCoreConstant.EMPTY));
		}
		return metric.getMetric();
	}
}