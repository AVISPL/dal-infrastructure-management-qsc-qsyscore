/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.MonitoringProxyDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * MonitoringProxyDevice class to implement monitoring and controlling for Monitoring proxy device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/29/2023
 * @since 1.0.0
 */
public class MonitoringProxyDevice extends QSYSPeripheralDevice {

	/**
	 * Manage are control of device
	 *
	 */
	@Override
	public void controlDevice(QSYSPeripheralDevice device, String property, String value, String metricName) {

	}

	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		this.getStats().clear();
		this.getAdvancedControllableProperties().clear();
		if (deviceControl.hasNonNull(QSYSCoreConstant.RESULT) && deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				String nameControl = control.get(QSYSCoreConstant.CONTROL_NAME).asText();
				if (nameControl.equals(MonitoringProxyDeviceMetric.STATUS.getProperty())) {
					String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
					this.getStats().put(MonitoringProxyDeviceMetric.STATUS.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
				}
			}
			super.updateStatusMessage();
		}
	}
}