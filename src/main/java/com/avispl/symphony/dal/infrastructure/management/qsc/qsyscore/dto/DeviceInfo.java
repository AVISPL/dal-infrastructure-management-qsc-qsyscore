/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceInfo contail DeviceInfoData
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {
	@JsonAlias("data")
	private DeviceInfoData deviceInfoData;

	/**
	 * Retrieves {@link #deviceInfoData}
	 *
	 * @return value of {@link #deviceInfoData}
	 */
	public DeviceInfoData getDeviceInfoData() {
		return deviceInfoData;
	}

	/**
	 * Sets {@link #deviceInfoData} value
	 *
	 * @param deviceInfoData new value of {@link #deviceInfoData}
	 */
	public void setDeviceInfoData(DeviceInfoData deviceInfoData) {
		this.deviceInfoData = deviceInfoData;
	}

	/**
	 * Get value of monitoring property by metric name
	 *
	 * @param systemMetric is QSYSCoreSystemMetric instance
	 * @return String is value of the metric
	 */
	public String getValueByMetricName(QSYSCoreSystemMetric systemMetric) {
		if (null == this.getDeviceInfoData()) {
			deviceInfoData = new DeviceInfoData();
		}
		switch (systemMetric) {
			case DEVICE_ID:
				return deviceInfoData.getNaturalId();
			case SERIAL_NUMBER:
				return deviceInfoData.getSerial();
			case DEVICE_NAME:
				return deviceInfoData.getName();
			case DEVICE_MODEL:
				return deviceInfoData.getModel();
			case FIRMWARE_VERSION:
				Firmware firmware = deviceInfoData.firmware;
				if (firmware == null) {
					firmware = new Firmware();
				}
				return firmware.getBuildName();
			case STATUS:
				Status status = deviceInfoData.status;
				if (status == null) {
					status = new Status();
				}
				return status.getName();
			case UPTIME:
				return String.valueOf(deviceInfoData.getUptime());
			default:
				throw new IllegalArgumentException("The property name doesn't support" + systemMetric.getName());
		}
	}
}