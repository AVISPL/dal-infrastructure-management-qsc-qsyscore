/*
 * Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.MiddleAtlanticMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.ControllablePropertyFactory;
import com.avispl.symphony.dal.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * MiddleAtlanticDevice class to implement monitoring and controlling for MiddleAtlantic device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 10/22/2024
 * @since 1.0.2
 */
public class MiddleAtlanticDevice extends QSYSPeripheralDevice {
	@Override
	public void controlDevice(JsonNode response) {

	}

	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		this.getStats().clear();
		this.getAdvancedControllableProperties().clear();
		if (deviceControl.hasNonNull(QSYSCoreConstant.RESULT) && deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				MiddleAtlanticMetric middleAtlanticMetric = EnumTypeHandler.getMetricByName(MiddleAtlanticMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (middleAtlanticMetric == null) {
					continue;
				}
				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				this.getStats().put(middleAtlanticMetric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
				switch (middleAtlanticMetric) {
					case OUTLET_1_CYCLE_TIME:
					case OUTLET_2_CYCLE_TIME:
					case OUTLET_3_CYCLE_TIME:
					case OUTLET_4_CYCLE_TIME:
					case OUTLET_5_CYCLE_TIME:
					case OUTLET_6_CYCLE_TIME:
					case OUTLET_7_CYCLE_TIME:
					case OUTLET_8_CYCLE_TIME:

						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createNumeric(middleAtlanticMetric.getMetric(), value));
						break;
					case OUTLET_1_NAME:
					case OUTLET_2_NAME:
					case OUTLET_3_NAME:
					case OUTLET_4_NAME:
					case OUTLET_5_NAME:
					case OUTLET_6_NAME:
					case OUTLET_7_NAME:
					case OUTLET_8_NAME:
					case RESTART_UPS_DELAY_TIME:
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createText(middleAtlanticMetric.getMetric(), value));
						break;
					case OUTLET_1_CYCLE:
					case OUTLET_2_CYCLE:
					case OUTLET_3_CYCLE:
					case OUTLET_4_CYCLE:
					case OUTLET_5_CYCLE:
					case OUTLET_6_CYCLE:
					case OUTLET_7_CYCLE:
					case OUTLET_8_CYCLE:
						this.getStats().put(middleAtlanticMetric.getMetric(), "");
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createButton(middleAtlanticMetric.getMetric(), "Cycle", "Processing", 0L));
						break;
					case RESTART_UPS:
						this.getStats().put(middleAtlanticMetric.getMetric(), "");
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createButton(middleAtlanticMetric.getMetric(), "Reset", "Processing", 0L));
						break;
					case OUTLET_1_POWER:
					case OUTLET_2_POWER:
					case OUTLET_3_POWER:
					case OUTLET_4_POWER:
					case OUTLET_5_POWER:
					case OUTLET_6_POWER:
					case OUTLET_7_POWER:
					case OUTLET_8_POWER:
					case BUZZER_ENABLE:
					case BUZZER_SILENCE_TRIGGER:
						int power = QSYSCoreConstant.TRUE.equalsIgnoreCase(value) ? 1 : 0;
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createSwitch(middleAtlanticMetric.getMetric(), power));
						break;
					case OUTLET_1_STATE:
					case OUTLET_2_STATE:
					case OUTLET_3_STATE:
					case OUTLET_4_STATE:
					case OUTLET_5_STATE:
					case OUTLET_6_STATE:
					case OUTLET_7_STATE:
					case OUTLET_8_STATE:
						this.getStats().put(middleAtlanticMetric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
					default:
						break;
				}
			}
			super.updateStatusMessage();
		}
	}
}
