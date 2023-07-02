/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.QSYSCoreAggregatorCommunicator;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreDesignMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreNetworkMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreSystemMetric;

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
		qSYSCoreCommunicator.setHost("10.70.50.138");
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

	/**
	 * Test GetMultipleStatistics with system information
	 *
	 * Expect GetMultipleStatistics successfully
	 */
	@Test
	void testGetMultipleStatisticsWithSystemInfo() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assertions.assertEquals("3-440F59FA6034C59670FF3C0928929607", stats.get(QSYSCoreSystemMetric.DEVICE_ID.getName()));
		Assertions.assertEquals("3-440F59FA6034C59670FF3C0928929607", stats.get(QSYSCoreSystemMetric.SERIAL_NUMBER.getName()));
		Assertions.assertEquals("Core 110f", stats.get(QSYSCoreSystemMetric.DEVICE_MODEL.getName()));
		Assertions.assertEquals("CeeSalt_TestCore_v3.2_28-6", stats.get(QSYSCoreDesignMetric.DESIGN_NAME.getName()));
		Assertions.assertEquals("9.8.0-2304.003", stats.get(QSYSCoreSystemMetric.FIRMWARE_VERSION.getName()));
		Assertions.assertEquals("29 day(s) 01 hour(s) 06 minute(s) 20 second(s)", stats.get(QSYSCoreSystemMetric.UPTIME.getName()));
		Assertions.assertEquals("Running", stats.get(QSYSCoreSystemMetric.STATUS.getName()));
	}

	/**
	 * Test GetMultipleStatistics with Network Info
	 *
	 * Expect get network info successfully
	 */
	@Test
	void testGetMultipleStatisticsNetworkInfo() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		String group = QSYSCoreConstant.LAN_A+ QSYSCoreConstant.HASH;
		Assertions.assertEquals("169.254.232.117", stats.get(group + QSYSCoreNetworkMetric.IP_ADDRESS.getName()));
		Assertions.assertEquals("255.255.0.0", stats.get(group + QSYSCoreNetworkMetric.SUBNET_MASK.getName()));
		Assertions.assertEquals("0.0.0.0", stats.get(group + QSYSCoreNetworkMetric.GATEWAY.getName()));
		Assertions.assertEquals("00:60:74:05:34:A9", stats.get(group + QSYSCoreNetworkMetric.MAC_ADDRESS.getName()));
		group = QSYSCoreConstant.LAN_B + QSYSCoreConstant.HASH;
		Assertions.assertEquals("10.70.50.138", stats.get(group + QSYSCoreNetworkMetric.IP_ADDRESS.getName()));
		Assertions.assertEquals("255.255.255.0", stats.get(group + QSYSCoreNetworkMetric.SUBNET_MASK.getName()));
		Assertions.assertEquals("10.70.50.1", stats.get(group + QSYSCoreNetworkMetric.GATEWAY.getName()));
		Assertions.assertEquals("00:60:74:05:34:A8", stats.get(group + QSYSCoreNetworkMetric.MAC_ADDRESS.getName()));
	}


	@Test
	public void testControlProperty() throws Exception {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Gain:ABC#Mute");
		controllableProperty.setValue(1);
		qSYSCoreCommunicator.getMultipleStatistics();
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
		Assertions.assertTrue(aggregatedDevices.size()>0);
		for (AggregatedDevice aggregatedDevice : aggregatedDevices) {
			Assertions.assertTrue(stringSet.contains(aggregatedDevice.getDeviceId()));
		}
	}

	/**
	 * Test Control GainValueControl value
	 *
	 * Expect control GainValueControl successfully
	 */
	@Test
	void testControlGainValueControl() throws Exception {

		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);

		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Gain:Gain_3#GainValueControl(dB)"));

		qSYSCoreCommunicator.disconnect();
//		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainName("Gain_3,Gain_1, Gain_2, Gain");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
//		Assertions.assertNotNull(stats.get("Gain:ABC!@X#GainControl(dB)"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String value = "0.0";
		String property = "Gain:Gain_3#GainControl(dB)";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
//		Assertions.assertEquals(2.0F, stats.get("Gain:ABC#GainCurrentValue(dB)"));
		List<AdvancedControllableProperty> controllableProperty1 = extendedStatistics.getControllableProperties();
		Assertions.assertEquals(0.0f, controllableProperty1.stream().filter(item -> item.getName().equals(property)).findFirst().get().getValue());
	}

}