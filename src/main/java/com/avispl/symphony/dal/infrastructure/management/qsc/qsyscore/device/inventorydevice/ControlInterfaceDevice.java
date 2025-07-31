/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.ControlInterfaceDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * ControlInterfaceDevice class to implement monitoring and controlling for Control Interface device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/18/2023
 * @since 1.0.0
 */
public class ControlInterfaceDevice extends QSYSPeripheralDevice {
	/**
	 * Manage are control of device
	 *
	 */
	@Override
	public void controlDevice(QSYSPeripheralDevice device, String property, String value, String metricName) {

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
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				ControlInterfaceDeviceMetric metric = EnumTypeHandler.getMetricByName(ControlInterfaceDeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (metric == null) {
					continue;
				}
				String value;
				switch (metric) {
					case MEMORY_USAGE:
						value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE) ? control.get(QSYSCoreConstant.CONTROL_VALUE).asText() : QSYSCoreConstant.DEFAUL_DATA;
						try {
							Float floatValue = Float.parseFloat(value);
							floatValue = ((float) Math.ceil(floatValue * 100)) / 100;
							value = String.valueOf(floatValue);
						} catch (Exception e) {
							break;
						}
						break;
					default:
						value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
						break;
				}
				this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
			}
			super.updateStatusMessage();
		}
	}
}