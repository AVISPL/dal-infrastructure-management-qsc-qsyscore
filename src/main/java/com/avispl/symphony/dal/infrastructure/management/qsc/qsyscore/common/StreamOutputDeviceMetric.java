/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * StreamOutputDeviceMetric save all metric fields and corresponding response fields of the Stream Output device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/25/2023
 * @since 1.0.0
 */
public enum StreamOutputDeviceMetric {
	CHANNEL_PEAK_INPUT_LEVEL("Channel%s#PeakInputLevel(dB)", "channel.%s.digital.output.level"),
	STATUS("Status", "output.status"),
	STATUS_LED("StatusLed", "output.status.led");
	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	StreamOutputDeviceMetric(String metric, String property) {
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
	public static StreamOutputDeviceMetric getByProperty(String property) {
		for (StreamOutputDeviceMetric controllingMetric : StreamOutputDeviceMetric.values()) {
			String[] splitProperty = controllingMetric.property.split("%s");
			if (splitProperty.length < 2) {
				if (Objects.equals(controllingMetric.getProperty(), property)) {
					return controllingMetric;
				}
			} else {
				try {
					Integer.parseInt(property.replace(splitProperty[0], QSYSCoreConstant.EMPTY).replace(splitProperty[1], QSYSCoreConstant.EMPTY));
					return controllingMetric;
				} catch (Exception e) {
					continue;
				}
			}
		}
		return null;
	}
}
