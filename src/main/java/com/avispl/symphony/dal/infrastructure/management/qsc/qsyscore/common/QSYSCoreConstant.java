/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
	public static final int MIN_PORT = 1;
	public static final int MAX_PORT = 65535;
	public static final String AUTHORIZED = "Authorized";
	public static final String PASSWORD = "password";
	public static final String USERNAME = "username";
	public static final String STATUS = "Status";
	public static final String QSYS_TYPE = "QSYSType";
	public static final String OK_STATUS = "OK";
	public static final String TOKEN = "token";
	public static final String HTTP = "http://";
	public static final String HASH = "#";
	public static final String COLON = ":";
	public static final String SEMICOLON = ";";
	public static final String DOT = ".";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String HYPHEN = "-";
	public static final String GAIN = "Gain";
	public static final String GAIN_TYPE = "gain";
	public static final String DEFAUL_DATA = "None";
	public static final String NOT_AVAILABLE = "N/A";
	public static final String FORMAT_STRING = "%s";
	public static final String DB_UNIT = "dB";
	public static final String EMPTY = "";
	public static final String ON = "On";
	public static final String OFF = "Off";
	public static final String ENABLED = "enabled";
	public static final String MUTED = "muted";
	public static final String NORMAL = "normal";

	public static final String ZERO = "0";
	public static final String LAN_A = "LAN_A";
	public static final String LAN_B = "LAN_B";
	public static final String CHANNEL = "Channel";
	public static final String HOSTNAME = "Hostname";
	public static final String REDUNDANCY = "Redundancy";
	public static final String NUMBER_OF_DEVICE = "MonitoredDevicesTotal";
	public static final String GAIN_CURRENT_VALUE = "GainCurrentValue(dB)";
	public static final String CMD_RESULT = "\"result\"";
	public static final String CMD_METHOD = "\"method\"";
	public static final String CMD_ERROR = "\"error\"";
	public static final String SPECIAL_CHARS_PATTERN = ".*[~!@#$%^&\\\\'].*|.*<(\\?).*|.*(\\<)(\\/).*";

	// Message
	public static final String STATUS_MISSING = "Missing";
	public static final String STATUS_LED = "StatusLed";
	public static final String STATUS_MESSAGE = "StatusMessage";
	public static final String STATUS_LED_MESSAGE = "StatusLedMessage";
	public static final String GETTING_TOKEN_ERR = "Username and Password are incorrect";
	public static final String MISSING_QSYS_TYPE_ERR = "Missing QSYS_TYPE property ";
	public static final String STATUS_MISSING_MESSAGE = "Not Present - Stream Disabled, Stopped";
	public static final String STATUS_LED_MISSING_MESSAGE = "Missing - Status unavailable";

	//Q-LAN device
	public static final String Q_LAN_RX_1_DEVICE = "Q-LAN_Receiver_Q-LAN-RX-1";
	public static final String Q_LAN_TX_1_DEVICE = "Q-LAN_Transmitter_Q-LAN-TX-1";

	// Device response Type
	public static final String DISPLAY_DEVICE = "vst_hdmi_display";
	public static final String PROCESSOR_DEVICE = "core_status";
	public static final String STREAM_INPUT_DEVICE = "soft_dante_input";
	public static final String CAMERA_DEVICE = "onvif_camera_operative";
	public static final String STREAM_OUTPUT_DEVICE = "soft_dante_output";
	public static final String VIDEO_IO_DEVICE = "vstreamer_status";
	public static final String VIDEO_SOURCE_DEVICE = "vst_hdmi_source";
	public static final String CONTROL_INTERFACE_DEVICE = "touch_screen_status";

	public static final String TRANSMITTER_DEVICE = "output_box";
	public static final String AMPLIFIER_DEVICE = "cxq_amplifier";
	public static final String RECEIVER_DEVICE = "input_box";
	public static final String LOUDSPEAKER_DEVICE = "lua_speaker";
	public static final String STATUS_AMP_DEVICE = "amplifier_cxq_status";

	public static final String MONITORING_PROXY = "monitoring_proxy";
	public static final String PLUGIN = "PLUGIN";
	public static final String SENNHEISER = "SennheiserTeamConnectCeiling";
	public static final String NETGEAR = "EnterpriseManagerNetgearAVLineSwitch";
	public static final String MIDDLE_ATLANTIC = "MiddleAtlanticNEXSYSUPS";
	public static final String AUDIO_LEVEL_CURRENT_VALUE = "AudioLevelCurrentValue(dB)";
	public static final String LED_BRIGHTNESS_CURRENT_VALUE = "LEDBrightnessCurrentValue";

	//MetaData
	public static final String MONITORING_CYCLE_DURATION = "LastMonitoringCycleDuration(s)";
	public static final String ADAPTER_VERSION = "AdapterVersion";
	public static final String ADAPTER_BUILD_DATE = "AdapterBuildDate";
	public static final String ADAPTER_UPTIME_MIN = "AdapterUptime(min)";
	public static final String ADAPTER_UPTIME = "AdapterUptime";
	public static final String NULL = "Null";
	public static final String NONE_VALUE = "None";

	public static final List<String> SOUND_PROFILE = Arrays.asList("Off", "Custom");
	public static final List<String> NOISE_LEVEL = Arrays.asList("Quiet", "Normal", "Loud");
	public static final List<String> ON_COLOR = Arrays.asList("White", "Green", "Blue", "Red", "Yellow", "Orange", "Cyan", "Pink");
	public static final Set<String> SUPPORTED_DEVICE_TYPE = new HashSet<>(
			Arrays.asList(CAMERA_DEVICE, PROCESSOR_DEVICE, DISPLAY_DEVICE, STREAM_INPUT_DEVICE, STREAM_OUTPUT_DEVICE, VIDEO_IO_DEVICE, VIDEO_SOURCE_DEVICE, CONTROL_INTERFACE_DEVICE, MONITORING_PROXY,
					PLUGIN, TRANSMITTER_DEVICE, AMPLIFIER_DEVICE, RECEIVER_DEVICE, LOUDSPEAKER_DEVICE, STATUS_AMP_DEVICE));
	public static final List<String> PLUGIN_LIST = Arrays.asList(SENNHEISER, MIDDLE_ATLANTIC, NETGEAR);
	public static final List<String> LIST_ONLINE_STATUS = Arrays.asList("Ok", "Compromised", "Fault","Initializing");
	/**
	 * Token timeout is 1 hour ( 60 minutes), as this case reserve 5 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 55;

	// Thread metric
	public static final int MAX_THREAD_QUANTITY = 8;
	public static final int MIN_THREAD_QUANTITY = 1;
	public static final int MAX_DEVICE_QUANTITY_PER_THREAD = 60;
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
	public static final String PROCESSOR_TYPE = "Processor";
	public static final String DISPLAY_TYPE = "Display";
	public static final String STREAM_IO_TYPE = "Streaming I/O";
	public static final String VIDEO_IO_TYPE = "Video I/O";
	public static final String VIDEO_SOURCE_TYPE = "Video Source";
	public static final String CONTROL_INTERFACE_TYPE = "Control Interface";
	public static final String EXTERNAL = "External";
	public static final String CAMERA_TYPE = "Camera";
	public static final String TRANSMITTER_TYPE = "Transmitter";
	public static final String AMPLIFIER_TYPE = "Amplifier";
	public static final String STATUS_AMPLIFIER_TYPE = "Status Amplifier";
	public static final String RECEIVER_TYPE = "Receiver";
	public static final String LOUDSPEAKER_TYPE = "LoudSpeaker";

	//Date time
	public static final String DAYS = "day(s)";
	public static final String HOURS = "hour(s)";
	public static final String MINUTES = "minute(s)";
	public static final String SECONDS = "second(s)";

	public static final Integer MAX_ERROR_COUNT = 2;
	public static final int MIN_CYCLE_TIME = 1;
	public static final int MAX_CYCLE_TIME = 60;
}