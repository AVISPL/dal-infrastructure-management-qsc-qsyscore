/*
 * Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * PluginDevice save all metric fields and corresponding response fields of the device has type is plugin
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 11/11/2024
 * @since 1.0.2
 */
public class PluginDevice extends QSYSPeripheralDevice {
	/**
	 * Manage are control of device
	 *
	 * @param response String store all information of a control
	 */
	@Override
	public void controlDevice(JsonNode response) {

	}

	/**
	 * Get all monitoring of device
	 *
	 * @param deviceControl list all control of device
	 */
	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		this.getStats().clear();
		this.getAdvancedControllableProperties().clear();
		if (deviceControl.hasNonNull(QSYSCoreConstant.RESULT) && deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			Map<String, String> stats = this.getStats();
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				String key = control.path("Name").asText();
				String value = control.path("Value").asText();
				String type = control.path("Type").asText();
				if ("Text".equals(type) || "Status".equals(type)) {
					value = control.path("String").asText();
				}
				stats.put(key, StringUtils.isNullOrEmpty(value) ? QSYSCoreConstant.DEFAUL_DATA : value);
			}
			super.updateStatusMessage();
		}
	}
}