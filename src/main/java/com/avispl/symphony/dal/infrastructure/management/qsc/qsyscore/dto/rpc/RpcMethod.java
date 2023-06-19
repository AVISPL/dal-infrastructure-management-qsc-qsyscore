package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.rpc;

/**
 * RpcMethod
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
public enum RpcMethod {
	STATUS_GET("StatusGet"),
	GET_COMPONENTS("Component.GetComponents"),
	GET_CONTROLS("Component.GetControls"),
	GET("Component.Get");

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

	public static String getRequest() {
		return "{\n"
				+ "  \"jsonrpc\": \"2.0\",\n"
				+ "  \"method\": \"%s\", \n"
				+ "  \"params\": \"%s\",\n"
				+ "  \"id\": 1234\n"
				+ "}\n\00";
	}

	public static String getParamsString(RpcMethod rcpMethod) {
		switch (rcpMethod) {
			case GET_CONTROLS:
				return "{name:%s}";
			case GET_COMPONENTS:
			case STATUS_GET:
			default:
				return "";
		}
	}
}
