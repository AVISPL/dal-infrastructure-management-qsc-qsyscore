package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.fasterxml.jackson.databind.JsonNode;

public class EncoderDecoderDevice extends QSYSPeripheralDevice {
    @Override
    public void controlDevice(QSYSPeripheralDevice device, String metric, String value, String metricName) {

    }

    @Override
    public void monitoringDevice(JsonNode deviceControl) {

    }
}
