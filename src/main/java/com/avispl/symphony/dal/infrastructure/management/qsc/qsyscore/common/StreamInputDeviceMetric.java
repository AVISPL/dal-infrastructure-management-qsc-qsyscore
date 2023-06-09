/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * StreamInputDeviceMetric save all metric fields and corresponding response fields of the Stream Input device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/25/2023
 * @since 1.0.0
 */
public enum StreamInputDeviceMetric {
	CHANNEL_PEAK_INPUT_LEVEL("Channel%s#PeakInputLevel(dB)", "channel.%s.digital.input.level"),
	CHANNEL_INPUT_GAIN("Channel%s#GainCurrentValue(dB)", "channel.%s.input.gain"),
	STATUS("Status", "input.status"),
	STATUS_LED("StatusLed", "input.status.led"),
	CHANNEL_SUBSCRIPTION_STATUS("Channel%s#SubscriptionStatus", "channel.%s.subscription.status"),
	CHANNEL_SUBSCRIPTION_STATUS_LED("Channel%s#SubscriptionStatusLed", "channel.%s.subscription.status.led");
	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
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
}
