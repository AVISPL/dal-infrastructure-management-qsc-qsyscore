package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Result
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {
	private String Platform;
	private String State;
	private String DesignName;
	private String DesignCode;
	private boolean IsRedundant;
	private boolean IsEmulator;

	/**
	 * Retrieves {@link #Platform}
	 *
	 * @return value of {@link #Platform}
	 */
	public String getPlatform() {
		return Platform;
	}

	/**
	 * Sets {@link #Platform} value
	 *
	 * @param Platform new value of {@link #Platform}
	 */
	public void setPlatform(String platform) {
		Platform = platform;
	}

	/**
	 * Retrieves {@link #State}
	 *
	 * @return value of {@link #State}
	 */
	public String getState() {
		return State;
	}

	/**
	 * Sets {@link #State} value
	 *
	 * @param State new value of {@link #State}
	 */
	public void setState(String state) {
		State = state;
	}

	/**
	 * Retrieves {@link #DesignName}
	 *
	 * @return value of {@link #DesignName}
	 */
	public String getDesignName() {
		return DesignName;
	}

	/**
	 * Sets {@link #DesignName} value
	 *
	 * @param DesignName new value of {@link #DesignName}
	 */
	public void setDesignName(String designName) {
		DesignName = designName;
	}

	/**
	 * Retrieves {@link #DesignCode}
	 *
	 * @return value of {@link #DesignCode}
	 */
	public String getDesignCode() {
		return DesignCode;
	}

	/**
	 * Sets {@link #DesignCode} value
	 *
	 * @param DesignCode new value of {@link #DesignCode}
	 */
	public void setDesignCode(String designCode) {
		DesignCode = designCode;
	}

	/**
	 * Retrieves {@link #IsRedundant}
	 *
	 * @return value of {@link #IsRedundant}
	 */
	public boolean isRedundant() {
		return IsRedundant;
	}

	/**
	 * Sets {@link #IsRedundant} value
	 *
	 * @param IsRedundant new value of {@link #IsRedundant}
	 */
	public void setRedundant(boolean redundant) {
		IsRedundant = redundant;
	}

	/**
	 * Retrieves {@link #IsEmulator}
	 *
	 * @return value of {@link #IsEmulator}
	 */
	public boolean isEmulator() {
		return IsEmulator;
	}

	/**
	 * Sets {@link #IsEmulator} value
	 *
	 * @param IsEmulator new value of {@link #IsEmulator}
	 */
	public void setEmulator(boolean emulator) {
		IsEmulator = emulator;
	}
}