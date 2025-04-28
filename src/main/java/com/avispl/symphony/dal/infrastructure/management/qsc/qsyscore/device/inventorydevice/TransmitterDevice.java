/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.TransmitterDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * TransmitterDevice
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.0
 */
public class TransmitterDevice extends QSYSPeripheralDevice {
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
		try{
			this.getStats().clear();
			this.getAdvancedControllableProperties().clear();
			List<TransmitterDeviceMetric> metrics = Arrays.asList(
					TransmitterDeviceMetric.CHANNEL_GAIN,
					TransmitterDeviceMetric.CHANNEL_PEAK_OUTPUT_LEVEL
			);
			if (!deviceControl.hasNonNull(QSYSCoreConstant.RESULT) ||
					!deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
				return;
			}
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				TransmitterDeviceMetric metric = EnumTypeHandler.getMetricByPropertyName(TransmitterDeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (metric == null) {
					continue;
				}
				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				if (metrics.contains(metric)) {
					value = value.replace(QSYSCoreConstant.DB_UNIT, QSYSCoreConstant.EMPTY);
				}
				String metricName = getFormattedMetricName(metric, control);
				switch (metric){
					case ENABLE:
					case CHANNEL_CLIP_HOLD:
					case CHANNEL_MUTE:
						int status = value.equalsIgnoreCase("enabled") || value.equalsIgnoreCase("true")
								|| value.equalsIgnoreCase("muted") ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, status, "Off", "On"),
								String.valueOf(status)
						);
						break;
					case CHANNEL_INVERT:
						int stateInvert = value.equalsIgnoreCase("normal") ? 0 : 1;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, stateInvert, "Off", "On"),
								String.valueOf(stateInvert)
						);
						break;
					case STATUS_LED:
					case CHANNEL_CLIP:
						this.getStats().put(metricName, StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
					case CHANNEL_GAIN:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metricName, "-100", "20", -100f, 20f, Float.parseFloat(value)), value);
						this.getStats().put(getFormattedMetricNameSlider(metricName), value);
						break;
					default:
						this.getStats().put(metricName, StringUtils.isNotNullOrEmpty(value) ? uppercaseFirstCharacter(value) : QSYSCoreConstant.DEFAUL_DATA);
						break;
				}
			}
			super.updateStatusMessage();
		} catch (Exception e){
			throw new ResourceNotReachableException("Error occurred while monitoring device control: " + e.getMessage(), e);
		}
	}

}