package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.config;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/16/2023
 * @since 1.0.0
 */
public class ObjectMapperSingleton {
	private static ObjectMapper objectMapper = null;

	public static ObjectMapper getInstance() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}
}