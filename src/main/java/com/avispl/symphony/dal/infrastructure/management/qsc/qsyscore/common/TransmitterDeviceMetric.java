/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * TransmitterDeviceMetric save all metric fields and corresponding response fields of the Transmitter device metric
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum TransmitterDeviceMetric implements DeviceMetric {
	ENABLE("Connection#Enable", "enable"),
	INTERFACE("Connection#Interface", "interface"),
	NETWORK_TX_BUFFER("Connection#NetworkTxBuffer", "network.buffer"),
	ENCODING("Connection#Encoding", "encoding"),
	STREAM_NAME("Connection#StreamName", "stream.name"),
	MULTI_CAST_ADDRESS("Connection#MulticastAddress", "address"),

	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),

	CHANNEL_PEAK_OUTPUT_LEVEL("Channel%s#PeakOutputLevel(dBFS)", "channel.%s.digital.output.level"),
	CHANNEL_INVERT("Channel%s#Invert", "channel.%s.output.invert"),
	CHANNEL_MUTE("Channel%s#Mute", "channel.%s.output.mute"),
	CHANNEL_GAIN("Channel%s#Gain(dB)", "channel.%s.output.gain"),
	CHANNEL_CLIP("Channel%s#Clip", "channel.%s.clip"),
	CHANNEL_CLIP_HOLD("Channel%s#ClipHold", "channel.%s.clip.hold");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	TransmitterDeviceMetric(String metric, String property) {
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