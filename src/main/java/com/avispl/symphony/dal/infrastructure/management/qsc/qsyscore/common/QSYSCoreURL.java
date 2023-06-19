package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * QSYSCoreURL
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
public class QSYSCoreURL {
	public static final String BASE_URI = "/api/v0/";
	public static final String TOKEN = "logon";
	public static final String DEVICE_INFO = "cores/self?meta=permissions";
	public static final String DEVICE_LAN_INFO = "cores/self/config/network?meta=permissions&include=autoDns";
}