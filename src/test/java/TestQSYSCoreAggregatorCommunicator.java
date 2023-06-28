/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.QSYSCoreAggregatorCommunicator;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreMonitoringMetric;

/**
 * TestQSYSCoreAggregatorCommunicator
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/14/2023
 * @since 1.0.0
 */
public class TestQSYSCoreAggregatorCommunicator {
	private final QSYSCoreAggregatorCommunicator qSYSCoreCommunicator = new QSYSCoreAggregatorCommunicator();

	@BeforeEach()
	public void setUp() throws Exception {
		qSYSCoreCommunicator.setHost("***REMOVED***");
		qSYSCoreCommunicator.setLogin("technican");
		qSYSCoreCommunicator.setPassword("12345678");
		qSYSCoreCommunicator.setPort(80);
		qSYSCoreCommunicator.init();
		qSYSCoreCommunicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		qSYSCoreCommunicator.disconnect();
		qSYSCoreCommunicator.destroy();
	}

	@Test
	public void testGetMultiplestatictis() throws Exception {
		final ExtendedStatistics[] extendedStatistics = new ExtendedStatistics[1];
		Assertions.assertDoesNotThrow(() -> extendedStatistics[0] = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0));
		Map<String, String> stats = extendedStatistics[0].getStatistics();
		Assertions.assertEquals("3-440F59FA6034C59670FF3C0928929607", stats.get(QSYSCoreMonitoringMetric.DEVICE_ID.getName()));
		Assertions.assertEquals("Core 110f", stats.get(QSYSCoreMonitoringMetric.DEVICE_MODEL.getName()));
		Assertions.assertEquals("CeeSalt_TestCore_v3.2", stats.get(QSYSCoreMonitoringMetric.DESIGN_NAME.getName()));
		Assertions.assertEquals("9.8.0-2304.003", stats.get(QSYSCoreMonitoringMetric.FIRMWARE_VERSION.getName()));
		Assertions.assertEquals("Running", stats.get(QSYSCoreMonitoringMetric.STATUS.getName()));
		Assertions.assertEquals("169.254.232.117", stats.get(QSYSCoreMonitoringMetric.LAN_A.getName() + QSYSCoreConstant.HASH + QSYSCoreMonitoringMetric.IP_ADDRESS.getName()));
	}

	@Test
	public void testControlProperty() throws Exception {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Gain:ABC#Mute");
		controllableProperty.setValue(false);
		Assertions.assertDoesNotThrow(() -> qSYSCoreCommunicator.controlProperty(controllableProperty));
	}

	@Test
	public void testRetrieveAggregatorDevice() throws Exception {
		Assertions.assertDoesNotThrow(() -> {
			qSYSCoreCommunicator.getMultipleStatistics();
			qSYSCoreCommunicator.retrieveMultipleStatistics();
			TimeUnit.MILLISECONDS.sleep(60000);
			List<AggregatedDevice> aggregatedDevices = qSYSCoreCommunicator.retrieveMultipleStatistics();
			AggregatedDevice aggregatedDevice = aggregatedDevices.get(0);
			for (AggregatedDevice aggregatedDevice1 : aggregatedDevices) {
				Assertions.assertNotNull(aggregatedDevice.getDeviceId());
				Assertions.assertNotNull(aggregatedDevice.getDeviceName());
				Assertions.assertNotEquals(0, aggregatedDevice.getProperties().size());
			}
		});
	}

	@Test
	public void testFilterGainName() throws Exception {
		qSYSCoreCommunicator.setFilterGainName("ABC");
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assertions.assertTrue(stats.containsKey("Gain:ABC#Bypass"));
		Assertions.assertFalse(stats.containsKey("Gain:123456#Bypass"));
	}

	@Test
	public void testFilterComponentName() throws Exception {
		Set<String> stringNameSet = new HashSet<>(Arrays.asList("Generic_AV_Source_HDMI-Source-3", "Status_CeeSalt-Decoder"));
		qSYSCoreCommunicator.setFilterComponentName("Generic_AV_Source_HDMI-Source-3, Status_CeeSalt-Decoder");
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		TimeUnit.MILLISECONDS.sleep(60000);
		List<AggregatedDevice> aggregatedDevices = qSYSCoreCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : aggregatedDevices) {
			Assertions.assertTrue(stringNameSet.contains(aggregatedDevice.getDeviceId()));
		}
	}

	@Test
	public void testFilterDeviceType() throws Exception {
		Set<String> stringSet = new HashSet<>(Arrays.asList("Software_Dante_RX_Software-Dante-RX-1", "Software_Dante_TX_Software-Dante-TX-1", "Generic_HDMI_Display_HDMI-Display-1"));
		qSYSCoreCommunicator.setFilterDeviceType(QSYSCoreConstant.STREAM_IO_TYPE + ",  " + QSYSCoreConstant.DISPLAY_TYPE);
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		TimeUnit.MILLISECONDS.sleep(60000);
		List<AggregatedDevice> aggregatedDevices = qSYSCoreCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : aggregatedDevices) {
			Assertions.assertTrue(stringSet.contains(aggregatedDevice.getDeviceId()));
		}
	}

	@Test
	public void testCustom() {
	}
}