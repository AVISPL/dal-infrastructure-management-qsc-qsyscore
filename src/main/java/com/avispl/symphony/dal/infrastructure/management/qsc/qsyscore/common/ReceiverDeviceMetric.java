/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * ReceiverDeviceMetric save all metric fields and corresponding response fields of the Receiver device metric
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum ReceiverDeviceMetric implements DeviceMetric {
	ENABLE("Connection#Enable", "enable"),
	INTERFACE("Connection#Interface", "interface"),
	NETWORK_RX_BUFFER("Connection#NetworkRxBuffer", "network.buffer"),
	STREAM_NAME("Connection#StreamName", "stream.name"),

	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),

	CHANNEL_PEAK_INPUT_LEVEL("Channel%s#PeakInnputLevel(dB)", "channel.%s.digital.input.level"),
	CHANNEL_INVERT("Channel%s#Invert", "channel.%s.input.invert"),
	CHANNEL_MUTE("Channel%s#Mute", "channel.%s.input.mute"),
	CHANNEL_GAIN("Channel%s#Gain(dB)", "channel.%s.input.gain");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	ReceiverDeviceMetric(String metric, String property) {
		this.metric = metric;
		this.property = property;
	}

	/**
	 * Retrieves {@link #metric}
	 *
	 * @return value of {@link #metric}
	 */
	@Override
	public String getMetric() {
		return metric;
	}

	/**
	 * Retrieves {@link #property}
	 *
	 * @return value of {@link #property}
	 */
	@Override
	public String getProperty() {
		return property;
	}
}