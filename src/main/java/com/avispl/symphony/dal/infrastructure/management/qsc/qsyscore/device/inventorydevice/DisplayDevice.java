package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.DisplayDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;

/**
 * DisplayDevice
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/18/2023
 * @since 1.0.0
 */
public class DisplayDevice extends QSYSPeripheralDevice {
	@Override
	public void controlDevice(JsonNode response) {

	}

	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		if (deviceControl.hasNonNull(QSYSCoreConstant.RESULT) && deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				DisplayDeviceMetric metric = DisplayDeviceMetric.getByProperty(control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (metric == null) {
					if (control.get(QSYSCoreConstant.CONTROL_NAME).asText().startsWith(DisplayDeviceMetric.CHANNEL.getMetric())) {
						String[] splitNamed = control.get(QSYSCoreConstant.CONTROL_NAME).asText().split("\\.", 3);
						String groupName = DisplayDeviceMetric.CHANNEL.getMetric() + splitNamed[1] + QSYSCoreConstant.HASH;
						if (splitNamed.length >= 3 && splitNamed[2].equals("")) {
							switch (splitNamed[2]) {
								case "digital.output.level":
									this.getStats().put(groupName + "PeakInputLevel(dB)", control.hasNonNull("Value") ? control.get("Value").asText() : "None");
									break;
								case "output.gain":
									this.getStats().put(groupName + "GainCurrentValue(dB)",
											control.hasNonNull("Value") ? control.get("Value").asText() : "None");
									break;
							}
						}
					}
					continue;
				}
				this.getStats().put(metric.getMetric(), control.hasNonNull("String") ? control.get("String").asText() : "None");
			}
		}
	}
}