package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Status
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	private float code;
	private String name;

	// Getter Methods

	public float getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	// Setter Methods

	public void setCode(float code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}
}