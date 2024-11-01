/*
 * Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * PDUDeviceMetric save all metric fields and corresponding response fields of the PDU device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 11/01/2024
 * @since 1.0.2
 */
public enum PDUDeviceMetric {

	IP_ADDRESS("IPAddress", "IPAddress"),
	POWER_CONTROL("Power", "Power"),
	POWER_STATE("PowerState", "PowerState"),
	STATUS("Status", "Status"),
	USER_NAME("UserName", "UserName");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that shows on UI
	 * @param property corresponding response field
	 */
	PDUDeviceMetric(String metric, String property) {
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
	public static PDUDeviceMetric getByMetric(String metric) {
		for (PDUDeviceMetric controllingMetric : values()) {
			if (Objects.equals(controllingMetric.getMetric(), metric)) {
				return controllingMetric;
			}
		}
		throw new IllegalArgumentException("Cannot find the enum with metric: " + metric);
	}

}
