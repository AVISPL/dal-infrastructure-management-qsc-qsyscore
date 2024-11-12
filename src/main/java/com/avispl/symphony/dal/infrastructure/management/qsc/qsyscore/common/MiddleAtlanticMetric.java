/*
 * Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * MiddleAtlanticMetric save all metric fields and corresponding response fields of the MiddleAtlantic device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 10/22/2024
 * @since 1.0.2
 */
public enum MiddleAtlanticMetric {

    STATUS("Status", "Status"),
    SERIAL_NUMBER("SerialNumber", "SerialNumber"),
    DEVICE_MODEL("DeviceModel", "Model"),
    IP_ADDRESS("IPAddress", "IPAddress"),
    MAC_ADDRESS("MACAddress", "MACAddress"),
    DEVICE_FIRMWARE("DeviceFirmware", "DeviceFirmware"),
    OUTPUT_SOURCE("OutputSource", "OutputSource"),
    INPUT_VOLTAGE("InputVoltage(V)", "InputVoltage"),
    INPUT_CURRENT("InputCurrent(A)", "InputCurrent"),
    OUTPUT_VOLTAGE("OutputVoltage(V)", "OutputVoltage"),
    OUTPUT_CURRENT("OutputCurrent(A)", "OutputCurrent"),
    OUTPUT_POWER("OutputPower(W)", "OutputPower"),
    ALARM_OVER_TEMPERATURE("Alarm#OverTemperature", "AlarmOverTemperature"),
    ALARM_OVERLOAD("Alarm#Overload", "AlarmOverload"),
    ALARM_OUTPUT_OFF("Alarm#OutputOff", "AlarmOutputOff"),
    ALARM_UPS_SHUTDOWN("Alarm#UPSShutdown", "AlarmUPSShutdown"),
    ALARM_CHARGER_FAIL("Alarm#ChargerFail", "AlarmChargerFail"),
    ALARM_FAN_FAIL("Alarm#FanFail", "AlarmFanFail"),
    ALARM_FUSE_FAIL("Alarm#FuseFail", "AlarmFuseFail"),
    ALARM_BATTERY_GROUND_FAULT("Alarm#BatteryGroundFault", "AlarmBatteryGroundFault"),
    ALARM_BATTERY_FAULT("Alarm#BatteryFault", "AlarmBatteryFault"),

    // Battery metrics
    BATTERY_CONDITION("Battery#Condition", "BatteryCondition"),
    BATTERY_STATUS("Battery#Status", "BatteryStatus"),
    BATTERY_CHARGE("Battery#Charge", "BatteryCharge"),
    BATTERY_EST_MIN_REMAINING("Battery#EstMinRemaining", "BatteryEstMinRemaining"),
    BATTERY_INTERNAL_TEMPERATURE("Battery#InternalTemperature(C)", "BatteryInternalTemperature"),
    BATTERY_LEVEL("Battery#Level(%)", "BatteryLevel"),
    BATTERY_VOLTAGE("Battery#Voltage(V)", "BatteryVoltage"),
    BATTERY_SECONDS_ON_BATTERY("Battery#SecondsOnBattery", "BatterySecondsOnBattery"),
    BATTERY_LAST_REPLACEMENT_DATE("Battery#LastReplacementDate", "BatteryLastReplacementDate"),
    BATTERY_NEXT_REPLACEMENT_DATE("Battery#NextReplacementDate", "BatteryNextReplacementDate"),

    // Control properties
    RESTART_UPS("RestartUPS", "RestartUPS"),
    RESTART_UPS_DELAY_TIME("RestartUPSDelayTime(s)", "RestartUPSDelayTime"),
    OUTLET_1_CYCLE("Outlet1#Cycle", "Outlet1Cycle"),
    OUTLET_1_CYCLE_TIME("Outlet1#CycleTime(s)", "Outlet1CycleTime"),
    OUTLET_1_POWER("Outlet1#Power", "Outlet1Power"),
    OUTLET_1_STATE("Outlet1#State", "Outlet1State"),
    OUTLET_1_NAME("Outlet1#Name", "Outlet1Name"),
    OUTLET_2_CYCLE("Outlet2#Cycle", "Outlet2Cycle"),
    OUTLET_2_CYCLE_TIME("Outlet2#CycleTime(s)", "Outlet2CycleTime"),
    OUTLET_2_POWER("Outlet2#Power", "Outlet2Power"),
    OUTLET_2_STATE("Outlet2#State", "Outlet2State"),
    OUTLET_2_NAME("Outlet2#Name", "Outlet2Name"),
    OUTLET_3_CYCLE("Outlet3#Cycle", "Outlet3Cycle"),
    OUTLET_3_CYCLE_TIME("Outlet3#CycleTime(s)", "Outlet3CycleTime"),
    OUTLET_3_POWER("Outlet3#Power", "Outlet3Power"),
    OUTLET_3_STATE("Outlet3#State", "Outlet3State"),
    OUTLET_3_NAME("Outlet3#Name", "Outlet3Name"),
    OUTLET_4_CYCLE("Outlet4#Cycle", "Outlet4Cycle"),
    OUTLET_4_CYCLE_TIME("Outlet4#CycleTime(s)", "Outlet4CycleTime"),
    OUTLET_4_POWER("Outlet4#Power", "Outlet4Power"),
    OUTLET_4_STATE("Outlet4#State", "Outlet4State"),
    OUTLET_4_NAME("Outlet4#Name", "Outlet4Name"),
    OUTLET_5_CYCLE("Outlet5#Cycle", "Outlet5Cycle"),
    OUTLET_5_CYCLE_TIME("Outlet5#CycleTime(s)", "Outlet5CycleTime"),
    OUTLET_5_POWER("Outlet5#Power", "Outlet5Power"),
    OUTLET_5_STATE("Outlet5#State", "Outlet5State"),
    OUTLET_5_NAME("Outlet5#Name", "Outlet5Name"),
    OUTLET_6_CYCLE("Outlet6#Cycle", "Outlet6Cycle"),
    OUTLET_6_CYCLE_TIME("Outlet6#CycleTime(s)", "Outlet6CycleTime"),
    OUTLET_6_POWER("Outlet6#Power", "Outlet6Power"),
    OUTLET_6_STATE("Outlet6#State", "Outlet6State"),
    OUTLET_6_NAME("Outlet6#Name", "Outlet6Name"),
    OUTLET_7_CYCLE("Outlet7#Cycle", "Outlet7Cycle"),
    OUTLET_7_CYCLE_TIME("Outlet7#CycleTime(s)", "Outlet7CycleTime"),
    OUTLET_7_POWER("Outlet7#Power", "Outlet7Power"),
    OUTLET_7_STATE("Outlet7#State", "Outlet7State"),
    OUTLET_7_NAME("Outlet7#Name", "Outlet7Name"),
    OUTLET_8_CYCLE("Outlet8#Cycle", "Outlet8Cycle"),
    OUTLET_8_CYCLE_TIME("Outlet8#CycleTime(s)", "Outlet8CycleTime"),
    OUTLET_8_POWER("Outlet8#Power", "Outlet8Power"),
    OUTLET_8_STATE("Outlet8#State", "Outlet8State"),
    OUTLET_8_NAME("Outlet8#Name", "Outlet8Name"),
    BUZZER_ENABLE("Buzzer#EnableToggle", "BuzzerEnable"),
    BUZZER_ENABLE_STATUS("Buzzer#EnableStatus", "BuzzerEnableStatus"),
    BUZZER_SILENCE_TRIGGER("Buzzer#SilenceUntilNextAlarmTrigger", "BuzzerSilenceUntilNextAlarm"),
    BUZZER_SILENCE_STATUS("Buzzer#SilenceUntilNextAlarmStatus", "BuzzerAlarmStatus");
    private final String metric;
    private final String property;

    /**
     * Parameterized constructor
     *
     * @param metric   metric that shows on UI
     * @param property corresponding response field
     */
    MiddleAtlanticMetric(String metric, String property) {
        this.metric = metric;
        this.property = property;
    }

    /**
     * Retrieves {@link #metric}
     *
     * @return value of {@link #metric}
     */
    public String getMetric() {
        return metric;
    }

    /**
     * Retrieves {@link #property}
     *
     * @return value of {@link #property}
     */
    public String getProperty() {
        return property;
    }

    /**
     * Get metric of metric from MiddleAtlanticMetric
     *
     * @param metric metric of metric
     * @return Enum of MiddleAtlanticMetric
     */
    public static MiddleAtlanticMetric getByMetric(String metric) {
        for (MiddleAtlanticMetric controllingMetric : values()) {
            if (Objects.equals(controllingMetric.getMetric(), metric)) {
                return controllingMetric;
            }
        }
        throw new IllegalArgumentException("Cannot find the enum with metric: " + metric);
    }
}
