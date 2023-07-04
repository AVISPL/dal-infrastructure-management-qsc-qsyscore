/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Firmware contain all firmware information of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Firmware {
	private String name;
	private String build;
	private String buildName;
	private boolean isRelease;
	private boolean isSigned;

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

	/**
	 * Retrieves {@link #build}
	 *
	 * @return value of {@link #build}
	 */
	public String getBuild() {
		return build;
	}

	/**
	 * Sets {@link #build} value
	 *
	 * @param build new value of {@link #build}
	 */
	public void setBuild(String build) {
		this.build = build;
	}

	/**
	 * Retrieves {@link #buildName}
	 *
	 * @return value of {@link #buildName}
	 */
	public String getBuildName() {
		return buildName;
	}

	/**
	 * Sets {@link #buildName} value
	 *
	 * @param buildName new value of {@link #buildName}
	 */
	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

	/**
	 * Retrieves {@link #isRelease}
	 *
	 * @return value of {@link #isRelease}
	 */
	public boolean isRelease() {
		return isRelease;
	}

	/**
	 * Sets {@link #isRelease} value
	 *
	 * @param release new value of {@link #isRelease}
	 */
	public void setRelease(boolean release) {
		isRelease = release;
	}

	/**
	 * Retrieves {@link #isSigned}
	 *
	 * @return value of {@link #isSigned}
	 */
	public boolean isSigned() {
		return isSigned;
	}

	/**
	 * Sets {@link #isSigned} value
	 *
	 * @param signed new value of {@link #isSigned}
	 */
	public void setSigned(boolean signed) {
		isSigned = signed;
	}
}