/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.GainControllingMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.MiddleAtlanticMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.NetgearDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.SennheiserDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.VideoIODeviceMetric;
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
	public QSYSCoreAggregatorCommunicator qSYSCoreCommunicator = new QSYSCoreAggregatorCommunicator();

	@BeforeEach()
	public void setUp() throws Exception {
		qSYSCoreCommunicator.setHost("");
		qSYSCoreCommunicator.setLogin("");
		qSYSCoreCommunicator.setPassword("");
		qSYSCoreCommunicator.setPort(443);
		qSYSCoreCommunicator.setProtocol("https");
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

		Assertions.assertEquals("CeeSalt_TestCore_v3.2-MonitoringProxy", stats.get(QSYSCoreDesignMetric.DESIGN_NAME.getName()));
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
		String group = QSYSCoreConstant.LAN_A + QSYSCoreConstant.HASH;
		Assertions.assertEquals("169.254.232.117", stats.get(group + QSYSCoreNetworkMetric.IP_ADDRESS.getName()));
		Assertions.assertEquals("255.255.0.0", stats.get(group + QSYSCoreNetworkMetric.SUBNET_MASK.getName()));
		Assertions.assertEquals("0.0.0.0", stats.get(group + QSYSCoreNetworkMetric.GATEWAY.getName()));
		Assertions.assertEquals("00:60:74:05:34:A9", stats.get(group + QSYSCoreNetworkMetric.MAC_ADDRESS.getName()));
		group = QSYSCoreConstant.LAN_B + QSYSCoreConstant.HASH;
		Assertions.assertEquals("***REMOVED***", stats.get(group + QSYSCoreNetworkMetric.IP_ADDRESS.getName()));
		Assertions.assertEquals("255.255.255.0", stats.get(group + QSYSCoreNetworkMetric.SUBNET_MASK.getName()));
		Assertions.assertEquals("10.70.50.1", stats.get(group + QSYSCoreNetworkMetric.GATEWAY.getName()));
		Assertions.assertEquals("00:60:74:05:34:A8", stats.get(group + QSYSCoreNetworkMetric.MAC_ADDRESS.getName()));
	}

	/**
	 * Test Control Mute Gain is off
	 *
	 * Expect control Gain successfully
	 */
	@Test
	void testControlMuteGainIsOff() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Gain:ABC#Mute"));

		qSYSCoreCommunicator.disconnect();
		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("ABC");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertNotNull(stats.get("Gain:ABC#Mute"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String value = "0";
		String property = "Gain:ABC#Mute";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperty1 = extendedStatistics.getControllableProperties();
		Assertions.assertEquals("0", controllableProperty1.stream().filter(item -> item.getName().equals(property)).findFirst().get().getValue());
	}

	/**
	 * Test Control Mute Gain is on
	 *
	 * Expect control Gain successfully
	 */
	@Test
	void testControlMuteGainIsON() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Gain:ABC#Mute"));

		qSYSCoreCommunicator.disconnect();
		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("ABC");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertNotNull(stats.get("Gain:ABC#Mute"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String value = "1";
		String property = "Gain:ABC#Mute";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperty1 = extendedStatistics.getControllableProperties();
		Assertions.assertEquals("1", controllableProperty1.stream().filter(item -> item.getName().equals(property)).findFirst().get().getValue());
	}

	/**
	 * Test Control Invert Gain is Off
	 *
	 * Expect control Invert Gain successfully
	 */
	@Test
	void testControlInvertGainIsOff() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Gain:ABC#Invert"));

		qSYSCoreCommunicator.disconnect();
		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("ABC");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertNotNull(stats.get("Gain:ABC#Invert"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String value = "0";
		String property = "Gain:ABC#Invert";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperty1 = extendedStatistics.getControllableProperties();
		Assertions.assertEquals("0", controllableProperty1.stream().filter(item -> item.getName().equals(property)).findFirst().get().getValue());
	}

	/**
	 * Test Control Invert Gain is on
	 *
	 * Expect control Invert successfully
	 */
	@Test
	void testControlInvertGainIsON() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Gain:ABC#Invert"));

		qSYSCoreCommunicator.disconnect();
		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("ABC");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertNotNull(stats.get("Gain:ABC#Invert"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String value = "1";
		String property = "Gain:ABC#Invert";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperty1 = extendedStatistics.getControllableProperties();
		Assertions.assertEquals("1", controllableProperty1.stream().filter(item -> item.getName().equals(property)).findFirst().get().getValue());
	}

	/**
	 * Test Control Bypass Gain is Off
	 *
	 * Expect control Bypass Gain successfully
	 */
	@Test
	void testControlBypassGainIsOff() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Gain:ABC#Bypass"));

		qSYSCoreCommunicator.disconnect();
		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("ABC");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertNotNull(stats.get("Gain:ABC#Bypass"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String value = "0";
		String property = "Gain:ABC#Bypass";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperty1 = extendedStatistics.getControllableProperties();
		Assertions.assertEquals("0", controllableProperty1.stream().filter(item -> item.getName().equals(property)).findFirst().get().getValue());
	}

	/**
	 * Test Control Bypass Gain is on
	 *
	 * Expect control Bypass successfully
	 */
	@Test
	void testControlBypassGainIsON() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertNull(stats.get("Gain:ABC#Bypass"));

		qSYSCoreCommunicator.disconnect();
		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("ABC");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertNotNull(stats.get("Gain:ABC#Bypass"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String value = "1";
		String property = "Gain:ABC#Bypass";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> controllableProperty1 = extendedStatistics.getControllableProperties();
		Assertions.assertEquals("1", controllableProperty1.stream().filter(item -> item.getName().equals(property)).findFirst().get().getValue());
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
		Assert.assertNull(stats.get("Gain:ABC#GainValueControl(dB)"));

		qSYSCoreCommunicator.disconnect();
		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("ABC");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertNotNull(stats.get("Gain:ABC#GainControl(dB)"));
		ControllableProperty controllableProperty = new ControllableProperty();
		String value = "2.0";
		String property = "Gain:ABC#GainControl(dB)";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assertions.assertEquals("2.0", stats.get("Gain:ABC#GainCurrentValue(dB)"));
		List<AdvancedControllableProperty> controllableProperty1 = extendedStatistics.getControllableProperties();
		Assertions.assertEquals("2.0", controllableProperty1.stream().filter(item -> item.getName().equals(property)).findFirst().get().getValue());
	}

	/**
	 * Test filter not apply gain name
	 *
	 * Expect not exits gain name in statistics successfully
	 */
	@Test
	void TestFilterNotApplyGainName() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		String groupGainFormat = QSYSCoreConstant.GAIN + QSYSCoreConstant.COLON;
		for (Entry<String, String> stringEntry : stats.entrySet()) {
			Assert.assertEquals(false, stringEntry.getKey().contains(groupGainFormat));
		}
	}

	/**
	 * Test filter By gain name ABC
	 *
	 * Expect filter by gain name successfully
	 */
	@Test
	void TestFilterByGainName() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		String groupGainFormat = QSYSCoreConstant.GAIN + QSYSCoreConstant.COLON + "ABC#";
		String gainControl = groupGainFormat + GainControllingMetric.GAIN_VALUE_CONTROL.getMetric();
		String bypass = groupGainFormat + GainControllingMetric.BYPASS_CONTROL.getMetric();
		String invert = groupGainFormat + GainControllingMetric.INVERT_CONTROL.getMetric();
		String mute = groupGainFormat + GainControllingMetric.MUTE_CONTROL.getMetric();
		Assert.assertNull(stats.get(gainControl));
		Assert.assertNull(stats.get(bypass));
		Assert.assertNull(stats.get(invert));
		Assert.assertNull(stats.get(mute));

		//Apply filter by gain name ABC
		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("ABC");
		extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		stats = extendedStatistics.getStatistics();
		Assert.assertNotNull(stats.get(gainControl));
		Assert.assertNotNull(stats.get(bypass));
		Assert.assertNotNull(stats.get(invert));
		Assert.assertNotNull(stats.get(mute));
	}

	/**
	 * Test filter by gain name contain special character
	 *
	 * Expect filter by gain name contain special character throw exception
	 */
	@Test
	void TestFilterByGainNameContainSpecialCharacter() {
		qSYSCoreCommunicator.setFilterGainComponentByName("#");
		assertThrows(IllegalArgumentException.class, () -> qSYSCoreCommunicator.getMultipleStatistics(), "Error because Gain name contain special character");

		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("@");
		assertThrows(IllegalArgumentException.class, () -> qSYSCoreCommunicator.getMultipleStatistics(), "Error because Gain name contain special character");

		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("!");
		assertThrows(IllegalArgumentException.class, () -> qSYSCoreCommunicator.getMultipleStatistics(), "Error because Gain name contain special character");

		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("$");
		assertThrows(IllegalArgumentException.class, () -> qSYSCoreCommunicator.getMultipleStatistics(), "Error because Gain name contain special character");

		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("&");
		assertThrows(IllegalArgumentException.class, () -> qSYSCoreCommunicator.getMultipleStatistics(), "Error because Gain name contain special character");

		qSYSCoreCommunicator.internalDestroy();
		qSYSCoreCommunicator.setFilterGainComponentByName("%");
		assertThrows(IllegalArgumentException.class, () -> qSYSCoreCommunicator.getMultipleStatistics(), "Error because Gain name contain special character");
	}

	/**
	 * Test RetrieveGetMultipleStatistics
	 *
	 * Expect RetrieveGetMultipleStatistics successfully
	 */
	@Test
	void TestRetrieveGetMultipleStatistics() throws Exception {
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(16, aggregatedDeviceList.size());
	}

	/**
	 * Test RetrieveGetMultipleStatistics with device name is dante
	 *
	 * Expect RetrieveGetMultipleStatistics successfully
	 */
	@Test
	void TestAggregatedDeviceHasNameIsDanteTR() throws Exception {
		qSYSCoreCommunicator.setFilterDeviceByName("Software_Dante_RX_Software-Dante-RX-1");
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(1, aggregatedDeviceList.size());
	}

	/**
	 * Test RetrieveGetMultipleStatistics with device type is Streaming I/O
	 *
	 * Expect RetrieveGetMultipleStatistics successfully
	 */
	@Test
	void TestAggregatedDeviceHasTypeIsStreamingOI() throws Exception {
		qSYSCoreCommunicator.setFilterDeviceByQSYSType("Streaming I/O");
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
	}

	/**
	 * Test RetrieveGetMultipleStatistics with device type is not exits
	 *
	 * Expect RetrieveGetMultipleStatistics successfully
	 */
	@Test
	void TestAggregatedDeviceHasTypeIsNotExits() throws Exception {
		qSYSCoreCommunicator.setFilterDeviceByQSYSType("Streaming I/O123");
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(0, aggregatedDeviceList.size());
	}

	/**
	 * Test GetMultipleStatistics with historical is supported
	 *
	 * Expect GetMultipleStatistics with historical successfully
	 */
	@Test
	void TestGetMultipleStatisticsWithHistorical() throws Exception {
		qSYSCoreCommunicator.setHistoricalProperties("ProcessorTemperature(C)");
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		AggregatedDevice aggregatedDevice = aggregatedDeviceList.stream().filter(item -> item.getDeviceName().equals("Status_CeeSalt-Core110f")).findFirst().orElse(new AggregatedDevice());
		Assert.assertNotNull(aggregatedDevice.getDynamicStatistics().get("ProcessorTemperature(C)"));
	}

	/**
	 * Test GetMultipleStatistics with historical is not supported
	 *
	 * Expect GetMultipleStatistics with historical successfully
	 */
	@Test
	void TestGetMultipleStatisticsWithHistoricalIsNotSupported() throws Exception {
		qSYSCoreCommunicator.setHistoricalProperties("Status, HardwareModel, StatusLed");
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		for (AggregatedDevice aggregatedDevice : aggregatedDeviceList) {
			Assert.assertEquals(0, aggregatedDevice.getDynamicStatistics().size());
		}
	}

	/**
	 * Test aggregated device with list properties
	 *
	 * Expect aggregated device with list properties successfully
	 */
	@Test
	void TestAggregatedDeviceByListProperties() throws Exception {
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		AggregatedDevice aggregatedDevice = aggregatedDeviceList.stream().filter(item -> item.getDeviceName().equalsIgnoreCase("Status_CeeSalt-Decoder")).findFirst().orElse(new AggregatedDevice());
		Map<String, String> stats = aggregatedDevice.getProperties();
		Assert.assertEquals("Status_CeeSalt-Decoder", aggregatedDevice.getDeviceName());
		Assert.assertEquals("Status_CeeSalt-Decoder", aggregatedDevice.getDeviceId());
		Assert.assertEquals(true, aggregatedDevice.getDeviceOnline());
		//Properties
		Assert.assertEquals("false", stats.get("AuxPower"));
		Assert.assertEquals("None", stats.get("ClockOffset"));
		Assert.assertEquals("0.0", stats.get("CPUTemperature(C)"));
		Assert.assertEquals("0", stats.get("Fan1"));
		Assert.assertEquals("0", stats.get("Fan2"));
		Assert.assertEquals("None", stats.get("Grandmaster"));
		Assert.assertEquals("0.0", stats.get("I/OTemperature(C)"));
		Assert.assertEquals("None", stats.get("ParentPort"));
		Assert.assertEquals("false", stats.get("PoEEqual90w"));
		Assert.assertEquals("false", stats.get("PoELessThan90w"));
		Assert.assertEquals("Not Present", stats.get("Status"));
		Assert.assertEquals("Not Present", stats.get("StatusLed"));
		Assert.assertEquals("0.0", stats.get("VPUTemperature(C)"));
		for (VideoIODeviceMetric deviceMetric : VideoIODeviceMetric.values()) {
			if (deviceMetric.getMetric().contains("#")) {
				Assert.assertEquals("None", stats.get(deviceMetric.getMetric()));
			}
		}
	}

	/**
	 * Test Sennheiser device
	 *
	 * Expect aggregated device with list properties successfully
	 */
	@Test
	void TestSennheiserDevice() throws Exception {
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		qSYSCoreCommunicator.getMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		Optional<AggregatedDevice> deviceOptional = aggregatedDeviceList.stream().filter(device -> device.getDeviceId().contains("Sennheiser")).findFirst();
		if (deviceOptional.isPresent()) {
			AggregatedDevice sennheiserDevice = deviceOptional.get();
			Map<String, String> stats = sennheiserDevice.getProperties();
			Assertions.assertEquals("OK", stats.get(SennheiserDeviceMetric.STATUS.getMetric()));
			Assertions.assertEquals("SLCMZ", stats.get(SennheiserDeviceMetric.DEVICE.getMetric()));
			Assertions.assertEquals("Manual", stats.get(SennheiserDeviceMetric.CONNECTION_MODE.getMetric()));
			Assertions.assertEquals("-120.0", stats.get(SennheiserDeviceMetric.AUDIO_LEVEL.getMetric()));
			Assertions.assertEquals("SymphonyLab", stats.get(SennheiserDeviceMetric.DEVICE_LOCATION.getMetric()));
			Assertions.assertEquals("172.31.254.105", stats.get(SennheiserDeviceMetric.IP_ADDRESS.getMetric()));
			Assertions.assertEquals("5.0", stats.get(SennheiserDeviceMetric.LED_BRIGHTNESS.getMetric()));
			Assertions.assertEquals("Activated", stats.get(SennheiserDeviceMetric.LED_ON_OFF.getMetric()));
			Assertions.assertEquals("Activated", stats.get(SennheiserDeviceMetric.MUTE.getMetric()));
			Assertions.assertEquals("Orange", stats.get(SennheiserDeviceMetric.MUTE_COLOR.getMetric()));
			Assertions.assertEquals("Activated", stats.get(SennheiserDeviceMetric.NOISE_GATE.getMetric()));
			Assertions.assertEquals("45", stats.get(SennheiserDeviceMetric.DEVICE_PORT.getMetric()));
			Assertions.assertEquals("Activated", stats.get(SennheiserDeviceMetric.PRIORITY_ZONE.getMetric()));
			Assertions.assertEquals("Off", stats.get(SennheiserDeviceMetric.SOUND_PROFILE.getMetric()));
			Assertions.assertEquals("Cyan", stats.get(SennheiserDeviceMetric.ON_COLOR.getMetric()));
			Assertions.assertEquals("On", stats.get(SennheiserDeviceMetric.EXCLUSION_1_ZONES.getMetric()));
			Assertions.assertEquals("On", stats.get(SennheiserDeviceMetric.EXCLUSION_2_ZONES.getMetric()));
			Assertions.assertEquals("On", stats.get(SennheiserDeviceMetric.EXCLUSION_3_ZONES.getMetric()));
			Assertions.assertEquals("On", stats.get(SennheiserDeviceMetric.EXCLUSION_4_ZONES.getMetric()));
			Assertions.assertEquals("On", stats.get(SennheiserDeviceMetric.EXCLUSION_5_ZONES.getMetric()));
			Assertions.assertEquals("Normal", stats.get(SennheiserDeviceMetric.NOISE_LEVEL.getMetric()));
			Assertions.assertEquals("Deactivated", stats.get(SennheiserDeviceMetric.TRU_VOICE_LIFT.getMetric()));
		}
	}

	/**
	 * Test Sennheiser device
	 *
	 * Expect aggregated device with list properties successfully
	 */
	@Test
	void TestMiddleAtlanticDevice() throws Exception {
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		qSYSCoreCommunicator.getMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		Optional<AggregatedDevice> deviceOptional = aggregatedDeviceList.stream().filter(device -> device.getDeviceId().contains("MiddleAtlantic")).findFirst();
		if (deviceOptional.isPresent()) {
			AggregatedDevice middleAtlanticDevice = deviceOptional.get();
			Map<String, String> stats = middleAtlanticDevice.getProperties();
			for (int i = 1; i <= 8; i++) {
				Assertions.assertNotNull(stats.get("Outlet" + i + "#State"));
				Assertions.assertNotNull(stats.get("Outlet" + i + "#Power"));
				Assertions.assertNotNull(stats.get("Outlet" + i + "#Cycle"));
				Assertions.assertNotNull(stats.get("Outlet" + i + "#CycleTime"));
				Assertions.assertNotNull(stats.get("Outlet" + i + "#Name"));
			}
			Assertions.assertEquals("OK", stats.get(MiddleAtlanticMetric.STATUS.getMetric()));
			Assertions.assertEquals("F0LW2A6002U", stats.get(MiddleAtlanticMetric.SERIAL_NUMBER.getMetric()));
			Assertions.assertEquals("UPX-RLNK-1500R-8", stats.get(MiddleAtlanticMetric.DEVICE_MODEL.getMetric()));
			Assertions.assertEquals("172.31.254.31", stats.get(MiddleAtlanticMetric.IP_ADDRESS.getMetric()));
			Assertions.assertEquals("00:1E:C5:10:03:B3", stats.get(MiddleAtlanticMetric.MAC_ADDRESS.getMetric()));
			Assertions.assertEquals("S02E03", stats.get(MiddleAtlanticMetric.DEVICE_FIRMWARE.getMetric()));
			Assertions.assertEquals("Normal", stats.get(MiddleAtlanticMetric.OUTPUT_SOURCE.getMetric()));
			Assertions.assertEquals("117.1", stats.get(MiddleAtlanticMetric.INPUT_VOLTAGE.getMetric()));
			Assertions.assertEquals("0.2", stats.get(MiddleAtlanticMetric.INPUT_CURRENT.getMetric()));
			Assertions.assertEquals("117.0", stats.get(MiddleAtlanticMetric.OUTPUT_VOLTAGE.getMetric()));
			Assertions.assertEquals("0.0", stats.get(MiddleAtlanticMetric.OUTPUT_CURRENT.getMetric()));
			Assertions.assertEquals("0", stats.get(MiddleAtlanticMetric.OUTPUT_POWER.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_BATTERY_FAULT.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_BATTERY_GROUND_FAULT.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_CHARGER_FAIL.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_FAN_FAIL.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_FUSE_FAIL.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_OUTPUT_OFF.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_OVERLOAD.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_OVER_TEMPERATURE.getMetric()));
			Assertions.assertEquals("false", stats.get(MiddleAtlanticMetric.ALARM_UPS_SHUTDOWN.getMetric()));
			Assertions.assertEquals("true", stats.get(MiddleAtlanticMetric.BUZZER_ENABLE_STATUS.getMetric()));
			Assertions.assertEquals("Off", stats.get(MiddleAtlanticMetric.BUZZER_ENABLE.getMetric()));
			Assertions.assertEquals("Off", stats.get(MiddleAtlanticMetric.RESTART_UPS.getMetric()));
			Assertions.assertEquals("1", stats.get(MiddleAtlanticMetric.RESTART_UPS_DELAY_TIME.getMetric()));
		}
	}

	/**
	 * Test Sennheiser device
	 *
	 * Expect aggregated device with list properties successfully
	 */
	@Test
	void TestNetgearAVLineSwitchDevice() throws Exception {
		qSYSCoreCommunicator.setFilterPluginByName(QSYSCoreConstant.SENNHEISER + "," + QSYSCoreConstant.MIDDLE_ATLANTIC);
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		qSYSCoreCommunicator.getMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = qSYSCoreCommunicator.retrieveMultipleStatistics();
		Optional<AggregatedDevice> deviceOptional = aggregatedDeviceList.stream().filter(device -> device.getDeviceId().contains("MiddleAtlantic")).findFirst();
		if (deviceOptional.isPresent()) {
			AggregatedDevice netgearDevice = deviceOptional.get();
			Map<String, String> stats = netgearDevice.getProperties();
			Assertions.assertEquals("VNOC Test", stats.get(NetgearDeviceMetric.SYSTEM_NAME.getMetric()));
			Assertions.assertEquals("M4250-9G1F-PoE+", stats.get(NetgearDeviceMetric.MODEL.getMetric()));
			Assertions.assertEquals("6YW1285BA00FD", stats.get(NetgearDeviceMetric.SERIAL_NUMBER.getMetric()));
			Assertions.assertEquals("94:18:65:6B:66:39", stats.get(NetgearDeviceMetric.MAC_ADDRESS.getMetric()));
			Assertions.assertEquals("13.0.4.17", stats.get(NetgearDeviceMetric.FIRMWARE_VERSION.getMetric()));
			Assertions.assertEquals("7 days, 10 hrs, 6 mins, 57 secs", stats.get(NetgearDeviceMetric.UPTIME.getMetric()));
			Assertions.assertEquals("1.0.0.11", stats.get(NetgearDeviceMetric.CONFIGURATION_VERSION.getMetric()));
			Assertions.assertEquals("OK", stats.get(NetgearDeviceMetric.STATUS.getMetric()));
			Assertions.assertEquals("10.100.0.204", stats.get(NetgearDeviceMetric.IP_ADDRESS.getMetric()));
			Assertions.assertEquals("avispl", stats.get(NetgearDeviceMetric.USER_NAME.getMetric()));
			for (int i = 1; i <= 12; i++) {
				Assertions.assertNotNull(stats.get("Port" + String.format("%02d", i) + "#Description"));
				Assertions.assertNotNull(stats.get("Port" + String.format("%02d", i) + "#Connected"));
				Assertions.assertNotNull(stats.get("Port" + String.format("%02d", i) + "#Powered"));
				Assertions.assertNotNull(stats.get("Port" + String.format("%02d", i) + "#Error"));
				Assertions.assertNotNull(stats.get("Port" + String.format("%02d", i) + "#Trunk"));
			}
		}
	}
}