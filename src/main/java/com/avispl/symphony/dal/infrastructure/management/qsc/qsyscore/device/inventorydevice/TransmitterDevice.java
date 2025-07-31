/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.TransmitterDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * TransmitterDevice class to implement monitoring and controlling for Transmitter device
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.1.0
 */
public class TransmitterDevice extends QSYSPeripheralDevice {
	/**
	 * Manage are control of device
	 *
	 */
	@Override
	public void controlDevice(QSYSPeripheralDevice device, String property, String value, String metricName) {
		try{
			Map<String, String> stats = Optional.ofNullable(device.getStats()).orElse(new HashMap<>());
			Optional<TransmitterDeviceMetric> optionalMetric = Arrays.stream(TransmitterDeviceMetric.values())
					.filter(metric -> metric != null && metric.getMetric() != null && metric.getMetric().equalsIgnoreCase(property))
					.findFirst();

			if (!optionalMetric.isPresent()) {
				throw new IllegalArgumentException("Error: Can not control this property " + property);
			}
				TransmitterDeviceMetric metric = optionalMetric.get();
				List<TransmitterDeviceMetric> metricsRemoveDb = Arrays.asList(
						TransmitterDeviceMetric.CHANNEL_GAIN,
						TransmitterDeviceMetric.CHANNEL_PEAK_OUTPUT_LEVEL
				);
				if (metricsRemoveDb.contains(metric)) {
					value = value.replace(QSYSCoreConstant.DB_UNIT, QSYSCoreConstant.EMPTY);
				}
				switch (metric){
					case CHANNEL_GAIN:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), stats, createSlider(stats,
								metricName, "-100", "20", -100f, 20f, Float.parseFloat(value)), value);
						stats.put(getFormattedMetricNameSlider(metricName), value);
						break;
					default:
						break;
				}
		} catch (Exception e){
			throw new ResourceNotReachableException("Error occurred while monitoring device control: " + e.getMessage(), e);
		}
	}

	/**
	 * Get all monitoring of device
	 *
	 * @param deviceControl list all control of device
	 */
	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		try{
			String deviceName = deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROL_NAME).asText();
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
				String controlName = Optional.ofNullable(control.get(QSYSCoreConstant.CONTROL_NAME))
						.map(JsonNode::asText)
						.orElse(null);
				if (controlName == null) {
					continue;
				}
				TransmitterDeviceMetric metric = EnumTypeHandler.getMetricByPropertyName(TransmitterDeviceMetric.class, controlName);
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
						int status = value.equalsIgnoreCase(QSYSCoreConstant.OK_STATUS) || value.equalsIgnoreCase(QSYSCoreConstant.ENABLED) || value.equalsIgnoreCase(QSYSCoreConstant.TRUE) ? 1 : 0;
						if(QSYSCoreConstant.Q_LAN_TX_1_DEVICE.equalsIgnoreCase(deviceName)){
							this.getStats().remove(metricName);
						} else {
							addAdvancedControlProperties(
									this.getAdvancedControllableProperties(),
									getStats(),
									createSwitch(metricName, status, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
									String.valueOf(status)
							);
						}
						break;
					case CHANNEL_CLIP_HOLD:
					case CHANNEL_MUTE:
						int statusMute = value.equalsIgnoreCase(QSYSCoreConstant.ENABLED) || value.equalsIgnoreCase(QSYSCoreConstant.TRUE)
								|| value.equalsIgnoreCase(QSYSCoreConstant.MUTED) ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, statusMute, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(statusMute)
						);
						break;
					case INTERFACE:
					case NETWORK_TX_BUFFER:
						if(QSYSCoreConstant.Q_LAN_TX_1_DEVICE.equals(deviceName)){
							this.getStats().remove(metricName);
						} else {
							this.getStats().put(metricName, StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						}
						break;
					case CHANNEL_INVERT:
						int stateInvert = value.equalsIgnoreCase(QSYSCoreConstant.NORMAL) ? 0 : 1;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, stateInvert, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(stateInvert)
						);
						break;
					case STATUS_LED:
					case CHANNEL_CLIP:
					case STREAM_NAME:
					case PRIMARY_OK:
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