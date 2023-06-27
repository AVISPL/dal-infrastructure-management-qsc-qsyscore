/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * StreamInputDeviceMetric save all metric fields and corresponding response fields of the Stream Input device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/25/2023
 * @since 1.0.0
 */
public enum StreamInputDeviceMetric {
	CHANNEL_PEAK_INPUT_LEVEL("Channel%s#PeakInputLevel(dB)", "channel.%s.digital.input.level"),
	STATUS("Status", "input.status"),
	STATUS_LED("StatusLed", "input.status.led"),
	CHANNEL_SUBCRIPTION_STATUS("Channel%s#SubcriptionStatus", "channel.%s.subscription.status"),
	CHANNEL_SUBCRIPTION_STATUS_LED("Channel%s#SubcriptionStatusled", "channel.%s.subscription.status.led"),
	CHANNEL_GAIN_CURRENT_VALUE("Channel%s#GainCurrentValue(dB)", "channel.%s.input.gain");
	private final String metric;
	private final String property;

	StreamInputDeviceMetric(String metric, String property) {
		this.metric = metric;
		this.property = property;
	}

	/**
	 * Retrieves {@link #metric}
	 *
	 * @return value of {@link #metric}
	 */
	public String getMetric() {
		return metric;
	}

	/**
	 * Retrieves {@link #property}
	 *
	 * @return value of {@link #property}
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Get metric of metric from QSYSCoreControllingMetric
	 *
	 * @param property property of metric
	 * @return Enum of QSYSCoreControllingMetric
	 */
	public static StreamInputDeviceMetric getByProperty(String property) {
		for (StreamInputDeviceMetric controllingMetric : StreamInputDeviceMetric.values()) {
			String[] splitProperty=controllingMetric.property.split("%s");
			if (splitProperty.length<2){
				if (Objects.equals(controllingMetric.getProperty(), property)) {
					return controllingMetric;
				}
			} else
			{
				if (property.startsWith(splitProperty[0]) && property.endsWith(splitProperty[splitProperty.length-1])){
					return controllingMetric;
				}
			}
		}
		return null;
	}
}
