/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * StreamIODeviceMetric save all metric fields and corresponding response fields of the Stream IO device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/23/2023
 * @since 1.0.0
 */
public enum StreamIODeviceMetric {
	DANTE_NAME("DanteName", "dante.name.property"),
	DANTE_INTERFACE("DanteInterface", "dante.interface.property");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	StreamIODeviceMetric(String metric, String property) {
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
