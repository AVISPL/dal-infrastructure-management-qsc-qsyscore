/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

import com.avispl.symphony.dal.util.StringUtils;

/**
 * GainControllingMetric save all metric fields and corresponding response fields of the gain component
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/21/2023
 * @since 1.0.0
 */
public enum GainControllingMetric {
	CURRENT_GAIN_VALUE("GainCurrentValue(dB)", null),
	GAIN_VALUE_CONTROL("GainControl(dB)", "gain"),
	MUTE_CONTROL("Mute", "mute"),
	INVERT_CONTROL("Invert", "invert"),
	BYPASS_CONTROL("Bypass", "bypass"),
	ERROR_MESSAGE("#Error Message", null);

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	GainControllingMetric(String metric, String property) {
		this.metric = metric;
		this.property = property;
	}

	/**
	 * Retrieves {@code {@link #metric }}
	 *
	 * @return value of {@link #metric}
	 */
	public String getMetric() {
		return metric;
	}

	/**
	 * Retrieves {@code {@link #property}}
	 *
	 * @return value of {@link #property}
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Get metric of metric from QSYSCoreControllingMetric
	 *
	 * @param metric metric of metric
	 * @return Enum of QSYSCoreControllingMetric
	 */
	public static GainControllingMetric getByMetric(String metric) {
		for (GainControllingMetric controllingMetric : values()) {
			if (Objects.equals(controllingMetric.getMetric(), metric)) {
				return controllingMetric;
			}
		}
		throw new IllegalArgumentException("Cannot find the enum with metric: " + metric);
	}

	/**
	 * Get name of metric from QSYSCoreControllingMetric
	 *
	 * @param name name of metric Gian
	 * @return Enum of QSYSCoreControllingMetric
	 */
	public static GainControllingMetric getByName(String name) {
		for (GainControllingMetric controllingMetric : values()) {
			if (!StringUtils.isNullOrEmpty(controllingMetric.getProperty()) &&controllingMetric.getProperty().equalsIgnoreCase( name)) {
				return controllingMetric;
			}
		}
		return null;
	}
}