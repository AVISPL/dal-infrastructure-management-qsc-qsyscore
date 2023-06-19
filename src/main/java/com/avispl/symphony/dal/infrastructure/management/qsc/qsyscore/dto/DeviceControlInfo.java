package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceControlWrapper
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/19/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceControlInfo {
	DeviceControlWrapper result;

	/**
	 * Retrieves {@link #result}
	 *
	 * @return value of {@link #result}
	 */
	public DeviceControlWrapper getResult() {
		return result;
	}

	/**
	 * Sets {@link #result} value
	 *
	 * @param result new value of {@link #result}
	 */
	public void setResult(DeviceControlWrapper result) {
		this.result = result;
	}
}