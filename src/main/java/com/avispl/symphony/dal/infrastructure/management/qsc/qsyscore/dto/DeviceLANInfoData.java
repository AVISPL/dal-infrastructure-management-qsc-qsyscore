/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceLANInfoData contain host name and interface information of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceLANInfoData {
	private String hostname;
	List<InterfaceInfo> interfaces = new ArrayList<InterfaceInfo>();

	/**
	 * Retrieves {@link #hostname}
	 *
	 * @return value of {@link #hostname}
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Sets {@link #hostname} value
	 *
	 * @param hostname new value of {@link #hostname}
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Retrieves {@link #interfaces}
	 *
	 * @return value of {@link #interfaces}
	 */
	public List<InterfaceInfo> getInterfaces() {
		return interfaces;
	}

	/**
	 * Sets {@link #interfaces} value
	 *
	 * @param interfaces new value of {@link #interfaces}
	 */
	public void setInterfaces(List<InterfaceInfo> interfaces) {
		this.interfaces = interfaces;
	}
}