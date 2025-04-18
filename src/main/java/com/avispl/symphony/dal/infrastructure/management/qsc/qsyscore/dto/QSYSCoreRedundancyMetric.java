/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

/**
 * QSYSCoreMonitoringMetric contain all metric of device
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.1
 */
public enum QSYSCoreRedundancyMetric {
	BACKUP_ACTIVE("BackupActive"),
	PRIMARY_ACTIVE("PrimaryActive"),
	PRIMARY_NAME("PrimaryName"),
	BACKUP_NAME("BackupName");

	private final String name;

	/**
	 * QSYSCoreRedundancyMetric constructor
	 *
	 * @param name Name of QSYS Core monitoring metric
	 */
	QSYSCoreRedundancyMetric(String name) {
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
