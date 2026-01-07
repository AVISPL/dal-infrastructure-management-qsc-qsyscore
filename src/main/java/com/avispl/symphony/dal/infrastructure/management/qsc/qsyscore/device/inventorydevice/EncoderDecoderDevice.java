/*
 * Copyright (c) 2026 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice;

import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EncoderDecoderDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NV-21 device, responsible for monitoring data mapping and control.
 *
 * @author Maksym.Rossiitsev / Symphony Dev Team<br>
 * Created on 1/7/2026
 * @since 1.2.1
 * */
public class EncoderDecoderDevice extends QSYSPeripheralDevice {
    @Override
    public void controlDevice(QSYSPeripheralDevice device, String metric, String value, String metricName) {
        throw new UnsupportedOperationException("Operation not supported yet.");
    }

    @Override
    public void monitoringDevice(JsonNode deviceControl) {
        try {
            this.getStats().clear();
            this.getAdvancedControllableProperties().clear();
            if (!deviceControl.hasNonNull(QSYSCoreConstant.RESULT) ||
                    !deviceControl.get(QSYSCoreConstant.RESULT).hasNonNull(QSYSCoreConstant.CONTROLS)) {
                return;
            }
            for (JsonNode control : deviceControl.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS)) {
                String controlName = Optional.ofNullable(control.get(QSYSCoreConstant.CONTROL_NAME))
                        .map(JsonNode::asText)
                        .orElse(null);
                if (controlName == null) {
                    continue;
                }
                Pattern numericPattern = Pattern.compile(".*(\\d\\.).*");
                Matcher matcher = numericPattern.matcher(controlName);
                EncoderDecoderDeviceMetric metric = null;
                if (matcher.matches()) {
                    String indexNumber = matcher.group(1).replace(".", "");
                    metric = EnumTypeHandler.getMetricByName(EncoderDecoderDeviceMetric.class, controlName.replace(indexNumber, "%s"));
                    if (metric != null) {
                        //Need to change index placeholder to an actual index
                        metric.setMetric(String.format(metric.getMetric(), indexNumber));
                        metric.setProperty(String.format(metric.getProperty(), indexNumber));
                    }
                }
                if (metric == null) {
                    metric = EnumTypeHandler.getMetricByName(EncoderDecoderDeviceMetric.class, controlName);
                }
                if (metric == null) {
                    continue;
                }
                String type = Optional.ofNullable(control.get(QSYSCoreConstant.CONTROL_TYPE)).map(JsonNode::asText).orElse("");
                String value;

                switch (type) {
                    case QSYSCoreConstant.TYPE_FLOAT:
                        value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE) ? String.valueOf(control.get(QSYSCoreConstant.CONTROL_VALUE).asDouble()) : QSYSCoreConstant.DEFAUL_DATA;
                        break;
                    case QSYSCoreConstant.TYPE_BOOLEAN:
                        value = control.hasNonNull(QSYSCoreConstant.CONTROL_VALUE) ? (control.get(QSYSCoreConstant.CONTROL_VALUE).asBoolean() ? QSYSCoreConstant.TRUE : QSYSCoreConstant.FALSE) : QSYSCoreConstant.DEFAUL_DATA;
                        break;
                    default:
                        value = Optional.ofNullable(control.get(QSYSCoreConstant.CONTROL_VALUE_STRING)).map(JsonNode::asText).orElse(QSYSCoreConstant.DEFAUL_DATA);
                        break;
                }
                switch (metric){
                    default:
                        this.getStats().put(metric.getMetric(), StringUtils.isNotNullOrEmpty(value) ? uppercaseFirstCharacter(value) : QSYSCoreConstant.DEFAUL_DATA);
                        break;
                }
            }
            super.updateStatusMessage();
        } catch (Exception e){
            throw new ResourceNotReachableException("Error occurred while monitoring device control: " + e.getMessage(), e);
        }
    }
}
