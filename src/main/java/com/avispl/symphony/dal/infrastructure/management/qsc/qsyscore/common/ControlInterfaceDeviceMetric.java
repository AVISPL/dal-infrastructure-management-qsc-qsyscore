package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Objects;

/**
 * ControlInterfaceDeviceMetric
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/22/2023
 * @since 1.0.0
 */
public enum ControlInterfaceDeviceMetric {
	STATUS("Status", "status"),
	STATUS_LED("StatusLed", "status.led"),
	MEMORY_USAGE("MemoryUsage(%)", "memory.usage"),
	UCI_CURRENT_UCI("UCICurrentUCI", "current.uci"),
	SCREEN_TOUCH_ACTIVITY("ScreenTouchActivity", "touched"),
	SCREEN_BLACKLIGHT_CURRENT_VALUE("ScreenBlacklightcurrentvalue(%)", "screen.brightness");

	private final String metric;
	private final String property;

	ControlInterfaceDeviceMetric(String metric, String property) {
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
	 * Get metric of metric from QSYSCoreControllingMetric
	 *
	 * @param property property of metric
	 * @return Enum of QSYSCoreControllingMetric
	 */
	public static ControlInterfaceDeviceMetric getByProperty(String property) {
		for (ControlInterfaceDeviceMetric controllingMetric : ControlInterfaceDeviceMetric.values()) {
			if (Objects.equals(controllingMetric.getProperty(), property)) {
				return controllingMetric;
			}
		}
		return null;
	}
}
