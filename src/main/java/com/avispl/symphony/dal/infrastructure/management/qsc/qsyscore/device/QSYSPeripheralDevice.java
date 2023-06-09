/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;

/**
 * DeviceInfo
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
public abstract class QSYSPeripheralDevice implements DeviceBehavior {
	private Map<String, String> stats;
	private List<AdvancedControllableProperty> advancedControllableProperties;
	private String name;
	private String type;
	private String id;

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Retrieves {@link #stats}
	 *
	 * @return value of {@link #stats}
	 */
	public Map<String, String> getStats() {
		return stats;
	}

	/**
	 * Sets {@link #stats} value
	 *
	 * @param stats new value of {@link #stats}
	 */
	public void setStats(Map<String, String> stats) {
		this.stats = stats;
	}

	/**
	 * Retrieves {@link #advancedControllableProperties}
	 *
	 * @return value of {@link #advancedControllableProperties}
	 */
	public List<AdvancedControllableProperty> getAdvancedControllableProperties() {
		return advancedControllableProperties;
	}

	/**
	 * Sets {@link #advancedControllableProperties} value
	 *
	 * @param advancedControllableProperties new value of {@link #advancedControllableProperties}
	 */
	public void setAdvancedControllableProperties(List<AdvancedControllableProperty> advancedControllableProperties) {
		this.advancedControllableProperties = advancedControllableProperties;
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
	 * Create a preripheral device
	 */
	public QSYSPeripheralDevice() {
		stats = new HashMap<>();
		advancedControllableProperties = new ArrayList<>();
	}

	public void updateStatusMessage() {
		String deviceStatus = this.getStats().get(QSYSCoreConstant.STATUS);
		if (deviceStatus != null) {
			String[] splitStatus = deviceStatus.split(QSYSCoreConstant.HYPHEN, 2);
			if (splitStatus.length == 2) {
				this.getStats().put(QSYSCoreConstant.STATUS, splitStatus[0]);
				this.getStats().put(QSYSCoreConstant.STATUS_MESSAGE, splitStatus[1]);
			} else {
				this.getStats().remove(QSYSCoreConstant.STATUS_MESSAGE);
			}
		}

		deviceStatus = this.getStats().get(QSYSCoreConstant.STATUS_LED);
		if (deviceStatus != null) {
			String[] splitStatus = deviceStatus.split(QSYSCoreConstant.HYPHEN, 2);
			if (splitStatus.length == 2) {
				this.getStats().put(QSYSCoreConstant.STATUS_LED, splitStatus[0]);
				this.getStats().put(QSYSCoreConstant.STATUS_LED_MESSAGE, splitStatus[1]);
			} else {
				this.getStats().remove(QSYSCoreConstant.STATUS_LED_MESSAGE);
			}
		}

	}
}