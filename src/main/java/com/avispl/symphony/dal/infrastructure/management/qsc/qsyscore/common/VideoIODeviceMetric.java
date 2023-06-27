/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * VideoIODeviceMetric save all metric fields and corresponding response fields of the Video IO device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/22/2023
 * @since 1.0.0
 */
public enum VideoIODeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	CLOCK_OFFSET("ClockOffset", "clock.offset"),
	GRANDMASTER("Grandmaster", "grandmaster.name"),
	CPU_TEMPERATURE("CPUTemperature(C)", "processor.temperature"),
	VPU_TEMPERATURE("VPUTemperature(C)", "system.temperature"),
	IO_TEMPERATURE("I/OTemperature(C)", "io.card.temperature"),
	PARENT_PORT("ParentPort", "parent.port.name"),
	FAN_1("Fan1", "system.fan.1.speed"),
	FAN_2("Fan2", "system.fan.2.speed"),
	POE_LESS_THAN_90W("PoELessThan90w", "system.poe.status.A"),
	POE_EQUAL_90W("PoEEqual90w", "system.poe.plus.plus.status.A"),
	AUX_POWER("AuxPower", "system.aux.power"),
	NETWORKED_AUDIO_HDMI_OUTPUT_DETAILS("NetworkedAudio#HDMIOutput%sDetails", "decoder.hdmi.%s.sink.aes67.primary.details"),
	NETWORKED_VIDEO_HDMI_OUTPUT_SOURCE_NAME("NetworkedVideo#HDMIOutput%sSourceName", "network.stats.hdmi.%s.source.name"),
	NETWORKED_VIDEO_HDMI_OUTPUT_BITRATE("NetworkedVideo#HDMIOutput%sBitrate(Mbps)", "network.stats.hdmi.%s.bitrate"),
	NETWORKED_VIDEO_HDMI_OUTPUT_PEAK_BITRATE("NetworkedVideo#HDMIOutput%sPeakBitrate(Mbps)", "network.stats.hdmi.%s.peak.bitrate"),
	NETWORKED_VIDEO_HDMI_OUTPUT_BMCAST_IP_SRC("NetworkedVideo#HDMIOutput%sBMcastIPSrc", "network.stats.hdmi.%s.bmcast.ip.src"),
	NETWORKED_VIDEO_HDMI_OUTPUT_PACKET_LOST("NetworkedVideo#HDMIOutput%sPacketLost", "network.stats.hdmi.%s.packets.lost"),
	NETWORKED_VIDEO_HDMI_OUTPUT_PACKET_LOSS("NetworkedVideo#HDMIOutput%sPacketLoss(%)", "network.stats.hdmi.%s.packet.loss.%"),
	NETWORKED_VIDEO_HDMI_OUTPUT_PACKET_COUNT("NetworkedVideo#HDMIOutput%sPacketCount", "network.stats.hdmi.%s.packet.count"),
	NETWORKED_VIDEO_HDMI_OUTPUT_SEQUENCE_ERRORS("NetworkedVideo#HDMIOutput%sSequenceErrors", "network.stats.hdmi.%s.sequence.error.count"),
	NETWORKED_VIDEO_HDMI_OUTPUT_HW_DROP_COUNT("NetworkedVideo#HDMIOutput%sHWDropCount", "network.stats.hdmi.%s.hw.drop.count"),
	NETWORKED_AUDIO_STREAM_INPUT_DETAILS("NetworkedAudio#StreamInputDetails", "primary.input.stream.info"),
	NETWORKED_AUDIO_STREAM_OUTPUT_DETAILS("NetworkedAudio#StreamOutputDetails", "primary.output.stream.info");

	private final String metric;
	private final String property;

	VideoIODeviceMetric(String metric, String property) {
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
	public static VideoIODeviceMetric getByProperty(String property) {
		for (VideoIODeviceMetric controllingMetric : VideoIODeviceMetric.values()) {
			String[] splitProperty=controllingMetric.property.split("%s");
			if (splitProperty.length<2){
				if (Objects.equals(controllingMetric.getProperty(), property)) {
					return controllingMetric;
				}
			} else
			{
				if (property.startsWith(splitProperty[0]) && property.endsWith(splitProperty[splitProperty.length-1])){
					return controllingMetric;
				}
			}
		}
		return null;
	}
}