/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * DeviceBehavior indicate behavior of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
public interface DeviceBehavior {
	/**
	 * Controls a device by setting a specified metric to a given value.
	 *
	 * @param device The device to control.
	 * @param metric The metric to be controlled.
	 * @param value The value to set for the metric.
	 * @param metricName The name of the metric.
	 */
	void controlDevice(QSYSPeripheralDevice device, String metric, String value, String metricName);

	/**
	 * Get all monitoring of device
	 *
	 * @param deviceControl list all control of device
	 */
	void monitoringDevice(JsonNode deviceControl);
}
