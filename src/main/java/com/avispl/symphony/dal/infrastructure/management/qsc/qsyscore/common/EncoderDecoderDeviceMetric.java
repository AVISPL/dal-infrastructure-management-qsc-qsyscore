/*
 * Copyright (c) 2026 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;
/**
 * EncoderDecoder device metric fields and corresponding response fields of the EncoderDecoder device
 *
 * @author Maksym.Rossiitsev / Symphony Dev Team<br>
 * @since 1.2.1
 */
public enum EncoderDecoderDeviceMetric implements DeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	STATUS_REMOTE("StatusRemote", "remote.status"),
	BOARD_REVISION("BoardRevision", "board.revision"),
	IO_CARD_ID("IOCardID", "io.card.id"),

	POE_STATUS("System#PoEStatus", "poe.status"),
	POE_STATUS_LED("System#PoEStatusLed", "poe.status.led"),
	SYSTEM_AUX_POWER("System#AuxPower", "system.aux.power"),
	SYSTEM_AUX_VOLTAGE("System#AuxVoltage", "system.aux.voltage"),
	SYSTEM_TEMPERATURE("System#Temperature(C)", "system.temperature"),
	PROCESSOR_TEMPERATURE("System#CPUTemperature(C)", "processor.temperature"),
	FAN_SPEED("System#Fan%sSpeed(rpm)", "system.fan.%s.speed"),
	FAN_AUTO("System#FanAuto", "fan.auto.button"),
	FAN_PWM_WRITE("System#FanPWMWrite", "fan.pwm.write"),
	FAN_PWM_READ("System#FanPWMRead", "fan.pwm.read"),
	USBC_PD_ACTIVE("System#USBPDActive", "usbcpd.active"),
//	USBC_PD_ENABLE("USB#PDEnable", "usbcpd.enable"),
//	USB_RESET("USB#Reset", "initiate.usb.reset");
	USBC_PD_FAULT("System#USBPDFault", "usbcpd.fault"),
	USBC_PD_VOLTAGE("System#USBPDVoltage(V)", "usbcpd.voltage.current"),

	VOLT_12V("Power#12VPort", "volt.12v"),
	VOLT_5V("Power#5VPort", "volt.5v"),
	VOLT_3V3("Power#3.3VPort", "volt.3.3v"),
	VOLT_CORE("Power#Core", "volt.core"),
	VOLT_AUX("Power#AuxSupply", "volt.aux.supply"),
	VOLT_INT("Power#InternalSupply", "volt.int.supply"),
	VOLT_BRAM("Power#BRAMSupply", "volt.bram.supply"),

	PSE_MAX_POWER("PSE#MaxPower", "pse.max.power"),
	PSE_POWER_AVAILABLE("PSE#PowerAvailable", "pse.power.available"),
	PSE_POWER_CONSUMED("PSE#PowerConsumed", "pse.power.consumed"),
	PSE_OUTPUT_VOLTS("PSE#OutputVolts", "pse.output.volts"),
	PSE_REFRESH("PSE#Refresh", "pse.refresh.info"),

	LAN_ACTIVE("LAN#Active", "lan.a.active"),
	LAN_ADDRESS("LAN#Address", "lan.a.address"),
	LAN_MODE("LAN#Mode", "lan.a.mode"),
	LAN_SPEED("LAN#Speed", "lan.a.speed"),
	LAN_STATE("LAN#State", "lan.a.state"),

	ENC_WIDTH("Encoder#Width(px)", "enc.dev.config.width"),
	ENC_HEIGHT("Encoder#Height(px)", "enc.dev.config.height"),
	ENC_FRAMERATE("Encoder#FrameRate", "enc.dev.config.frameRate"),
	ENC_QUANTIZATION("Encoder#Quantization", "enc.dev.config.quantization"),
	ENC_QUANTIZATION_DELTA("Encoder#QuantizationDelta", "enc.dev.config.quantizationDelta"),
	ENC_CBR_MODE("Encoder#CBRMode", "enc.dev.config.cbrMode"),
	ENC_FORCE_IFRAME("Encoder#ForceIFrame", "enc.dev.config.forceIFrame"),
	ENC_INTER_REFRESH("Encoder#InterRefresh", "enc.dev.config.interRefresh"),
	ENC_MBR_LATENCY("Encoder#MBRLatency", "enc.dev.config.mbr.latency"),
	ENC_H264_LEVEL("Encoder#H264Level", "enc.dev.config.h264Level"),

	NET_HDMI_AVG_BITRATE("HDMI%sInput#AvgBitrate", "network.stats.hdmi.input.%s.avg.bitrate"),
	NET_HDMI_PEAK_BITRATE("HDMI%sInput#PeakBitrate", "network.stats.hdmi.input.%s.peak.bitrate"),
	NET_HDMI_DROP_COUNT("HDMI%sInput#DropCount", "network.stats.hdmi.input.%s.drop.count"),
	NET_HDMI_DSCP("HDMI%sInput#DSCP", "network.stats.hdmi.input.%s.dscp");
//	NET_HDMI_NETWORK_TEST("HDMI%sInput#NetworkTest", "network.stats.hdmi.input.%s.network.test"),
//	NET_HDMI_NETWORK_TEST_STATUS("HDMI%sInput#NetworkTestStatus", "network.stats.hdmi.input.%s.network.test.status");

	private final String metric;
	private final String property;
	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	EncoderDecoderDeviceMetric(String metric, String property) {
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