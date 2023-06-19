package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceLANInfo
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceLANInfo {
	DeviceLANInfoData data;

	/**
	 * Retrieves {@link #data}
	 *
	 * @return value of {@link #data}
	 */
	public DeviceLANInfoData getData() {
		return data;
	}

	/**
	 * Sets {@link #data} value
	 *
	 * @param data new value of {@link #data}
	 */
	public void setData(DeviceLANInfoData data) {
		this.data = data;
	}
}