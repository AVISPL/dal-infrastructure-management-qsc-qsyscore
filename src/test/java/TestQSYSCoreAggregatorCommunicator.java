/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
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
		qSYSCoreCommunicator.setHost("***REMOVED***");
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
}