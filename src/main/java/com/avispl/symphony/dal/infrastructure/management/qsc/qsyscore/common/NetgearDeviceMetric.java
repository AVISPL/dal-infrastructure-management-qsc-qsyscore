/*
 * Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * NetgearDeviceMetric save all metric fields and corresponding response fields of the NetgearDeviceMetric device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 11/01/2024
 * @since 1.0.2
 */
public enum NetgearDeviceMetric {

	SYSTEM_NAME("SystemName", "DeviceName"),
	MODEL("Model", "Model"),
	SERIAL_NUMBER("SerialNumber", "SerialNumber"),
	MAC_ADDRESS("MACAddress", "MACAddress"),
	FIRMWARE_VERSION("FirmwareVersion", "DeviceFirmware"),
	UPTIME("Uptime", "DeviceUptime"),
	CONFIGURATION_VERSION("ConfigurationVersion", "DeviceConfiguration"),
	STATUS("Status", "Status"),
	IP_ADDRESS("IPAddress", "IPAddress"),
	USER_NAME("UserName", "UserName"),
	PORT_1("Port01#Description", "Description 1"),
	PORT_2("Port02#Description", "Description 2"),
	PORT_3("Port03#Description", "Description 3"),
	PORT_4("Port04#Description", "Description 4"),
	PORT_5("Port05#Description", "Description 5"),
	PORT_6("Port06#Description", "Description 6"),
	PORT_7("Port07#Description", "Description 7"),
	PORT_8("Port08#Description", "Description 8"),
	PORT_9("Port09#Description", "Description 9"),
	PORT_10("Port10#Description", "Description 10"),
	PORT_11("Port11#Description", "Description 11"),
	PORT_12("Port12#Description", "Description 12"),
	CONNECTED_1("Port01#Connected", "Connected 1"),
	CONNECTED_2("Port02#Connected", "Connected 2"),
	CONNECTED_3("Port03#Connected", "Connected 3"),
	CONNECTED_4("Port04#Connected", "Connected 4"),
	CONNECTED_5("Port05#Connected", "Connected 5"),
	CONNECTED_6("Port06#Connected", "Connected 6"),
	CONNECTED_7("Port07#Connected", "Connected 7"),
	CONNECTED_8("Port08#Connected", "Connected 8"),
	CONNECTED_9("Port09#Connected", "Connected 9"),
	CONNECTED_10("Port10#Connected", "Connected 10"),
	CONNECTED_11("Port11#Connected", "Connected 11"),
	CONNECTED_12("Port12#Connected", "Connected 12"),
	POWERED_1("Port01#Powered", "ConnectedPowered 1"),
	POWERED_2("Port02#Powered", "ConnectedPowered 2"),
	POWERED_3("Port03#Powered", "ConnectedPowered 3"),
	POWERED_4("Port04#Powered", "ConnectedPowered 4"),
	POWERED_5("Port05#Powered", "ConnectedPowered 5"),
	POWERED_6("Port06#Powered", "ConnectedPowered 6"),
	POWERED_7("Port07#Powered", "ConnectedPowered 7"),
	POWERED_8("Port08#Powered", "ConnectedPowered 8"),
	POWERED_9("Port09#Powered", "ConnectedPowered 9"),
	POWERED_10("Port10#Powered", "ConnectedPowered 10"),
	POWERED_11("Port11#Powered", "ConnectedPowered 11"),
	POWERED_12("Port12#Powered", "ConnectedPowered 12"),
	ERROR_1("Port01#Error", "Error 1"),
	ERROR_2("Port02#Error", "Error 2"),
	ERROR_3("Port03#Error", "Error 3"),
	ERROR_4("Port04#Error", "Error 4"),
	ERROR_5("Port05#Error", "Error 5"),
	ERROR_6("Port06#Error", "Error 6"),
	ERROR_7("Port07#Error", "Error 7"),
	ERROR_8("Port08#Error", "Error 8"),
	ERROR_9("Port09#Error", "Error 9"),
	ERROR_10("Port10#Error", "Error 10"),
	ERROR_11("Port11#Error", "Error 11"),
	ERROR_12("Port12#Error", "Error 12"),
	TRUNK_1("Port01#Trunk", "Trunk 1"),
	TRUNK_2("Port02#Trunk", "Trunk 2"),
	TRUNK_3("Port03#Trunk", "Trunk 3"),
	TRUNK_4("Port04#Trunk", "Trunk 4"),
	TRUNK_5("Port05#Trunk", "Trunk 5"),
	TRUNK_6("Port06#Trunk", "Trunk 6"),
	TRUNK_7("Port07#Trunk", "Trunk 7"),
	TRUNK_8("Port08#Trunk", "Trunk 8"),
	TRUNK_9("Port09#Trunk", "Trunk 9"),
	TRUNK_10("Port10#Trunk", "Trunk 10"),
	TRUNK_11("Port11#Trunk", "Trunk 11"),
	TRUNK_12("Port12#Trunk", "Trunk 12");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that shows on UI
	 * @param property corresponding response field
	 */
	NetgearDeviceMetric(String metric, String property) {
		this.metric = metric;
		this.property = property;
	}

	/**
	 * Retrieves {@link #metric}
	 *
	 * @return value of {@link #metric}
	 */
	public String getMetric() {
		return metric;
	}

	/**
	 * Retrieves {@link #property}
	 *
	 * @return value of {@link #property}
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Get metric of metric from MiddleAtlanticMetric
	 *
	 * @param metric metric of metric
	 * @return Enum of MiddleAtlanticMetric
	 */
	public static NetgearDeviceMetric getByMetric(String metric) {
		for (NetgearDeviceMetric controllingMetric : values()) {
			if (Objects.equals(controllingMetric.getMetric(), metric)) {
				return controllingMetric;
			}
		}
		throw new IllegalArgumentException("Cannot find the enum with metric: " + metric);
	}
}
