/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * VideoSourceDeviceMetric save all metric fields and corresponding response fields of the Video Source device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/23/2023
 * @since 1.0.0
 */
public enum VideoSourceDeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	HDMI_AV_OUTPUT_5V("HDMIAVOutput5v", "5v"),
	HDMI_AV_OUTPUT_VALID_FORMAT("HDMIAVOutputValidFormat", "valid.format"),
	VIDEO_FORMAT_STATUS("VideoFormatStatus", "input.format"),
	COLOR_FORMAT_STATUS("ColorFormatStatus", "color.format"),
	ASPECT_RATIO("AspectRatio", "aspect.ratio"),
	ACTIVE_AUDIO_CHANNELS("ActiveAudioChannels", "audio.channel.count"),
	MAX_SUPPORTED_FORMAT("MaxSupportedFormat", "max.format.edid"),
	HDCP_STATUS("HDCP#Status", "hdcp.status"),
	HDCP_ENCRYPTION("HDCP#Encryption", "hdcp.encrypted"),
	CHANNEL_PEAK_INPUT_LEVEL("Channel%s#PeakInputLevel(dB)", "channel.%s.digital.input.level"),
	CHANNEL_VALID("Channel%s#Valid", "channel.%s.valid");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	VideoSourceDeviceMetric(String metric, String property) {
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
	 * Get metric of metric from QSYSCoreControllingMetric
	 *
	 * @param property property of metric
	 * @return Enum of QSYSCoreControllingMetric
	 */
	public static VideoSourceDeviceMetric getByProperty(String property) {
		for (VideoSourceDeviceMetric controllingMetric : VideoSourceDeviceMetric.values()) {
			String[] splitProperty = controllingMetric.property.split("%s");
			if (splitProperty.length < 2) {
				if (Objects.equals(controllingMetric.getProperty(), property)) {
					return controllingMetric;
				}
			} else {
				try {
					Integer.parseInt(property.replace(splitProperty[0], QSYSCoreConstant.EMPTY).replace(splitProperty[1], QSYSCoreConstant.EMPTY));
					return controllingMetric;
				} catch (Exception e) {
					continue;
				}
			}
		}
		return null;
	}
}
