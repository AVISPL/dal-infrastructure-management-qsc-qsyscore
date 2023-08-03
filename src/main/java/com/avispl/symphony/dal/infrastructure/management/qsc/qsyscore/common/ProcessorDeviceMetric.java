/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * ProcessorDeviceMetric save all metric fields and corresponding response fields of the Processor device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/21/2023
 * @since 1.0.0
 */
public enum ProcessorDeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	CLOCK_MASTER("ClockMaster", "clock.master"),
	CLOCK_OFFSET("ClockOffset", "clock.offset"),
	GRANDMASTER_NAME("GrandmasterName", "grandmaster.name"),
	PROCESSOR_TEMPERATURE("ProcessorTemperature(C)", "processor.temperature"),
	FAN_SPEED("FanSpeed(rpm)", "system.fan.1.speed"),
	SYSTEM_TEMPERATURE("SystemTemperature(C)", "system.temperature"),
	PARENT_PORT_NAME("ParentPortName", "parent.port.name"),
	USB_CONNECTED("USBConnected", "usb.plugged.in"),
	USB_EC_SPEAKERPHONE_1_IN_ACTIVE("USBECSpeakerphone1InActive", "bridge.audio.active.1"),
	USB_EC_SPEAKERPHONE_1_OUT_ACTIVE("USBECSpeakerphone1OutActive", "bridge.audio.active.2");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	ProcessorDeviceMetric(String metric, String property) {
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
