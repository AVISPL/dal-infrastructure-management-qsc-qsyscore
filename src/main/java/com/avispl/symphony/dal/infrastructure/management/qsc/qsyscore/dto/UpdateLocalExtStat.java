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
	 *
	 * @param property property of control
	 * @param value value of control
	 * @param namedComponent name of component
	 * @param controllingMetric control type
	 */
	public UpdateLocalExtStat(String property, String value, String namedComponent, GainControllingMetric controllingMetric) {
		this.property = property;
		this.value = value;
		this.namedComponent = namedComponent;
		this.controllingMetric = controllingMetric;
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
	 * Sets {@link #property} value
	 *
	 * @param property new value of {@link #property}
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets {@link #value} value
	 *
	 * @param value new value of {@link #value}
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Retrieves {@link #namedComponent}
	 *
	 * @return value of {@link #namedComponent}
	 */
	public String getNamedComponent() {
		return namedComponent;
	}

	/**
	 * Sets {@link #namedComponent} value
	 *
	 * @param namedComponent new value of {@link #namedComponent}
	 */
	public void setNamedComponent(String namedComponent) {
		this.namedComponent = namedComponent;
	}

	/**
	 * Retrieves {@link #controllingMetric}
	 *
	 * @return value of {@link #controllingMetric}
	 */
	public GainControllingMetric getControllingMetric() {
		return controllingMetric;
	}

	/**
	 * Sets {@link #controllingMetric} value
	 *
	 * @param controllingMetric new value of {@link #controllingMetric}
	 */
	public void setControllingMetric(GainControllingMetric controllingMetric) {
		this.controllingMetric = controllingMetric;
	}
}