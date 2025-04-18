/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DesignResult contain all information of Design
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DesignResult {
	@JsonAlias("Platform")
	private String platform;
	@JsonAlias("State")
	private String state;
	@JsonAlias("DesignName")
	private String designName;
	@JsonAlias("DesignCode")
	private String designCode;
	@JsonAlias("IsRedundant")
	private String isRedundant;

	/**
	 * Retrieves {@link #platform}
	 *
	 * @return value of {@link #platform}
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * Sets {@link #platform} value
	 *
	 * @param platform new value of {@link #platform}
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
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

	/**
	 * Retrieves {@link #designName}
	 *
	 * @return value of {@link #designName}
	 */
	public String getDesignName() {
		return designName;
	}

	/**
	 * Sets {@link #designName} value
	 *
	 * @param designName new value of {@link #designName}
	 */
	public void setDesignName(String designName) {
		this.designName = designName;
	}

	/**
	 * Retrieves {@link #designCode}
	 *
	 * @return value of {@link #designCode}
	 */
	public String getDesignCode() {
		return designCode;
	}

	/**
	 * Sets {@link #designCode} value
	 *
	 * @param designCode new value of {@link #designCode}
	 */
	public void setDesignCode(String designCode) {
		this.designCode = designCode;
	}

	/**
	 * Retrieves {@link #isRedundant}
	 *
	 * @return value of {@link #isRedundant}
	 */
	public String getIsRedundant() {
		return isRedundant;
	}

	/**
	 * Sets {@link #isRedundant} value
	 *
	 * @param isRedundant new value of {@link #isRedundant}
	 */
	public void setIsRedundant(String isRedundant) {
		this.isRedundant = isRedundant;
	}
}