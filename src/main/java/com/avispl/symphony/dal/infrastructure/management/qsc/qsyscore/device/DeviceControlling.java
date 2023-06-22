package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * DeviceControlling
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
public interface DeviceControlling<T> {
	/**
	 * Manage are control of device
	 *
	 * @param response String store all information of a control
	 */
	void controlDevice(JsonNode response);
}
