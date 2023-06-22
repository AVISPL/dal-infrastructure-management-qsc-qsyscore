package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.schematicdevice;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * GainDevice
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/16/2023
 * @since 1.0.0
 */
public class GainDevice extends SchematicDevice {

	public GainDevice(String groupName) {
		super("Gain:" + groupName);
	}

	@Override
	public void controlDevice(JsonNode response) {
//		ObjectMapper objectMapper=ObjectMapperSingleton.getInstance();
	}

	@Override
	public void monitoringDevice(JsonNode deviceControls) {
//		for (DeviceControl deviceControl : deviceControls) {
//			switch (deviceControl.getName()) {
//				case "bypass":
//					String name = this.getGroupName() + QSYSCoreConstant.HASH + "Bypass";
//					this.getStats().put(name, "");
//					ControlToolbox.addAdvanceControlProperties(this.getAdvancedControllableProperties(), this.getStats(),
//							ControlToolbox.createSwitch(name, deviceControl.getString().equals("no") ? 0 : 1, "Off", "On"),
//							deviceControl.getString().equals("no") ? "0" : "1");
//					break;
//			}
//		}
	}
}