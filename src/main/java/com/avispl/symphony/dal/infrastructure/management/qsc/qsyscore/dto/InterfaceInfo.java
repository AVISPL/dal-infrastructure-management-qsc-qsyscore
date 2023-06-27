/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * InterfaceInfo contain all network information of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterfaceInfo {
	private String id;
	private String name;
	private String mode;
	private String ipAddress;
	private String netMask;
	private String gateway;
	private String macAddress;
	private String chassis;
	private String port;
	private String linkSpeed;

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
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
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #mode}
	 *
	 * @return value of {@link #mode}
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Sets {@link #mode} value
	 *
	 * @param mode new value of {@link #mode}
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Retrieves {@link #ipAddress}
	 *
	 * @return value of {@link #ipAddress}
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Sets {@link #ipAddress} value
	 *
	 * @param ipAddress new value of {@link #ipAddress}
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Retrieves {@link #netMask}
	 *
	 * @return value of {@link #netMask}
	 */
	public String getNetMask() {
		return netMask;
	}

	/**
	 * Sets {@link #netMask} value
	 *
	 * @param netMask new value of {@link #netMask}
	 */
	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	/**
	 * Retrieves {@link #gateway}
	 *
	 * @return value of {@link #gateway}
	 */
	public String getGateway() {
		return gateway;
	}

	/**
	 * Sets {@link #gateway} value
	 *
	 * @param gateway new value of {@link #gateway}
	 */
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	/**
	 * Retrieves {@link #macAddress}
	 *
	 * @return value of {@link #macAddress}
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * Sets {@link #macAddress} value
	 *
	 * @param macAddress new value of {@link #macAddress}
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * Retrieves {@link #chassis}
	 *
	 * @return value of {@link #chassis}
	 */
	public String getChassis() {
		return chassis;
	}

	/**
	 * Sets {@link #chassis} value
	 *
	 * @param chassis new value of {@link #chassis}
	 */
	public void setChassis(String chassis) {
		this.chassis = chassis;
	}

	/**
	 * Retrieves {@link #port}
	 *
	 * @return value of {@link #port}
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Sets {@link #port} value
	 *
	 * @param port new value of {@link #port}
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * Retrieves {@link #linkSpeed}
	 *
	 * @return value of {@link #linkSpeed}
	 */
	public String getLinkSpeed() {
		return linkSpeed;
	}

	/**
	 * Sets {@link #linkSpeed} value
	 *
	 * @param linkSpeed new value of {@link #linkSpeed}
	 */
	public void setLinkSpeed(String linkSpeed) {
		this.linkSpeed = linkSpeed;
	}
}