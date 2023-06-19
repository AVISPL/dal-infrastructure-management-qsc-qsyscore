/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

/**
 * QSYSCoreConstant class provides during the monitoring and controlling process
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/30/2023
 * @since 1.0.0
 */
public class QSYSCoreConstant {

	public static final int QRC_PORT = 1710;
	public static final int MIN_PORT = 0;
	public static final int MAX_PORT = 65535;
	public static final String AUTHORIZED = "Authorized";
	public static final String PASSWORD = "password";
	public static final String USERNAME = "username";
	public static final String TOKEN = "token";
	public static final String GETTING_TOKEN_ERR = "Username and Password are incorrect";
	public static final String HTTP = "http://";
	public static final String HASH = "#";

	/**
	 * Token timeout is 1 hour ( 60 minutes), as this case reserve 5 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 55;

	// Thread metric
	public static final int MAX_THREAD = 7;
	public static final int MAX_THREAD_QUANTITY = 7;
	public static final int MIN_THREAD_QUANTITY = 1;
	public static final int MAX_DEVICE_QUANTITY_PER_THREAD = 60;
	public static final int MIN_POLLING_INTERVAL = 1;
}