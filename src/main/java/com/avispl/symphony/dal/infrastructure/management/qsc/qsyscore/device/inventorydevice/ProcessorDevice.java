/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.ProcessorDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;

/**
 * ProcessorDevice class to implement monitoring and controlling for Processor device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/21/2023
 * @since 1.0.0
 */
public class ProcessorDevice extends QSYSPeripheralDevice {
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
				ProcessorDeviceMetric processor = ProcessorDeviceMetric.getByProperty(control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (processor == null) {
					continue;
				}
				switch (processor) {
					case PROCESSOR_TEMPERATURE:
					case SYSTEM_TEMPERATURE:
						this.getStats().put(processor.getMetric(), control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE) ? control.get(QSYSCoreConstant.CONTROL_VALUE).asText() : QSYSCoreConstant.DEFAUL_DATA);
						break;
					default:
						this.getStats().put(processor.getMetric(), control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA);
				}
			}
		}
	}
}