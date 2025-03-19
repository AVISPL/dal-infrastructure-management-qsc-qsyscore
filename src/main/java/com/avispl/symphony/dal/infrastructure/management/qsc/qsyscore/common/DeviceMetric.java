/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * Represents a device metric that defines properties and metric names.
 * This interface is used for different types of device metrics, providing methods to
 * retrieve the property name and the corresponding metric identifier.
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.0
 */
public interface DeviceMetric {
	/**
	 * Retrieves the property associated with the metric.
	 */
	String getProperty();

	/**
	 * Retrieves the metric name or identifier.
	 */
	String getMetric();
}
