/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

/**
 * QSYSCoreMonitoringMetric contail all metric of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
public enum QSYSCoreSystemMetric {

	SERIAL_NUMBER("SerialNumber"),
	DEVICE_NAME("DeviceName"),
	DEVICE_ID("DeviceID"),
	DEVICE_MODEL("DeviceModel"),
	FIRMWARE_VERSION("FirmwareVersion"),
	UPTIME("Uptime"),
	STATUS("Status"),
	REDUNDANCY_ROLE("RedundancyRole"),
	REDUNDANCY_STATE("RedundancyState");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of QSYS Core monitoring metric
	 */
	QSYSCoreSystemMetric(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}