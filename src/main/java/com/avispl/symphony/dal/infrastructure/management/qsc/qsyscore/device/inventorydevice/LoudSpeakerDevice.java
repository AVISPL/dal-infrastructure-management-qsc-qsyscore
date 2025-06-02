/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.LoudSpeakerDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * LoudSpeakerDevice class to implement monitoring and controlling for LoudSpeaker device
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.1.0
 */
public class LoudSpeakerDevice extends QSYSPeripheralDevice {
	/**
	 * Manage are control of device
	 *
	 */
	@Override
	public void controlDevice(QSYSPeripheralDevice device, String property, String value, String metricName) {
		try {
			Map<String, String> stats = device.getStats();
			Optional<LoudSpeakerDeviceMetric> optionalMetric = Arrays.stream(LoudSpeakerDeviceMetric.values())
					.filter(metric -> metric != null && metric.getMetric() != null && metric.getMetric().equalsIgnoreCase(property))
					.findFirst();

			if (!optionalMetric.isPresent()) {
				throw new IllegalArgumentException("Error: Can not control this property " + property);
			}
				LoudSpeakerDeviceMetric metric = optionalMetric.get();
				if (METRIC_LIST.contains(metric)) {
					for (Map.Entry<String, String> entry : UNIT_REPLACEMENTS.entrySet()) {
						value = value.replace(entry.getKey(), entry.getValue());
					}
				}
				switch (metric){
					case GAIN:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), stats, createSlider(stats,
								metricName, "-100", "20", -100f, 20f, Float.parseFloat(value)), value);
						stats.put(QSYSCoreConstant.GAIN_CURRENT_VALUE, value);
						break;
					case DELAY:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), stats, createSlider(stats,
								metricName, "0", "2000", 0f, 2000f, Float.parseFloat(value)), value);
						stats.put("DelayCurrentValue(ms)", value);
						break;
					case FULL_RANGE_HIGH_PASS_FREQ:
						value = value.replaceAll(Pattern.quote("Hz") + "\\s*$", "");
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), stats, createSlider(stats,
								metricName, "30", "300", 30f, 300f, Float.parseFloat(value)), value);
						stats.put("Fullrange#HighPassFreqCurrentValue(Hz)", value);
						break;
					default:
						break;
				}
				super.updateStatusMessage();
		} catch (Exception e){
			throw new ResourceNotReachableException("Error occurred while monitoring device control: " + e.getMessage(), e);
		}
	}

	/**
	 * A list of predefined metrics for LoudSpeaker devices.
	 * This list contains various measurement parameters and configurations
	 * relevant to loudspeakers, such as gain, power, voltage, and delay.
	 */
	private static final List<LoudSpeakerDeviceMetric> METRIC_LIST = Arrays.asList(
			LoudSpeakerDeviceMetric.FULL_RANGE_LIMITER,
			LoudSpeakerDeviceMetric.GAIN,
			LoudSpeakerDeviceMetric.FULL_RANGE_HIGH_PASS_FREQ,
			LoudSpeakerDeviceMetric.FULL_RANGE_CURRENT,
			LoudSpeakerDeviceMetric.FULL_RANGE_POWER,
			LoudSpeakerDeviceMetric.DELAY,
			LoudSpeakerDeviceMetric.FULL_RANGE_VOLTAGE
	);

	/**
	 * A mapping of unit symbols to their replacements.
	 * This map is used to remove specific unit symbols from metric values, ensuring consistency
	 * in data processing. Any occurrence of these units will be replaced with an empty string.
	 */
	private static final Map<String, String> UNIT_REPLACEMENTS = ImmutableMap.<String, String>builder()
			.put(QSYSCoreConstant.DB_UNIT, QSYSCoreConstant.EMPTY)
			.put("V", QSYSCoreConstant.EMPTY)
			.put("A", QSYSCoreConstant.EMPTY)
			.put("W", QSYSCoreConstant.EMPTY)
			.put("Hz", QSYSCoreConstant.EMPTY)
			.put("ms", QSYSCoreConstant.EMPTY)
			.put("Ω", QSYSCoreConstant.EMPTY)
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
				LoudSpeakerDeviceMetric metric = EnumTypeHandler.getMetricByName(LoudSpeakerDeviceMetric.class, controlName);
				if (metric == null) {
					continue;
				}
				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				if (METRIC_LIST.contains(metric)) {
					for (Map.Entry<String, String> entry : UNIT_REPLACEMENTS.entrySet()) {
						value = value.replace(entry.getKey(), entry.getValue());
					}
				}
				switch (metric){
					case MUTE:
					case FULL_RANGE_MUTE:
						int statusMute = value.equalsIgnoreCase("unmuted") ? 0 : 1;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metric.getMetric(), statusMute, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(statusMute)
						);
						break;
					case FULL_RANGE_INVERT:
						int status = value.equalsIgnoreCase(QSYSCoreConstant.NORMAL) ? 0 : 1;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metric.getMetric(), status, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(status)
						);
						break;
					case GAIN:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "-100", "20", -100f, 20f, Float.parseFloat(value)), value);
						this.getStats().put(QSYSCoreConstant.GAIN_CURRENT_VALUE, value);
						break;
					case FULL_RANGE_LIMITER:
						this.getStats().put(
								metric.getMetric(),
								StringUtils.isNotNullOrEmpty(value)
										? uppercaseFirstCharacter(value.equals("-0") ? QSYSCoreConstant.ZERO : value)
										: QSYSCoreConstant.DEFAUL_DATA
						);						break;
					case DELAY:
						String delayInMs = convertTimeToMs(value);
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "0", "2000", 0f, 2000f, Float.parseFloat(delayInMs)), delayInMs);
						this.getStats().put("DelayCurrentValue(ms)", delayInMs);
						break;
					case FULL_RANGE_CURRENT:
					case FULL_RANGE_POWER:
					case FULL_RANGE_VOLTAGE:
						if (StringUtils.isNotNullOrEmpty(value)) {
							if (value.matches("^\\.\\d+$")) {
								value = QSYSCoreConstant.ZERO + value;
							}
							this.getStats().put(metric.getMetric(), value);
						} else {
							this.getStats().put(metric.getMetric(), QSYSCoreConstant.DEFAUL_DATA);
						}
						break;
					case FULL_RANGE_HIGH_PASS_FREQ:
						value = value.replaceAll(Pattern.quote("Hz") + "\\s*$", "");
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "30", "300", 30f, 300f, Float.parseFloat(value)), value);
						this.getStats().put("Fullrange#HighPassFreqCurrentValue(Hz)", value);
						break;
					case FULL_RANGE_OPEN_THRESHOLD:
					case FULL_RANGE_SHORT_THRESHOLD:
						String processedValue = value;
						if (!value.contains("---") && !value.startsWith(">")) {
							processedValue = value.replaceAll(Pattern.quote("Ω") + "\\s*$", "");
						}
						this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(processedValue) ? processedValue : QSYSCoreConstant.DEFAUL_DATA);
						break;
					case FULL_RANGE_LOW_PILOT_IMPEDANCE:
					case FULL_RANGE_HIGH_PILOT_IMPEDANCE:
					case FULL_RANGE_IMPEDANCE:
						if(!value.contains("---")){
							value = value.replaceAll(Pattern.quote("kΩ") + "\\s*$", "");
						}
						this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
					case FULL_RANGE_LOW_PILOT_TON:
					case FULL_RANGE_HIGH_PILOT_TON:
					case FULL_RANGE_OPEN:
					case FULL_RANGE_SHORT:
						this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
					case METER_SELECT:
						String[] options = { "Peak", "RMS" };
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createDropdown(metric.getMetric(), options, value), value);
						break;
						case FULL_RANGE_USER_HIGH_PASS:
						String[] userHighPassOptions = { "DEFAULT", "80 Hz HP", "100 Hz HP" };
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createDropdown(metric.getMetric(), userHighPassOptions, value), value);
						break;
					default:
						this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? uppercaseFirstCharacter(value) : QSYSCoreConstant.DEFAUL_DATA);
						break;
				}
			}
			super.updateStatusMessage();
		} catch (Exception e){
			throw new ResourceNotReachableException("Error occurred while monitoring device control: " + e.getMessage(), e);
		}
	}
}