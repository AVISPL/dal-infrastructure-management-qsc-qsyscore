package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Firmware
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

	// Getter Methods

	public String getName() {
		return name;
	}

	public String getBuild() {
		return build;
	}

	public String getBuildName() {
		return buildName;
	}

	public boolean getIsRelease() {
		return isRelease;
	}

	public boolean getIsSigned() {
		return isSigned;
	}

	// Setter Methods

	public void setName(String name) {
		this.name = name;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

	public void setIsRelease(boolean isRelease) {
		this.isRelease = isRelease;
	}

	public void setIsSigned(boolean isSigned) {
		this.isSigned = isSigned;
	}
}