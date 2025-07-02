/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;

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
				String type = Optional.ofNullable(control.get(QSYSCoreConstant.CONTROL_TYPE)).map(JsonNode::asText).orElse("");
				String value;

				switch (type) {
					case QSYSCoreConstant.TYPE_FLOAT:
						value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE) ? String.valueOf(control.get(QSYSCoreConstant.CONTROL_VALUE).asDouble()) : QSYSCoreConstant.DEFAUL_DATA;
						break;
					case QSYSCoreConstant.TYPE_BOOLEAN:
						value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE) ? (control.get(QSYSCoreConstant.CONTROL_VALUE).asBoolean() ? QSYSCoreConstant.TRUE : QSYSCoreConstant.FALSE) : QSYSCoreConstant.DEFAUL_DATA;
						break;
					default:
						value = Optional.ofNullable(control.get(QSYSCoreConstant.CONTROL_VALUE_STRING)).map(JsonNode::asText).orElse(QSYSCoreConstant.DEFAUL_DATA);
						break;
				}

				switch (metric){
					case FULL_RANGE_HIGH_PILOT_TON:
					case FULL_RANGE_LOW_PILOT_TON:
						this.getStats().put(metric.getMetric(), value.equalsIgnoreCase(QSYSCoreConstant.FALSE) ? uppercaseFirstCharacter("disabled") : uppercaseFirstCharacter("enabled"));
						break;
					case MUTE:
					case FULL_RANGE_MUTE:
					case FULL_RANGE_INVERT:
						int statusMute = value.equalsIgnoreCase(QSYSCoreConstant.FALSE) ? 0 : 1;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metric.getMetric(), statusMute, QSYSCoreConstant.OFF, QSYSCoreConstant.ON),
								String.valueOf(statusMute)
						);
						break;
					case GAIN:
						String gainValue = roundToDecimalPlaces(control.get(QSYSCoreConstant.CONTROL_VALUE), 1);
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "-100", "20", -100f, 20f, Float.parseFloat(gainValue)), gainValue);
						this.getStats().put(QSYSCoreConstant.GAIN_CURRENT_VALUE, gainValue);
						break;
					case FULL_RANGE_LIMITER:
						JsonNode valueNode = control.get(QSYSCoreConstant.CONTROL_VALUE);
						String formattedLimiterValue;
						if (valueNode != null && valueNode.isNumber()) {
							double rawValue = valueNode.asDouble();

							formattedLimiterValue = Math.abs(rawValue) < 1e-5
									? QSYSCoreConstant.ZERO
									: String.format(Locale.US, "%.2f", rawValue);
						} else {
							formattedLimiterValue = QSYSCoreConstant.DEFAUL_DATA;
						}

						this.getStats().put(metric.getMetric(), formattedLimiterValue);
						break;
					case DELAY:
						JsonNode delayNode = control.get(QSYSCoreConstant.CONTROL_VALUE);
						String delayMs;
						if (delayNode != null && delayNode.isNumber()) {
							double valueSec = delayNode.asDouble();
							double valueInMs = valueSec * 1000;
							BigDecimal rounded = new BigDecimal(Double.toString(valueInMs))
									.setScale(1, RoundingMode.HALF_UP)
									.stripTrailingZeros();
							delayMs = rounded.toPlainString();
						} else {
							delayMs = QSYSCoreConstant.NOT_AVAILABLE;
						}
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "0", "2000", 0f, 2000f, Float.parseFloat(delayMs)), delayMs);
						this.getStats().put("DelayCurrentValue(ms)", delayMs);
						break;
					case FULL_RANGE_IMPEDANCE:
					case FULL_RANGE_HIGH_PILOT_IMPEDANCE:
					case FULL_RANGE_OPEN_THRESHOLD:
					case FULL_RANGE_SHORT_THRESHOLD:
					case FULL_RANGE_LOW_PILOT_IMPEDANCE:
					case FULL_RANGE_CURRENT:
					case FULL_RANGE_POWER:
					case FULL_RANGE_VOLTAGE:
						String rounded = roundToDecimalPlaces(control.get(QSYSCoreConstant.CONTROL_VALUE), 1);
						this.getStats().put(metric.getMetric(), rounded);
						break;
					case FULL_RANGE_HIGH_PASS_FREQ:
						String highPassValue = roundToDecimalPlaces(control.get(QSYSCoreConstant.CONTROL_VALUE), 1);
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "30", "300", 30f, 300f, Float.parseFloat(highPassValue)), highPassValue);
						this.getStats().put("Fullrange#HighPassFreqCurrentValue(Hz)", highPassValue);
						break;
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

	/**
	 * Rounds a numeric value from a {@link JsonNode} to a specified number of decimal places.
	 *
	 * @param valueNode      the JSON node containing the numeric value
	 * @param decimalPlaces  the number of decimal places to round to
	 * @return a string representation of the rounded number, or a default value if invalid
	 */
	private String roundToDecimalPlaces(JsonNode valueNode, int decimalPlaces) {
		if (valueNode == null || !valueNode.isNumber()) {
			return QSYSCoreConstant.DEFAUL_DATA;
		}
		double value = valueNode.asDouble();

		if (Math.abs(value) < 1e-6) {
			return QSYSCoreConstant.ZERO;
		}
		BigDecimal rounded = new BigDecimal(Double.toString(value)).setScale(decimalPlaces, RoundingMode.HALF_UP).stripTrailingZeros();
		return rounded.toPlainString();
	}
}