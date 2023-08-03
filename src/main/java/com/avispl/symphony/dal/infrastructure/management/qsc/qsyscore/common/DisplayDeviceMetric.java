/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * DisplayDeviceMetric save all metric fields and corresponding response fields of the Display device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/22/2023
 * @since 1.0.0
 */
public enum DisplayDeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	EDID_NAME("EDID#Name", "hdmi.edid.name"),
	EDID_SERIAL_NUMBER("EDID#SerialNumber", "hdmi.edid.serial.number"),
	EDID_AUDIO_CHANNELS("EDID#AudioChannels", "hdmi.edid.audio.channels"),
	HDMI_AV_INPUT_CONNECTED("HDMI#Connected(HPD)", "hdmi.connected.led"),
	VIDEO_FORMAT_STATUS("Video#FormatStatus", "hdmi.video.format.info"),
	HDCP_STATUS("HDCP#Status", "hdmi.hdcp.mode.info"),
	HDMI_DVI_STATUS("HDMI#HDMI-DVIStatus", "hdmi.output.mode.info"),
	HDCP_ENCRYPTION("HDCP#Encryption", "hdmi.hdcp.encrypted"),
	COLOR_FORMAT_STATUS("ColorFormatStatus", "hdmi.color.format.info"),
	ASPECT_RATIO_STATUS("AspectRatioStatus", "hdmi.aspect.ratio.info"),
	IDLE_TIMER("IdleTimer", "hdmi.idle.timer"),
	SLEEP_TIMER("SleepTimer", "hdmi.sleep.timer"),
	CHANNEL_PEAK_INPUT_LEVEL("Channel%s#PeakInputLevel(dB)", "channel.%s.digital.output.level"),
	CHANNEL_VALID("Channel%s#Valid", "channel.%s.valid"),
	CHANNEL_CLIP("Channel%s#Clip", "channel.%s.clip");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	DisplayDeviceMetric(String metric, String property) {
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
