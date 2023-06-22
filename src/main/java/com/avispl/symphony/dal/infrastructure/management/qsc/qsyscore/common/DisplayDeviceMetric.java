package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * DisplayDeviceMetric
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/22/2023
 * @since 1.0.0
 */
public enum DisplayDeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("Statusled", "status.led"),
	EDID_NAME("EDIDName", "hdmi.edid.name"),
	EDID_SERIAL_NUMBER("EDIDSerialNumber", "hdmi.edid.serial.number"),
	EDID_AUDIO_CHANNELS("EDIDAudioChannels", "hdmi.edid.audio.channels"),
	HDMI_AV_INPUT_CONNECTED("HDMIAVInputConnected(HPD)", "hdmi.connected.led"),
	VIDEO_FORMAT_STATUS("VideoFormatStatus", "hdmi.video.format.info"),
	HDCP_STATUS("HDCPStatus", "hdmi.hdcp.mode.info"),
	HDMI_DVI_STATUS("HDMI-DVIStatus", "hdmi.output.mode.info"),
	HDCP_ENCRYPTION("HDCPEncryption", "hdmi.hdcp.encrypted"),
	COLOR_FORMAT_STATUS("ColorFormatStatus", "hdmi.color.format.info"),
	ASPECT_RATIO_STATUS("AspectRatioStatus", "hdmi.aspect.ratio.info"),
	CHANNEL("Channel", "");

	private final String metric;
	private final String property;

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

	/**
	 * Get metric of metric from QSYSCoreControllingMetric
	 *
	 * @param property property of metric
	 * @return Enum of QSYSCoreControllingMetric
	 */
	public static DisplayDeviceMetric getByProperty(String property) {
		for (DisplayDeviceMetric controllingMetric : DisplayDeviceMetric.values()) {
			if (Objects.equals(controllingMetric.getProperty(), property)) {
				return controllingMetric;
			}
		}
		return null;
	}
}
