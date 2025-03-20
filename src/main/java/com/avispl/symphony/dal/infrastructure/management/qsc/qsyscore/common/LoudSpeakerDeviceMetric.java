/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * LoudSpeakerDeviceMetric save all metric fields and corresponding response fields of the LoudSpeaker device metric
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum LoudSpeakerDeviceMetric implements DeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	MUTE("Mute", "mute"),
	GAIN("Gain(dB)", "gain"),
	DELAY("Delay(s)", "architectural.delay"),
	METER_SELECT("MeterSelect", "peak.rms.select"),

	FULL_RANGE_MUTE("Fullrange#Mute", "band.0.mute"),
	FULL_RANGE_INVERT("Fullrange#Invert", "band.0.invert"),
	FULL_RANGE_OPEN("Fullrange#Open", "band.0.open.circuit"),
	FULL_RANGE_SHORT("Fullrange#Short", "band.0.short.circuit"),

	FULL_RANGE_OPEN_THRESHOLD("Fullrange#OpenThreshold(Ω)", "band.0.open.circuit.threshold"),
	FULL_RANGE_SHORT_THRESHOLD("Fullrange#ShortThreshold(Ω)", "band.0.short.circuit.threshold"),

	FULL_RANGE_VOLTAGE("Fullrange#Voltage(V)", "band.0.voltage"),
	FULL_RANGE_CURRENT("Fullrange#Current(A)", "band.0.current"),
	FULL_RANGE_POWER("Fullrange#Power(W)", "band.0.power"),
	FULL_RANGE_GAIN("Fullrange#Gain(dB)", "band.0.monitor.gain"),
	FULL_RANGE_LIMITER("Fullrange#Limiter(dB)", "band.0.limiter.reduction"),
	FULL_RANGE_HIGHT_PASS_FREQ("Fullrange#HighPassFreq(Hz)", "band.0.line.voltage.hp.frequency"),
	FULL_RANGE_LISTEN("Fullrange#Listen", "band.0.monitor.listen"),
	;

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	LoudSpeakerDeviceMetric(String metric, String property) {
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