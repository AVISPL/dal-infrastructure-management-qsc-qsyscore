/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.AmplifierDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * AmplifierDevice
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.0
 */
public class AmplifierDevice extends QSYSPeripheralDevice {

	private final Map<String, Integer> previousChannelMuteValues = new HashMap<>();

	/**
	 * Manage are control of device
	 *
	 * @param response String store all information of a control
	 */
	@Override
	public void controlDevice(JsonNode response) {

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
			AmplifierDeviceMetric.METER_SELECT,
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
		int muteAll = 0;
		int meterValue = 0;
		this.getStats().clear();
		this.getAdvancedControllableProperties().clear();

		if (!deviceControl.hasNonNull(QSYSCoreConstant.RESULT) ||
				!deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			return;
		}

			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				AmplifierDeviceMetric metric = EnumTypeHandler.getMetricByPropertyName(AmplifierDeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (metric == null) {
					continue;
				}
				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				if (METRIC_LIST.contains(metric)) {
					for (Entry<String, String> entry : UNIT_REPLACEMENTS.entrySet()) {
						value = value.replace(entry.getKey(), entry.getValue());
					}
				}
				String metricName = getFormattedMetricName(metric, control);
				switch (metric){
					case ON_STANDBY:
					case GAIN_LOCK:
					case MUTE_LOCK:
					case DISABLE_POWER_SAVE:
						int status = value.equalsIgnoreCase("enabled") || value.equalsIgnoreCase("true")
								|| value.equalsIgnoreCase("on") ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, status, "Off", "On"),
								String.valueOf(status)
						);
						break;
					case POWER_METERS:
						meterValue = value.equalsIgnoreCase("enabled") ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, meterValue, "Off", "On"),
								String.valueOf(meterValue)
						);
						break;
					case CHANNEL_VOLTAGE:
					case CHANNEL_CURRENT:
						if(meterValue == 0){
							this.getStats().remove(metricName);
						} else {
							this.getStats().put(metricName, StringUtils.isNotNullOrEmpty(value) ? uppercaseFirstCharacter(value) : QSYSCoreConstant.DEFAUL_DATA);
						}
						break;
					case MUTE_ALL:
						muteAll = value.equalsIgnoreCase("unmuted") ? 1 : 0;
						addAdvancedControlProperties(
								this.getAdvancedControllableProperties(),
								getStats(),
								createSwitch(metricName, muteAll, "Off", "On"),
								String.valueOf(muteAll)
						);
						break;
					case CHANNEL_MUTE:
						int channelMute = value.equalsIgnoreCase("unmuted") ? 1 : 0;
						if(muteAll == 1) {
							previousChannelMuteValues.putIfAbsent(metricName, channelMute);
							addAdvancedControlProperties(
									this.getAdvancedControllableProperties(),
									getStats(),
									createSwitch(metricName, muteAll, "Off", "On"),
									String.valueOf(muteAll)
							);
						} else {
							int previousValue = previousChannelMuteValues.getOrDefault(metricName, channelMute);
							previousChannelMuteValues.remove(metricName);
							addAdvancedControlProperties(
									this.getAdvancedControllableProperties(),
									getStats(),
									createSwitch(metricName, previousValue, "Off", "On"),
									String.valueOf(previousValue)
							);
						}
						break;
					case METER_SELECT:
						String[] options = { "Peak", "RMS" };
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createDropdown(metricName, options, value), value);
						break;
					case CHANNEL_GAIN:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metricName, "-100", "20", -100f, 20f, Float.parseFloat(value)), value);
						this.getStats().put(getFormattedMetricNameSlider(metricName), uppercaseFirstCharacter(value));
						break;
					case POWER_SAVE_THRESHOLD:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metricName, "-99", "-50", -99.0f, -50.0f, Float.parseFloat(value)), value);
						this.getStats().put(getFormattedMetricNameSlider(metricName), uppercaseFirstCharacter(value));
						break;
					case POWER_SAVE_TIMEOUT:
						addAdvancedControlProperties(this.getAdvancedControllableProperties(), getStats(), createSlider(getStats(),
								metricName, "1", "99", 1.0f, 99.0f, Float.parseFloat(value)), value);
						this.getStats().put("PowerManagement#PowerSaveTimeoutCurrentValue", uppercaseFirstCharacter(value));
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
	}
}