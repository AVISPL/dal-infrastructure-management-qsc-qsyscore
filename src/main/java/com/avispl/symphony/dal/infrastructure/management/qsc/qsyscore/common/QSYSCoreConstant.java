/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * QSYSCoreConstant contains constants used in monitoring and controlling
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
	public static final String COLON = ":";
	public static final String DOT = ".";
	public static final String SPACE = " ";
	public static final String COMMAS = ",";
	public static final String GAIN = "Gain";
	public static final String GAIN_TYPE = "gain";
	public static final String DEFAUL_DATA = "None";
	public static final String FORMAT_STRING = "%s";
	public static final String DB_UNIT = "dB";
	public static final String EMPTY = "";
	public static final String COMMA = ",";
	public static final String ZERO = "0";

	// Device response Type
	public static final String DISPLAY_DEVICE = "vst_hdmi_display";
	public static final String PROCESSOR_DEVICE = "core_status";
	public static final String STREAM_INPUT_DEVICE = "soft_dante_input";
	public static final String STREAM_OUTPUT_DEVICE = "soft_dante_output";
	public static final String VIDEO_IO_DEVICE = "vstreamer_status";
	public static final String VIDEO_SOURCE_DEVICE = "vst_hdmi_source";
	public static final String CONTROL_INTERFACE_DEVICE = "touch_screen_status";
	public static final Set<String> SUPPORTED_DEVICE_TYPE = new HashSet<>(
			Arrays.asList(PROCESSOR_DEVICE, DISPLAY_DEVICE, STREAM_INPUT_DEVICE, STREAM_OUTPUT_DEVICE, VIDEO_IO_DEVICE, VIDEO_SOURCE_DEVICE, CONTROL_INTERFACE_DEVICE));

	/**
	 * Token timeout is 1 hour ( 60 minutes), as this case reserve 5 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 55;

	// Thread metric
	public static final int MAX_THREAD = 3;
	public static final int MAX_THREAD_QUANTITY = 3;
	public static final int MIN_THREAD_QUANTITY = 1;
	public static final int MAX_DEVICE_QUANTITY_PER_THREAD = 2; //60
	public static final int MIN_POLLING_INTERVAL = 1;

	//Control response
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	public static final String CONTROL_VALUE = "Value";
	public static final String CONTROL_VALUE_MIN = "ValueMin";
	public static final String CONTROL_VALUE_MAX = "ValueMax";
	public static final String RESULT = "result";
	public static final String CONTROLS = "Controls";
	public static final String CONTROL_NAME = "Name";
	public static final String CONTROL_VALUE_STRING = "String";

	// Device Type
	public static final String PROCESSOR_TYPE="Processor";
	public static final String DISPLAY_TYPE="Display";
	public static final String STREAM_IO_TYPE="Streaming I/O";
	public static final String VIDEO_IO_TYPE="Video I/O";
	public static final String VIDEO_SOURCE_TYPE="Video Source";
	public static final String CONTROL_INTERFACE_TYPE="Control Interface";

}