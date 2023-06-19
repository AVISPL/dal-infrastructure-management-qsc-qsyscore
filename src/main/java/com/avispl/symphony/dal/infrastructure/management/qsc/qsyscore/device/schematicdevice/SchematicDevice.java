package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.schematicdevice;

import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.Device;

/**
 * SchematicDevice
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/16/2023
 * @since 1.0.0
 */
public abstract class SchematicDevice extends Device {
	private String groupName;

	/**
	 * Retrieves {@link #groupName}
	 *
	 * @return value of {@link #groupName}
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets {@link #groupName} value
	 *
	 * @param groupName new value of {@link #groupName}
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public SchematicDevice(String groupName) {
		this.groupName = groupName;
	}
}