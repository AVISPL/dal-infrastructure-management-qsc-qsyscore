/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.CameraDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * CameraDevice
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/30/2023
 * @since 1.0.0
 */
public class CameraDevice extends QSYSPeripheralDevice {
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
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				CameraDeviceMetric cameraDeviceMetric = EnumTypeHandler.getMetricByName(CameraDeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (cameraDeviceMetric == null) {
					continue;
				}
				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				this.getStats().put(cameraDeviceMetric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
			}
			super.updateStatusMessage();
		}
	}
}