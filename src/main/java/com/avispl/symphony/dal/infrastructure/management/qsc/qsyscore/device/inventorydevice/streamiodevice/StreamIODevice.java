/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.streamiodevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.StreamIODeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * StreamIODevice class to implement monitoring and controlling for Stream IO device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/22/2023
 * @since 1.0.0
 */
public abstract class StreamIODevice extends QSYSPeripheralDevice {
	/**
	 * Get all monitoring of device
	 *
	 * @param deviceControl list all control of device
	 */
	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		if (deviceControl.hasNonNull(QSYSCoreConstant.RESULT) && deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				StreamIODeviceMetric metric = EnumTypeHandler.getMetricByPropertyName(StreamIODeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (metric == null) {
					continue;
				}
				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
			}
		}
	}
}