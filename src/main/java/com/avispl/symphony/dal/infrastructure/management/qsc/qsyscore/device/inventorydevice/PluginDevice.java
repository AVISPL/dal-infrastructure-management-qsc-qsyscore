/*
 * Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.PluginDeviceMetric;
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
			for (PluginDeviceMetric pluginDevice : PluginDeviceMetric.values()) {
				String value = getValueByName(deviceControl, pluginDevice.getProperty());
				if (StringUtils.isNullOrEmpty(value)) {
					value = QSYSCoreConstant.DEFAUL_DATA;
				}
				this.getStats().put(pluginDevice.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
			}
			super.updateStatusMessage();
		}
	}

	/**
	 * get value of response by name
	 *
	 * @param deviceControl is jsonNode value of response
	 * @param property is the property name
	 * @return string is value of property or null
	 */
	private String getValueByName(JsonNode deviceControl, String property) {
		for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
			String name = control.get(QSYSCoreConstant.CONTROL_NAME).asText();
			if (property.equalsIgnoreCase(name)) {
				return control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
			}
		}
		return null;
	}
}