package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceInfo
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {
	@JsonAlias("data")
	private DeviceInfoData deviceInfoData;

	/**
	 * Retrieves {@link #deviceInfoData}
	 *
	 * @return value of {@link #deviceInfoData}
	 */
	public DeviceInfoData getDeviceInfoData() {
		return deviceInfoData;
	}

	/**
	 * Sets {@link #deviceInfoData} value
	 *
	 * @param deviceInfoData new value of {@link #deviceInfoData}
	 */
	public void setDeviceInfoData(DeviceInfoData deviceInfoData) {
		this.deviceInfoData = deviceInfoData;
	}
}