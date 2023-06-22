package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * GainControllingMetric
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/21/2023
 * @since 1.0.0
 */
public enum GainControllingMetric {
	CURRENT_GAIN_VALUE("GainCurrentValue(dB)", null),
	GAIN_VALUE_CONTROL("GainValueControl", "gain"),
	MUTE_CONTROL("Mute", "mute"),
	INVERT_CONTROL("Invert", "invert"),
	BYPASS_CONTROL("Bypass", "bypass"),
	ERROR_MESSAGE("#Error Message", null);

	private final String metric;
	private final String property;

	/**
	 * QSYSCoreControllingMetric with arg constructor
	 *
	 * @param metric name of the metric
	 * @param property of control
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
		for (GainControllingMetric controllingMetric : GainControllingMetric.values()) {
			if (Objects.equals(controllingMetric.getMetric(), metric)) {
				return controllingMetric;
			}
		}
		throw new IllegalArgumentException("Cannot find the enum with metric: " + metric);
	}
}