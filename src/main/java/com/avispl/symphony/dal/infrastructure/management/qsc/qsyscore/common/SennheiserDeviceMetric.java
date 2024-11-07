/*
 * Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * SennheiserDeviceMetric save all metric fields and corresponding response fields of the Sennheiser device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 10/22/2024
 * @since 1.0.2
 */
public enum SennheiserDeviceMetric {

	STATUS("Status", "Status"),
	DEVICE("Device", "Device"),
	CONNECTION_MODE("ConnectionMode", "DiscoveryMode"),
	AUDIO_LEVEL("AudioLevel(dB)", "AudioLevel"),
	DEVICE_LOCATION("DeviceLocation", "DeviceLocation"),
	IP_ADDRESS("IPAddress", "IPAddress"),
	LED_BRIGHTNESS("LEDBrightness", "LEDBrightness"),
	LED_ON_OFF("LEDOnOff", "LEDOnOff"),
	MUTE("Mute", "Mute"),
	MUTE_COLOR("MuteColor", "MuteColor"),
	NOISE_GATE("NoiseGate", "NoiseGate"),
	DEVICE_PORT("DevicePort", "Port"),
	PRIORITY_ZONE("PriorityZone", "PriorityZone"),
	SOUND_PROFILE("SoundProfile", "SoundProfile"),
	HORIZONTAL_ANGLE("HorizontalAngle", "HorizontalAngle"),
	VERTICAL_ANGLE("VerticalAngle", "VerticalAngle"),
	TRU_VOICE_LIFT("TruVoicelift", "TruVoicelift"),
	ON_COLOR("OnColor", "OnColor"),
	EXCLUSION_1_ZONES("ExclusionZone1", "ExclusionZones 1"),
	EXCLUSION_2_ZONES("ExclusionZone2", "ExclusionZones 2"),
	EXCLUSION_3_ZONES("ExclusionZone3", "ExclusionZones 3"),
	EXCLUSION_4_ZONES("ExclusionZone4", "ExclusionZones 4"),
	EXCLUSION_5_ZONES("ExclusionZone5", "ExclusionZones 5"),
	NOISE_LEVEL("RoomNoiseLevel", "NoiseLevel");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that shows on UI
	 * @param property corresponding response field
	 */
	SennheiserDeviceMetric(String metric, String property) {
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
	 * Get metric of metric from SennheiserDeviceMetric
	 *
	 * @param metric metric of metric
	 * @return Enum of SennheiserDeviceMetric
	 */
	public static SennheiserDeviceMetric getByMetric(String metric) {
		for (SennheiserDeviceMetric controllingMetric : values()) {
			if (Objects.equals(controllingMetric.getMetric(), metric)) {
				return controllingMetric;
			}
		}
		throw new IllegalArgumentException("Cannot find the enum with metric: " + metric);
	}
}
