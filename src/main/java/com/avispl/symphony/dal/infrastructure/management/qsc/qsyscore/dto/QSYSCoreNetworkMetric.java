/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

/**
 * QSYSCoreMonitoringMetric contain all metric of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
public enum QSYSCoreNetworkMetric {

	IP_ADDRESS("IPAddress"),
	SUBNET_MASK("SubnetMask"),
	GATEWAY("Gateway"),
	MAC_ADDRESS("MACAddress"),
	HOSTNAME("Hostname");

	private final String name;

	/**
	 * QSYSCoreNetworkMetric constructor
	 *
	 * @param name Name of QSYS Core monitoring metric
	 */
	QSYSCoreNetworkMetric(String name) {
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