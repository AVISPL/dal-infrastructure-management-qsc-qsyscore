/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.GainControllingMetric;

/**
 * UpdateLocalExtStat store information when control gain
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/25/2023
 * @since 1.0.0
 */
public class UpdateLocalExtStat {
	private String property;
	private String value;
	private String namedComponent;
	private GainControllingMetric controllingMetric;

	/**
	 * Parameterized constructor
	 */
	public UpdateLocalExtStat(String property, String value, String namedComponent, GainControllingMetric controllingMetric) {
		this.property = property;
		this.value = value;
		this.namedComponent = namedComponent;
		this.controllingMetric = controllingMetric;
	}

	/**
	 * Retrieves {@code {@link #property}}
	 *
	 * @return value of {@link #property}
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Sets {@code property}
	 *
	 * @param property the {@code java.lang.String} field
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets {@code value}
	 *
	 * @param value the {@code java.lang.String} field
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Retrieves {@code {@link #namedComponent}}
	 *
	 * @return value of {@link #namedComponent}
	 */
	public String getNamedComponent() {
		return namedComponent;
	}

	/**
	 * Sets {@code namedComponent}
	 *
	 * @param namedComponent the {@code java.lang.String} field
	 */
	public void setNamedComponent(String namedComponent) {
		this.namedComponent = namedComponent;
	}

	/**
	 * Retrieves {@code {@link #controllingMetric}}
	 *
	 * @return value of {@link #controllingMetric}
	 */
	public GainControllingMetric getControllingMetric() {
		return controllingMetric;
	}

	/**
	 * Sets {@code controllingMetric}
	 *
	 * @param controllingMetric the {@code com.avispl.symphony.dal.device.core510i.common.QSYSCoreControllingMetric} field
	 */
	public void setControllingMetric(GainControllingMetric controllingMetric) {
		this.controllingMetric = controllingMetric;
	}
}