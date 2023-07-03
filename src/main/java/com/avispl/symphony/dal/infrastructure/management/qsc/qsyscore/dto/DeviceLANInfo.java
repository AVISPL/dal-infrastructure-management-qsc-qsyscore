/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DeviceLANInfo contain DeviceLANInfoData
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceLANInfo {

	@JsonAlias("data")
	DeviceLANInfoData networkInfo;

	/**
	 * Retrieves {@link #networkInfo}
	 *
	 * @return value of {@link #networkInfo}
	 */
	public DeviceLANInfoData getNetworkInfo() {
		return networkInfo;
	}

	/**
	 * Sets {@link #networkInfo} value
	 *
	 * @param networkInfo new value of {@link #networkInfo}
	 */
	public void setNetworkInfo(DeviceLANInfoData networkInfo) {
		this.networkInfo = networkInfo;
	}


	/**
	 * Get value of monitoring property by metric name
	 *
	 * @param networkMetric is QSYSCoreNetworkMetric instance
	 * @return String is value of the metric
	 */
	public String getValueByMetricName(QSYSCoreNetworkMetric networkMetric, boolean isNetworkInterFace) {
		if (null == this.networkInfo) {
			networkInfo = new DeviceLANInfoData();
		}
		List<InterfaceInfo> interfaces = networkInfo.getInterfaces();
		if (interfaces.isEmpty()) {
			interfaces = new ArrayList<>(2);
		}
		InterfaceInfo interfaceName = interfaces.get(0);
		if (isNetworkInterFace) {
			interfaceName = interfaces.get(1);
		}
		switch (networkMetric) {
			case GATEWAY:
				return interfaceName.getGateway();
			case IP_ADDRESS:
				return interfaceName.getIpAddress();
			case MAC_ADDRESS:
				return interfaceName.getMacAddress();
			case SUBNET_MASK:
				return interfaceName.getNetMask();
			case HOSTNAME:
				return networkInfo.getHostname();
			default:
				throw new IllegalArgumentException("The property name doesn't support" + networkMetric.getName());
		}
	}
}