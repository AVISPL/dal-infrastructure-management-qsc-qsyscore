/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * Amplifier_CXQ_Status save all metric fields and corresponding response fields of the Amplifier device metric
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.1.0
 */
public enum Amplifier_CXQ_StatusDeviceMetric implements DeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	TEMPERATURE("Temperature(C)", "system.temperature"),

	LAN_A_ACTIVE("LAN_A#Link", "lan.a.active"),
	LAN_A_ADDRESS("LAN_A#IPAddress", "lan.a.address"),
	LAN_A_MODE("LAN_A#Mode", "lan.a.mode"),
	LAN_A_SPEED("LAN_A#Speed", "lan.a.speed"),
	LAN_A_STATE("LAN_A#PTPv2State", "lan.a.state"),

	LAN_B_ACTIVE("LAN_B#Link", "lan.b.active"),
	LAN_B_ADDRESS("LAN_B#IPAddress", "lan.b.address"),
	LAN_B_MODE("LAN_B#Mode", "lan.b.mode"),
	LAN_B_SPEED("LAN_B#Speed", "lan.b.speed"),
	LAN_B_STATE("LAN_B#PTPv2State", "lan.b.state"),

	AUDIO_STREAM_INPUT_OK("AudioStreams#InputOK", "primary.input.stream.ok"),
	AUDIO_STREAM_OUTPUT_OK("AudioStreams#OutputOK", "primary.output.stream.ok"),
	AUDIO_STREAM_RESET_DETAILS("AudioStreams#ResetDetail", "primary.iobox.reset.details"),

	NETWORK_GRANDMASTER_NAME("Network#GrandMasterName", "grandmaster.name"),
	NETWORK_PARENT_PORT_NAME("Network#ParentPortName", "parent.port.name"),
	NETWORK_CLOCK_OFFSET("Network#ClockOffset", "clock.offset");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	Amplifier_CXQ_StatusDeviceMetric(String metric, String property) {
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