/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceInfoData contain all information of device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfoData {
	private String naturalId;
	private String serial;
	private String name;
	private String model;
	private String hardwareId;
	private String hostname;
	private String modelName;
	private String modelCode;
	private Firmware firmware;
	private long uptime;
	private String access;
	private boolean engineOnline;
	private long engineUptime;
	private String redundancy = null;
	private Status status;
	private boolean isComplete;
	private String serialNo;

	/**
	 * Retrieves {@link #naturalId}
	 *
	 * @return value of {@link #naturalId}
	 */
	public String getNaturalId() {
		return naturalId;
	}

	/**
	 * Sets {@link #naturalId} value
	 *
	 * @param naturalId new value of {@link #naturalId}
	 */
	public void setNaturalId(String naturalId) {
		this.naturalId = naturalId;
	}

	/**
	 * Retrieves {@link #serial}
	 *
	 * @return value of {@link #serial}
	 */
	public String getSerial() {
		return serial;
	}

	/**
	 * Sets {@link #serial} value
	 *
	 * @param serial new value of {@link #serial}
	 */
	public void setSerial(String serial) {
		this.serial = serial;
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
	 * Retrieves {@link #model}
	 *
	 * @return value of {@link #model}
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Sets {@link #model} value
	 *
	 * @param model new value of {@link #model}
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Retrieves {@link #hardwareId}
	 *
	 * @return value of {@link #hardwareId}
	 */
	public String getHardwareId() {
		return hardwareId;
	}

	/**
	 * Sets {@link #hardwareId} value
	 *
	 * @param hardwareId new value of {@link #hardwareId}
	 */
	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

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
	 * Retrieves {@link #modelName}
	 *
	 * @return value of {@link #modelName}
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * Sets {@link #modelName} value
	 *
	 * @param modelName new value of {@link #modelName}
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * Retrieves {@link #modelCode}
	 *
	 * @return value of {@link #modelCode}
	 */
	public String getModelCode() {
		return modelCode;
	}

	/**
	 * Sets {@link #modelCode} value
	 *
	 * @param modelCode new value of {@link #modelCode}
	 */
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	/**
	 * Retrieves {@link #firmware}
	 *
	 * @return value of {@link #firmware}
	 */
	public Firmware getFirmware() {
		return firmware;
	}

	/**
	 * Sets {@link #firmware} value
	 *
	 * @param firmware new value of {@link #firmware}
	 */
	public void setFirmware(Firmware firmware) {
		this.firmware = firmware;
	}

	/**
	 * Retrieves {@link #status}
	 *
	 * @return value of {@link #status}
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets {@link #status} value
	 *
	 * @param status new value of {@link #status}
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Retrieves {@link #uptime}
	 *
	 * @return value of {@link #uptime}
	 */
	public long getUptime() {
		return uptime;
	}

	/**
	 * Sets {@link #uptime} value
	 *
	 * @param uptime new value of {@link #uptime}
	 */
	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	/**
	 * Retrieves {@link #access}
	 *
	 * @return value of {@link #access}
	 */
	public String getAccess() {
		return access;
	}

	/**
	 * Sets {@link #access} value
	 *
	 * @param access new value of {@link #access}
	 */
	public void setAccess(String access) {
		this.access = access;
	}

	/**
	 * Retrieves {@link #engineOnline}
	 *
	 * @return value of {@link #engineOnline}
	 */
	public boolean isEngineOnline() {
		return engineOnline;
	}

	/**
	 * Sets {@link #engineOnline} value
	 *
	 * @param engineOnline new value of {@link #engineOnline}
	 */
	public void setEngineOnline(boolean engineOnline) {
		this.engineOnline = engineOnline;
	}

	/**
	 * Retrieves {@link #engineUptime}
	 *
	 * @return value of {@link #engineUptime}
	 */
	public float getEngineUptime() {
		return engineUptime;
	}

	/**
	 * Sets {@link #engineUptime} value
	 *
	 * @param engineUptime new value of {@link #engineUptime}
	 */
	public void setEngineUptime(long engineUptime) {
		this.engineUptime = engineUptime;
	}

	/**
	 * Retrieves {@link #redundancy}
	 *
	 * @return value of {@link #redundancy}
	 */
	public String getRedundancy() {
		return redundancy;
	}

	/**
	 * Sets {@link #redundancy} value
	 *
	 * @param redundancy new value of {@link #redundancy}
	 */
	public void setRedundancy(String redundancy) {
		this.redundancy = redundancy;
	}

	/**
	 * Retrieves {@link #isComplete}
	 *
	 * @return value of {@link #isComplete}
	 */
	public boolean isComplete() {
		return isComplete;
	}

	/**
	 * Sets {@link #isComplete} value
	 *
	 * @param complete new value of {@link #isComplete}
	 */
	public void setComplete(boolean complete) {
		isComplete = complete;
	}

	/**
	 * Retrieves {@link #serialNo}
	 *
	 * @return value of {@link #serialNo}
	 */
	public String getSerialNo() {
		return serialNo;
	}

	/**
	 * Sets {@link #serialNo} value
	 *
	 * @param serialNo new value of {@link #serialNo}
	 */
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
}