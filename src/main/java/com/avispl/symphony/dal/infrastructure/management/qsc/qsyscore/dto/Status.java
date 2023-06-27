/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Status contains Status of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	private float code;
	private String name;

	/**
	 * Retrieves {@link #code}
	 *
	 * @return value of {@link #code}
	 */
	public float getCode() {
		return code;
	}

	/**
	 * Sets {@link #code} value
	 *
	 * @param code new value of {@link #code}
	 */
	public void setCode(float code) {
		this.code = code;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}
}