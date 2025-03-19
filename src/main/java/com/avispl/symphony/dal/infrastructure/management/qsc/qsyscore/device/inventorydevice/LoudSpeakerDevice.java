/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.LoudSpeakerDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * LoudSpeakerDevice
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.0
 */
public class LoudSpeakerDevice extends QSYSPeripheralDevice {
	/**
	 * Manage are control of device
	 *
	 * @param response String store all information of a control
	 */
	@Override
	public void controlDevice(JsonNode response) {

	}

	/**
	 * A list of predefined metrics for LoudSpeaker devices.
	 * This list contains various measurement parameters and configurations
	 * relevant to loudspeakers, such as gain, power, voltage, and delay.
	 */
	private static final List<LoudSpeakerDeviceMetric> METRIC_LIST = Arrays.asList(
			LoudSpeakerDeviceMetric.FULL_RANGE_LIMITER,
			LoudSpeakerDeviceMetric.FULL_RANGE_GAIN,
			LoudSpeakerDeviceMetric.GAIN,
			LoudSpeakerDeviceMetric.FULL_RANGE_HIGHT_PASS_FREQ,
			LoudSpeakerDeviceMetric.FULL_RANGE_CURRENT,
			LoudSpeakerDeviceMetric.FULL_RANGE_POWER,
			LoudSpeakerDeviceMetric.FULL_RANGE_VOLTAGE,
			LoudSpeakerDeviceMetric.DELAY
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
		this.getStats().clear();
		this.getAdvancedControllableProperties().clear();
		if (!deviceControl.hasNonNull(QSYSCoreConstant.RESULT) ||
				!deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			return;
		}
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				LoudSpeakerDeviceMetric metric = EnumTypeHandler.getMetricByName(LoudSpeakerDeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
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
					case FULL_RANGE_INVERT:
						int status = value.equalsIgnoreCase("normal") || value.equalsIgnoreCase("false") ? 0 : 1;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metric.getMetric(), status, "Off", "On"),
								String.valueOf(status)
						);
						break;
					case GAIN:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "-100", "20", -100f, 20f, Float.parseFloat(value)), value);
						this.getStats().put("GainCurrentValue(dB)", uppercaseFirstCharacter(value));
						break;
					case DELAY:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "0", "2", 0f, 2f, Float.parseFloat(value)), value);
						this.getStats().put("DelayCurrentValue(ms)", uppercaseFirstCharacter(value));
						break;
					case FULL_RANGE_CURRENT:
					case FULL_RANGE_POWER:
					case FULL_RANGE_VOLTAGE:
						if (StringUtils.isNotNullOrEmpty(value)) {
							if (value.matches("^\\.\\d+$")) {
								value = "0" + value;
							}
							this.getStats().put(metric.getMetric(), value);
						} else {
							this.getStats().put(metric.getMetric(), QSYSCoreConstant.DEFAUL_DATA);
						}
						break;
					case FULL_RANGE_HIGHT_PASS_FREQ:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metric.getMetric(), "30", "300", 30f, 300f, Float.parseFloat(value)), value);
						this.getStats().put("Fullrange#HighPassFreqCurrentValue(Hz)", uppercaseFirstCharacter(value));
						break;
					case FULL_RANGE_OPEN_THRESHOLD:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								getFormattedMetricName(metric, control), "0", "1501", 0f, 1501f, Float.parseFloat(value)), value);
						this.getStats().put("Fullrange#OpenThresholdCurrentValue(Ω)", uppercaseFirstCharacter(value));
						break;
					case FULL_RANGE_SHORT_THRESHOLD:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								getFormattedMetricName(metric, control), "0", "1501", 0f, 1501f, Float.parseFloat(value)), value);
						this.getStats().put("Fullrange#ShortThresholdCurrentValue(Ω)", uppercaseFirstCharacter(value));
						break;
					case FULL_RANGE_OPEN:
					case FULL_RANGE_SHORT:
						this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
					case METER_SELECT:
						String[] options = { "Peak", "RMS" };
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createDropdown(metric.getMetric(), options, value), value);
						break;
					default:
						this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? uppercaseFirstCharacter(value) : QSYSCoreConstant.DEFAUL_DATA);
						break;
				}

			}
			super.updateStatusMessage();
	}
}