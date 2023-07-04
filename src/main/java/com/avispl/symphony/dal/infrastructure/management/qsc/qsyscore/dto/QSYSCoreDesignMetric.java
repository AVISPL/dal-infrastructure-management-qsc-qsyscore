/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

/**
 * QSYSCoreDesignMetric contain all metric of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
public enum QSYSCoreDesignMetric {

	DESIGN_NAME("DesignName"),
	DESIGN_CODE("DesignCode"),
	PLATFORM("Platform"),
	STATE("State");

	private final String name;

	/**
	 * QSYSCoreDesignMetric constructor
	 *
	 * @param name Name of QSYS Core monitoring metric
	 */
	QSYSCoreDesignMetric(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}