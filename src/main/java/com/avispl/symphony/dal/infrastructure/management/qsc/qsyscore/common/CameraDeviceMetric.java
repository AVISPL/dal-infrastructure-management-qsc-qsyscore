/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * CameraDeivceMetric save all metric fields and corresponding response fields of the Camera device metric
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/30/2023
 * @since 1.0.0
 */
public enum CameraDeviceMetric {
	STATUS("Status", "camera.status"),
	STATUS_LED("StatusLed", "camera.status.led"),
	IP_STREAMING_STATUS("IpStreamingStatus", "streaming.network"),
	HARDWARE_MODEL("HardwareDetail#Model", "textc.model"),
	HARDWARE_MAC("HardwareDetail#MACAddress", "textc.mac"),
	HARDWARE_SERIAL("HardwareDetail#SerialNumber", "textc.serial");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	CameraDeviceMetric(String metric, String property) {
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