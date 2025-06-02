/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.VideoSourceDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * VideoSourceDevice class to implement monitoring and controlling for Video Source device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/18/2023
 * @since 1.0.0
 */
public class VideoSourceDevice extends QSYSPeripheralDevice {
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
				VideoSourceDeviceMetric metric = EnumTypeHandler.getMetricByPropertyName(VideoSourceDeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());

				if (metric == null) {
					continue;
				}

				String[] splitProperty = metric.getProperty().split(QSYSCoreConstant.FORMAT_STRING);
				String value = QSYSCoreConstant.DEFAUL_DATA;
				JsonNode jsonNode = control.get(QSYSCoreConstant.CONTROL_VALUE_STRING);
				if (jsonNode != null && StringUtils.isNotNullOrEmpty(jsonNode.asText())) {
					value = jsonNode.asText();
				}

				if (splitProperty.length > 1) {
					String metricName = String.format(metric.getMetric(),
							control.get(QSYSCoreConstant.CONTROL_NAME).asText().replace(splitProperty[0], QSYSCoreConstant.EMPTY).replace(splitProperty[1], QSYSCoreConstant.EMPTY));

					if (VideoSourceDeviceMetric.CHANNEL_PEAK_INPUT_LEVEL.getMetric().equalsIgnoreCase(metric.getMetric())) {
						value = value.replace(QSYSCoreConstant.DB_UNIT, QSYSCoreConstant.EMPTY);
					}

					this.getStats().put(metricName, value);
				} else {
					this.getStats().put(metric.getMetric(), value);
				}
			}
			super.updateStatusMessage();
		}
	}
}