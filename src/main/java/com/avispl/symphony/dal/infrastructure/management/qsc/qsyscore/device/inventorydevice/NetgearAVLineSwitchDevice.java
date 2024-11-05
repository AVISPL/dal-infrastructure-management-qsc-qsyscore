package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.NetgearDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * NetgearAVLineSwitchDevice class to implement monitoring and controlling for Netgear device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 11/01/2024
 * @since 1.0.2
 */
public class NetgearAVLineSwitchDevice extends QSYSPeripheralDevice {
	@Override
	public void controlDevice(JsonNode response) {

	}

	@Override
	public void monitoringDevice(JsonNode deviceControl) {
		this.getStats().clear();
		this.getAdvancedControllableProperties().clear();
		if (deviceControl.hasNonNull(QSYSCoreConstant.RESULT) && deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
			for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
				NetgearDeviceMetric netgearDeviceMetric = EnumTypeHandler.getMetricByName(NetgearDeviceMetric.class, control.get(QSYSCoreConstant.CONTROL_NAME).asText());
				if (netgearDeviceMetric == null) {
					continue;
				}
				String value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE_STRING) ? control.get(QSYSCoreConstant.CONTROL_VALUE_STRING).asText() : QSYSCoreConstant.DEFAUL_DATA;
				switch (netgearDeviceMetric) {
					case PORT_1:
					case PORT_2:
					case PORT_3:
					case PORT_4:
					case PORT_5:
					case PORT_6:
					case PORT_7:
					case PORT_8:
					case PORT_9:
					case PORT_10:
					case PORT_11:
					case PORT_12:
						this.getStats().put(netgearDeviceMetric.getMetric(), StringUtils.isNotNullOrEmpty(value) && !QSYSCoreConstant.DEFAUL_DATA.equals(value) ? value : "");
						break;
					default:
						this.getStats().put(netgearDeviceMetric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? value : QSYSCoreConstant.DEFAUL_DATA);
						break;
				}
			}
			super.updateStatusMessage();
		}
	}
}
