/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DesignInfo contail DesignResult
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
}