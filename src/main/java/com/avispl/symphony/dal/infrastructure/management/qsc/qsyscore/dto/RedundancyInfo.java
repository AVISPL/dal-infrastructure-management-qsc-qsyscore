/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Redundancy contain all redundancy information of device
 *
 * @author Harry / Symphony Dev Team
 * @since 1.0.1
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedundancyInfo {
	public boolean backupActive;
	public String backupName;
	public String primaryName;
	public boolean primaryActive;

	/**
	 * Retrieves {@link #backupActive}
	 *
	 * @return value of {@link #backupActive}
	 */
	public boolean isBackupActive() {
		return backupActive;
	}

	/**
	 * Set {@link #backupActive} value
	 *
	 * @param backupActive new value of {@link #backupActive}
	 */
	public void setBackupActive(boolean backupActive) {
		this.backupActive = backupActive;
	}

	/**
	 * Retrieves {@link #backupName}
	 *
	 * @return value of {@link #backupName}
	 */
	public String getBackupName() {
		return backupName;
	}

	/**
	 * Set {@link #backupName} value
	 *
	 * @param backupName new value of {@link #backupName}
	 */
	public void setBackupName(String backupName) {
		this.backupName = backupName;
	}

	/**
	 * Retrieves {@link #primaryName}
	 *
	 * @return value of {@link #primaryName}
	 */
	public String getPrimaryName() {
		return primaryName;
	}

	/**
	 * Set {@link #primaryName} value
	 *
	 * @param primaryName new value of {@link #primaryName}
	 */
	public void setPrimaryName(String primaryName) {
		this.primaryName = primaryName;
	}

	/**
	 * Retrieves {@link #primaryActive}
	 *
	 * @return value of {@link #primaryActive}
	 */
	public boolean isPrimaryActive() {
		return primaryActive;
	}

	/**
	 * Set {@link #primaryActive} value
	 *
	 * @param primaryActive new value of {@link #primaryActive}
	 */
	public void setPrimaryActive(boolean primaryActive) {
		this.primaryActive = primaryActive;
	}
}
