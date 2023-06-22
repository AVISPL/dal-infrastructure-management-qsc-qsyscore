package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.ProcessorDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;

/**
 * ProcessorDevice
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/21/2023
 * @since 1.0.0
 */
public class ProcessorDevice extends QSYSPeripheralDevice {
	@Override
	public void controlDevice(JsonNode response) {

	}

	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		if (deviceControl.hasNonNull("result") && deviceControl.get("result").hasNonNull("Controls")) {
			for (JsonNode control : deviceControl.get("result").get("Controls")) {
				ProcessorDeviceMetric processor = ProcessorDeviceMetric.getByProperty(control.get("Name").asText());
				if (processor == null) {
					continue;
				}
				switch (processor) {
					case PROCESSOR_TEMPERATURE:
					case SYSTEM_TEMPERATURE:
						this.getStats().put(processor.getMetric(), control.hasNonNull("Value") ? control.get("Value").asText() : "None");
						return;
					default:
						this.getStats().put(processor.getMetric(), control.hasNonNull("String") ? control.get("String").asText() : "None");
				}
			}
		}
	}
}