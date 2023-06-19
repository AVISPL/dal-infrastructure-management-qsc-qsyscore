package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DesignStatus
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DesignInfo {
	@JsonAlias("params")
	Result result;

	/**
	 * Retrieves {@link #result}
	 *
	 * @return value of {@link #result}
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * Sets {@link #result} value
	 *
	 * @param result new value of {@link #result}
	 */
	public void setResult(Result result) {
		this.result = result;
	}
}