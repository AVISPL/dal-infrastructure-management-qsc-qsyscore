/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * RedundancyWrapper contain redundancy information
 *
 * @author Harry / Symphony Dev Team<br>
 * @since 1.0.1
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedundancyWrapper {

	@JsonAlias("redundancy")
	public RedundancyInfo redundancyInfos;

	/**
	 * Retrieves {@link #redundancyInfos}
	 *
	 * @return value of {@link #redundancyInfos}
	 */
	public RedundancyInfo getRedundancyInfos() {
		return redundancyInfos;
	}

	/**
	 * Sets {@link #redundancyInfos} value
	 *
	 * @param redundancyInfos new value of {@link #redundancyInfos}
	 */
	public void setRedundancyInfos(RedundancyInfo redundancyInfos) {
		this.redundancyInfos = redundancyInfos;
	}

	/**
	 * Get value of monitoring property by metric name
	 *
	 * @param redundancyMetric is QSYSCoreRedundancyMetric instance
	 * @return String is value of the metric
	 */
	public String getValueByMetricName(QSYSCoreRedundancyMetric redundancyMetric) {
		if(null == this.getRedundancyInfos()){
			redundancyInfos = new RedundancyInfo();
		}
		switch (redundancyMetric) {
			case BACKUP_ACTIVE:
				return String.valueOf(redundancyInfos.isBackupActive());
			case BACKUP_NAME:
				return redundancyInfos.getBackupName();
			case PRIMARY_ACTIVE:
				return String.valueOf(redundancyInfos.isPrimaryActive());
			case PRIMARY_NAME:
				return redundancyInfos.getPrimaryName();
			default:
				throw new IllegalArgumentException("The property name doesn't support" + redundancyMetric.getName());
		}
	}
}
