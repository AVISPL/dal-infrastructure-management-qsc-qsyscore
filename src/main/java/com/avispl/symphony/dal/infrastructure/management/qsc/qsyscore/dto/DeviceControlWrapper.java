package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceControl
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/19/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceControlWrapper {
	@JsonAlias("Controls")
	private List<DeviceControl> controls;

	/**
	 * Retrieves {@link #controls}
	 *
	 * @return value of {@link #controls}
	 */
	public List<DeviceControl> getControls() {
		return controls;
	}

	/**
	 * Sets {@link #controls} value
	 *
	 * @param controls new value of {@link #controls}
	 */
	public void setControls(List<DeviceControl> controls) {
		this.controls = controls;
	}
}