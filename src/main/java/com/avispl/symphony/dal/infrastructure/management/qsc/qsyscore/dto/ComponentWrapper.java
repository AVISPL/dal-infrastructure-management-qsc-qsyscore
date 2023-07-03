/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ComponentWrapper contail list all componentInfo
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/16/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentWrapper {
	List<ComponentInfo> result;

	/**
	 * Retrieves {@link #result}
	 *
	 * @return value of {@link #result}
	 */
	public List<ComponentInfo> getResult() {
		return result;
	}

	/**
	 * Sets {@link #result} value
	 *
	 * @param result new value of {@link #result}
	 */
	public void setResult(List<ComponentInfo> result) {
		this.result = result;
	}
}