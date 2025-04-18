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
public class Redundancy {
	private String role;
	private String state;

	/**
	 * Retrieves {@link #role}
	 *
	 * @return value of {@link #role}
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets {@link #role} value
	 *
	 * @param role new value of {@link #role}
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Retrieves {@link #state}
	 *
	 * @return value of {@link #state}
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets {@link #state} value
	 *
	 * @param state new value of {@link #state}
	 */
	public void setState(String state) {
		this.state = state;
	}
}
