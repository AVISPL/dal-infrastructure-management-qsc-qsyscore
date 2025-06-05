/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.AmplifierDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * AmplifierDevice class to implement monitoring and controlling for Amplifier device
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.1.0
 */
public class AmplifierDevice extends QSYSPeripheralDevice {
	/**
	 * Manage are control of device
	 *
	 */
	@Override
	public void controlDevice(QSYSPeripheralDevice device, String property, String value, String metricName) {
			try {
				Map<String, String> stats = Optional.ofNullable(device.getStats()).orElse(new HashMap<>());
				Optional<AmplifierDeviceMetric> optionalMetric = Arrays.stream(AmplifierDeviceMetric.values())
						.filter(metric -> metric != null && metric.getMetric() != null && metric.getMetric().equalsIgnoreCase(property))
						.findFirst();
				if (!optionalMetric.isPresent()) {
					throw new IllegalArgumentException("Error: Can not control this property " + property);
				}
				AmplifierDeviceMetric metric = optionalMetric.get();
				if (METRIC_LIST.contains(metric)) {
					for (Map.Entry<String, String> entry : UNIT_REPLACEMENTS.entrySet()) {
						value = value.replace(entry.getKey(), entry.getValue());
					}
				}
				switch (metric){
					case CHANNEL_GAIN:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), stats, createSlider(stats,
								metricName, "-100", "20", -100f, 20f, Float.parseFloat(value)), value);
						stats.put(getFormattedMetricNameSlider(metricName), value);
						break;
					case POWER_SAVE_THRESHOLD:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), stats, createSlider(stats,
								metricName, "-99", "-50", -99.0f, -50.0f, Float.parseFloat(value)), value);
						stats.put(getFormattedMetricNameSlider(metricName), value);
						break;
					case POWER_SAVE_TIMEOUT:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), stats, createSlider(stats,
								metricName, "1", "99", 1.0f, 99.0f, Float.parseFloat(value)), value);
						stats.put("PowerManagement#PowerSaveTimeoutCurrentValue", value);
						break;
					default:
						break;
				}
				super.updateStatusMessage();
			}catch (Exception e){
				throw new ResourceNotReachableException("Error occurred while monitoring device control: " + e.getMessage(), e);
			}
	}

	/**
	 * A list of predefined amplifier device metrics.
	 * This list contains various metrics related to amplifier devices, such as
	 * gain levels, input status, temperature, voltage, current, and power measurements.
	 * These metrics are used for monitoring and controlling amplifier performance.
	 * </p>
	 */
	private static final List<AmplifierDeviceMetric> METRIC_LIST = Arrays.asList(
			AmplifierDeviceMetric.CHANNEL_GAIN,
			AmplifierDeviceMetric.CHANNEL_INPUT,
			AmplifierDeviceMetric.CHANNEL_HEAD_ROOM,
			AmplifierDeviceMetric.CHANNEL_OUTPUT,
			AmplifierDeviceMetric.POWER_SAVE_THRESHOLD,
			AmplifierDeviceMetric.CHANNEL_TEMP,
			AmplifierDeviceMetric.AC_CURRENT,
			AmplifierDeviceMetric.CHANNEL_CURRENT,
			AmplifierDeviceMetric.AC_VOLTAGE,
			AmplifierDeviceMetric.VRAIL_1,
			AmplifierDeviceMetric.VRAIL_2,
			AmplifierDeviceMetric.CHANNEL_VOLTAGE,
			AmplifierDeviceMetric.CHANNEL_POWER,
			AmplifierDeviceMetric.PSU_TEMP
	);

	/**
	 * A mapping of unit replacements used for standardizing metric values.
	 * This map is used to replace specific measurement units (e.g., "V", "A", "W", "Hz", "ms", "°C", "RMS")
	 * with an empty string. This helps in normalizing metric names or values by removing unit indicators.
	 */
	private static final Map<String, String> UNIT_REPLACEMENTS = ImmutableMap.<String, String>builder()
			.put(QSYSCoreConstant.DB_UNIT, QSYSCoreConstant.EMPTY)
			.put("V", QSYSCoreConstant.EMPTY)
			.put("A", QSYSCoreConstant.EMPTY)
			.put("W", QSYSCoreConstant.EMPTY)
			.put("Hz", QSYSCoreConstant.EMPTY)
			.put("ms", QSYSCoreConstant.EMPTY)
			.put("°C", QSYSCoreConstant.EMPTY)
			.put("C", QSYSCoreConstant.EMPTY)
			.put("RMS", QSYSCoreConstant.EMPTY)
			.build();

	/**
	 * Get all monitoring of device
	 *
	 * @param deviceControl list all control of device
	 */
	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		try {
			this.getStats().clear();
			this.getAdvancedControllableProperties().clear();

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
				AmplifierDeviceMetric metric = EnumTypeHandler.getMetricByPropertyName(AmplifierDeviceMetric.class, controlName);
				if (metric == null) {
					continue;
				}

				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				if (METRIC_LIST.contains(metric)) {
					for (Entry<String, String> entry : UNIT_REPLACEMENTS.entrySet()) {
						value = value.replace(entry.getKey(), entry.getValue());
					}
				}
				String metricName = getFormattedMetricNameAmplifier(metric, control);
				switch (metric){
					case ON_STANDBY:
						int standByStatus = value.equalsIgnoreCase("on") ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, standByStatus, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(standByStatus)
						);
						break;
					case GAIN_LOCK:
					case MUTE_LOCK:
					case DISABLE_POWER_SAVE:
						int status = value.equalsIgnoreCase(QSYSCoreConstant.ENABLED) || value.equalsIgnoreCase(QSYSCoreConstant.TRUE) ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, status, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(status)
						);
						break;
					case POWER_METERS:
						int meterValue = value.equalsIgnoreCase(QSYSCoreConstant.ENABLED) ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, meterValue, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(meterValue)
						);
						break;
					case MUTE_ALL:
					case FRONT_PANEL_DISABLE:
					case CHANNEL_MUTE:
						int mute = value.equalsIgnoreCase(QSYSCoreConstant.MUTED) ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, mute, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(mute)
						);
						break;
					case METER_SELECT:
						String[] options = { "Peak", "RMS" };
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createDropdown(metricName, options, value), value);
						break;
					case CHANNEL_GAIN:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metricName, "-100", "20", -100f, 20f, Float.parseFloat(value)), value);
						this.getStats().put(getFormattedMetricNameSlider(metricName), value);
						break;
					case POWER_SAVE_THRESHOLD:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metricName, "-99", "-50", -99.0f, -50.0f, Float.parseFloat(value)), value);
						this.getStats().put(getFormattedMetricNameSlider(metricName), value);
						break;
					case POWER_SAVE_TIMEOUT:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metricName, "1", "99", 1.0f, 99.0f, Float.parseFloat(value)), value);
						this.getStats().put("PowerManagement#PowerSaveTimeoutCurrentValue", value);
						break;
					case CHANNEL_VOLTAGE:
					case CHANNEL_CURRENT:
					case CHANNEL_POWER:
					case AC_CURRENT:
					case AC_VOLTAGE:
						if (StringUtils.isNotNullOrEmpty(value)) {
							if (value.matches("^\\.\\d+$")) {
								value = QSYSCoreConstant.ZERO + value;
							}
							this.getStats().put(metricName, value);
						} else {
							this.getStats().put(metricName, QSYSCoreConstant.DEFAUL_DATA);
						}
						break;
					case CHANNEL_DAC_LIMIT:
					case CHANNEL_LIMIT:
					case CHANNEL_OVER_CURRENT:
					case CHANNEL_POWER_SAVE:
					case CHANNEL_PROTECT:
					case CHANNEL_SHORT:
						this.getStats().put(metricName, StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
					default:
						this.getStats().put(metricName, StringUtils.isNotNullOrEmpty(value) ? uppercaseFirstCharacter(value) : QSYSCoreConstant.DEFAUL_DATA);
						break;
				}
			}
			super.updateStatusMessage();
		}catch (Exception e){
			throw new ResourceNotReachableException("Error occurred while monitoring device control: " + e.getMessage(), e);
		}
	}

	/**
	 * Formats the metric name based on the given control data for Amplifier.
	 *
	 * @param metric containing the metric and property information.
	 * @param control containing the control name used for formatting.
	 * @return the formatted metric name.
	 */
	public String getFormattedMetricNameAmplifier(AmplifierDeviceMetric metric, JsonNode control) {
		String[] splitProperty = metric.getProperty().split(QSYSCoreConstant.FORMAT_STRING);

		if (splitProperty.length > 1) {
			String controlName = control.get(QSYSCoreConstant.CONTROL_NAME).asText();

			controlName = String.format(metric.getMetric(),
					controlName.replace(splitProperty[0], QSYSCoreConstant.EMPTY)
							.replace(splitProperty[1], QSYSCoreConstant.EMPTY));
			return convertChannelNumbersToLetters(controlName);
		}

		return metric.getMetric();
	}

	/**
	 * Formats the metric name based on the given control data for Amplifier.
	 * Replace Channel group numbers (e.g., 1, 2, 3, ...) with corresponding letters (A, B, C, ...).
	 *
	 * @param input containing the name of the metric.
	 * @return formatted metric name with channel numbers replaced by letters.
	 */
	private String convertChannelNumbersToLetters(String input) {
		Matcher matcher = Pattern.compile("Channel(\\d+)").matcher(input);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			int channelNumber = Integer.parseInt(matcher.group(1));

			if (channelNumber >= 1 && channelNumber <= 26) {
				char letter = (char) ('A' + channelNumber - 1);
				matcher.appendReplacement(result, "Channel" + letter);
			} else {
				matcher.appendReplacement(result, matcher.group(0));
			}
		}
		matcher.appendTail(result);
		return result.toString();
	}

}