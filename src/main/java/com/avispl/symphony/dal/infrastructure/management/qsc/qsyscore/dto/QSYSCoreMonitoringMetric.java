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
public enum QSYSCoreMonitoringMetric {
	SERIAL_NUMBER("SerialNumber"),
	DEVICE_NAME("DeviceName"),
	DEVICE_ID("DeviceID"),
	DEVICE_MODEL("DeviceModel"),
	FIRMWARE_VERSION("FirmwareVersion"),
	UPTIME("Uptime"),
	STATUS("Status"),
	HOSTNAME("Hostname"),
	LAN_A("LAN_A"),
	LAN_B("LAN_B"),
	LAN_A_IP_ADDRESS("LANAIPAddress"),
	LAN_A_SUBNET_MASK("LANASubnetMask"),
	LAN_A_GATEWAY("LANAGateway"),
	LAN_A_MAC_ADDRESS("LANAMACAddress"),
	LAN_B_IP_ADDRESS("LANBIPAddress"),
	LAN_B_SUBNET_MASK("LANBSubnetMask"),
	LAN_B_GATEWAY("LANBGateway"),
	LAN_B_MAC_ADDRESS("LANBMACAddress"),
	DESIGN_NAME("DesignName"),
	DESIGN_CODE("DesignCode"),
	PLATFORM("Platform"),
	STATE("State");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of QSYS Core monitoring metric
	 */
	QSYSCoreMonitoringMetric(String name) {
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