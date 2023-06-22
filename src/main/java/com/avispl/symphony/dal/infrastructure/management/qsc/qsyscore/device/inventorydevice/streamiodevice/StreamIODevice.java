package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.streamiodevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;

/**
 * StreamIODevice
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/22/2023
 * @since 1.0.0
 */
public abstract class StreamIODevice extends QSYSPeripheralDevice {
	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		this.getStats().put("","");
	}
}