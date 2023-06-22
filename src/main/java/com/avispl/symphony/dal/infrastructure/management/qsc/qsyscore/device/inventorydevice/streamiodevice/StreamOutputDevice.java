package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.streamiodevice;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * StreamOutputDevice
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/22/2023
 * @since 1.0.0
 */
public class StreamOutputDevice extends StreamIODevice{
	@Override
	public void controlDevice(JsonNode response) {

	}

	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		super.monitoringDevice(deviceControl);
		this.getStats().put("","");
	}
}