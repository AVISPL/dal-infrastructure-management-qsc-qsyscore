/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * PluginDeviceMetric save all metric fields and corresponding response fields of the Plugin device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 11/13/2024
 * @since 1.0.0
 */
public enum PluginDeviceMetric {
	STATUS("Status", "Status"),
	MODEL("Model", "Model"),
	SERIAL_NUMBER("SerialNumber", "SerialNumber"),
	MAC_ADDRESS("MACAddress", "MACAddress"),
	IP_ADDRESS("IPAddress", "IPAddress"),
	DEVICE_NAME("DeviceName", "DeviceName"),
	FIRMWARE_VERSION("FirmwareVersion", "DeviceFirmware");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	PluginDeviceMetric(String metric, String property) {
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
}
