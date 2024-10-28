/*
 * Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.*;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.ControllablePropertyFactory;
import com.avispl.symphony.dal.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * SennheiserDevice class to implement monitoring and controlling for Sennheiser device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 10/22/2024
 * @since 1.0.2
 */
public class SennheiserDevice extends QSYSPeripheralDevice {
	@Override
	public void controlDevice(JsonNode response) {

	}

	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		this.getStats().clear();
		this.getAdvancedControllableProperties().clear();
		if (deviceControl.hasNonNull(QSYSCoreConstant.RESULT) && deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				SennheiserDeviceMetric sennheiserDeviceMetric = EnumTypeHandler.getMetricByName(SennheiserDeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (sennheiserDeviceMetric == null) {
					continue;
				}
				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				this.getStats().put(sennheiserDeviceMetric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
				switch (sennheiserDeviceMetric) {
					case AUDIO_LEVEL:
						value = value.substring(0, value.indexOf("dB"));
						this.getStats().put(sennheiserDeviceMetric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createSlider(sennheiserDeviceMetric.getMetric(), -120f, 12f, Float.parseFloat(value)));
						this.getStats().put(QSYSCoreConstant.AUDIO_LEVEL_CURRENT_VALUE, StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
					case LED_BRIGHTNESS:
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createSlider(sennheiserDeviceMetric.getMetric(), 0f, 5f, Float.parseFloat(value)));
						this.getStats().put(QSYSCoreConstant.LED_BRIGHTNESS_CURRENT_VALUE, StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
					case LED_ON_OFF:
					case MUTE:
					case TRU_VOICE_LIFT:
					case NOISE_GATE:
					case PRIORITY_ZONE:
						int ledOnOff = QSYSCoreConstant.TRUE.equalsIgnoreCase(value) ? 1 : 0;
						this.getAdvancedControllableProperties().add(createSwitch(sennheiserDeviceMetric.getMetric(), ledOnOff, "Deactivated", "Activated"));
						break;
					case EXCLUSION_1_ZONES:
					case EXCLUSION_2_ZONES:
					case EXCLUSION_3_ZONES:
					case EXCLUSION_4_ZONES:
					case EXCLUSION_5_ZONES:
						int exclusion = QSYSCoreConstant.TRUE.equalsIgnoreCase(value) ? 1 : 0;
						this.getAdvancedControllableProperties().add(createSwitch(sennheiserDeviceMetric.getMetric(), exclusion, "Off", "On"));
						break;
					case SOUND_PROFILE:
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createDropdown(sennheiserDeviceMetric.getMetric(), QSYSCoreConstant.SOUND_PROFILE, value));
						break;
					case NOISE_LEVEL:
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createDropdown(sennheiserDeviceMetric.getMetric(), QSYSCoreConstant.NOISE_LEVEL, value));
						break;
					case ON_COLOR:
					case MUTE_COLOR:
						this.getAdvancedControllableProperties().add(ControllablePropertyFactory.createDropdown(sennheiserDeviceMetric.getMetric(), QSYSCoreConstant.ON_COLOR, value));
						break;
					default:
						break;
				}
			}
			super.updateStatusMessage();
		}
	}
}
