/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * DeviceMonitoring
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
public interface DeviceMonitoring {
	/**
	 * Get all monitoring of device
	 *
	 * @param deviceControl list all control of device
	 */
	void monitoringDevice(JsonNode deviceControl);
}
