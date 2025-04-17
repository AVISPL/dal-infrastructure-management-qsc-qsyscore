/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * AmplifierDeviceMetric save all metric fields and corresponding response fields of the Amplifier device metric
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum AmplifierDeviceMetric implements DeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	ON_STANDBY("On/Standby", "power.on"),

	MUTE_ALL("MuteAll", "master.mute"),
	POWER_METERS("PowerMeters", "power.meters"),
	GAIN_LOCK("GainLock", "gain.lockout"),
	MUTE_LOCK("MuteLock", "mute.lockout"),
	FRONT_PANEL_DISABLE("FrontPanelDisable", "front.panel.disable"),
	PSU_TEMP("PSUTemperature(C)", "psu.temp"),

	VRAIL_1("VRail1(V)", "rail.voltage.1"),
	VRAIL_2("VRail2(V)", "rail.voltage.2"),
	FAN_RPM("Fan(RPM)", "system.fan.1.speed"),
	METER_SELECT("MeterSelect", "peak.rms.select"),

	AC_CURRENT("PowerManagement#ACCurrent(A)", "ac.current"),
	AC_VOLTAGE("PowerManagement#ACVoltage(V)", "ac.voltage"),
	POWER_SAVE_THRESHOLD("PowerManagement#PowerSaveThreshold(dB)", "powersave.threshold"),
	POWER_SAVE_TIMEOUT("PowerManagement#PowerSaveTimeout(m)", "powersave.timeout"),
	DISABLE_POWER_SAVE("PowerManagement#DisablePowerSave", "powersave.disable"),

	CHANNEL_MUTE("Channel%s#Mute", "channel.%s.backup.mute"),
	CHANNEL_GAIN("Channel%s#Gain(dB)", "channel.%s.backup.gain"),
	CHANNEL_OVER_CURRENT("Channel%s#OverCurrent", "channel.%s.over.current"),
	CHANNEL_DAC_LIMIT("Channel%s#DACLimit", "channel.%s.dac.output.limit"),
	CHANNEL_PROTECT("Channel%s#Protect", "channel.%s.protect"),
	CHANNEL_POWER_SAVE("Channel%s#PowerSave", "channel.%s.powersave"),
	CHANNEL_LIMIT("Channel%s#Limit", "channel.%s.limit"),
	CHANNEL_SHORT("Channel%s#Short", "channel.%s.output.short"),
	CHANNEL_TEMP("Channel%s#Temperature(C)", "channel.%s.temperature"),
	CHANNEL_INPUT("Channel%s#Input(dBFS)", "channel.%s.input.meter"),
	CHANNEL_OUTPUT("Channel%s#Output(dBFS)", "channel.%s.output.meter"),
	CHANNEL_HEAD_ROOM("Channel%s#Headroom(dB)", "channel.%s.headroom"),
	CHANNEL_CURRENT("Channel%s#Current(A)", "channel.%s.current"),
	CHANNEL_VOLTAGE("Channel%s#Voltage(V)", "channel.%s.voltage"),
	CHANNEL_POWER("Power(W)", "channel.x.power");

	private final String metric;
	private final String property;

	/**
	 * Parameterized constructor
	 *
	 * @param metric metric that show on UI
	 * @param property corresponding response field
	 */
	AmplifierDeviceMetric(String metric, String property) {
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