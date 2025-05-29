/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DesignInfo contain DesignResult
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DesignInfo {
	DesignResult result;

	/**
	 * Retrieves {@link #result}
	 *
	 * @return value of {@link #result}
	 */
	public DesignResult getResult() {
		return result;
	}

	/**
	 * Sets {@link #result} value
	 *
	 * @param result new value of {@link #result}
	 */
	public void setResult(DesignResult result) {
		this.result = result;
	}

	/**
	 * Get value of monitoring property by metric name
	 *
	 * @param qsysCoreDesignMetric is QSYSCoreDesignMetric instance
	 * @return String is value of the metric
	 */
	public String getValueByMetricName(QSYSCoreDesignMetric qsysCoreDesignMetric) {

		switch (qsysCoreDesignMetric) {
			case STATE:
				return result.getState();
			case DESIGN_NAME:
				return result.getDesignName();
			case PLATFORM:
				return result.getPlatform();
			case DESIGN_CODE:
				return result.getDesignCode();
			case IS_REDUNDANCY:
				String redundancy;
				if("true".equals(result.getIsRedundant())){
					redundancy = "Enabled";
				} else {
					redundancy = "Disabled";
				}
				return redundancy;
			default:
				throw new IllegalArgumentException("The property name doesn't support" + qsysCoreDesignMetric.getName());
		}
	}
}