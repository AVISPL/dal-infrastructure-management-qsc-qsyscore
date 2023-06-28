/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.VideoIODeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * VideoIODevice class to implement monitoring and controlling for Video IO device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/18/2023
 * @since 1.0.0
 */
public class VideoIODevice extends QSYSPeripheralDevice {
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
		if (deviceControl.hasNonNull(QSYSCoreConstant.RESULT) && deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				VideoIODeviceMetric metric = VideoIODeviceMetric.getByProperty(control.get(QSYSCoreConstant.CONTROL_NAME).asText());

				if (metric == null) {
					continue;
				}

				String[] splitProperty = metric.getProperty().split(QSYSCoreConstant.FORMAT_STRING);
				if (splitProperty.length > 1) {
					String metricName = String.format(metric.getMetric(),
							control.get(QSYSCoreConstant.CONTROL_NAME).asText().replace(splitProperty[0], QSYSCoreConstant.EMPTY).replace(splitProperty[1], QSYSCoreConstant.EMPTY));
					String value=control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
					this.getStats().put(metricName, StringUtils.isNotNullOrEmpty(value)?value:QSYSCoreConstant.DEFAUL_DATA);
				} else {
					String value=control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
					this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value)?value:QSYSCoreConstant.DEFAUL_DATA);
				}
			}
		}
	}
}