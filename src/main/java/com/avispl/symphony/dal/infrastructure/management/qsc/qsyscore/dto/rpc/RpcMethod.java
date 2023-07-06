/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.rpc;

/**
 * RpcMethod store all method off QSYS aggregator device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
public enum RpcMethod {
	STATUS_GET("StatusGet"),
	GET_COMPONENTS("Component.GetComponents"),
	GET_CONTROLS("Component.GetControls"),
	GET("Component.Get"),
	SET_CONTROLS("Component.Set");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of method
	 */
	RpcMethod(String name) {
		this.name = name;
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
	 * Get request
	 *
	 * @return request
	 */
	public static String getRequest() {
		return "{\n"
				+ "  \"jsonrpc\": \"2.0\",\n"
				+ "  \"method\": \"%s\", \n"
				+ "  \"params\": %s,\n"
				+ "  \"id\": 1234\n"
				+ "}\n\00";
	}

	/**
	 * Get param of Method
	 *
	 * @param rcpMethod method to get request
	 * @return param
	 */
	public static String getParamsString(RpcMethod rcpMethod) {
		switch (rcpMethod) {
			case GET:
				return " {\n"
						+ "    \"Name\": \"%s\",\n"
						+ "    \"Controls\": [\n"
						+ "      { \"Name\": \"%s\" }\n"
						+ "    ]\n"
						+ "  }";
			case SET_CONTROLS:
				return "{\n"
						+ "    \"Name\": \"%s\",\n"
						+ "    \"Controls\": [\n"
						+ "      {\n"
						+ "        \"Name\": \"%s\", \n"
						+ "        \"Value\": %s\n"
						+ "      }\n"
						+ "    ]\n"
						+ "  }";
			case GET_CONTROLS:
				return "{\"Name\":\"%s\"}";
			case GET_COMPONENTS:
			case STATUS_GET:
			default:
				return "\"\"";
		}
	}
}
