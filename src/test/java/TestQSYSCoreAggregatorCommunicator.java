/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.QSYSCoreAggregatorCommunicator;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.ProcessorDevice;
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

	@Test
	public void testGetMultiplestatictis() throws Exception {
		final ExtendedStatistics[] extendedStatistics = new ExtendedStatistics[1];
		Assertions.assertDoesNotThrow(() -> extendedStatistics[0] = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0));
		Map<String, String> stats = extendedStatistics[0].getStatistics();
		Assertions.assertEquals("3-440F59FA6034C59670FF3C0928929607", stats.get(QSYSCoreMonitoringMetric.DEVICE_ID.getName()));
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
			Assertions.assertNotNull(aggregatedDevice.getDeviceId());
			Assertions.assertNotNull(aggregatedDevice.getDeviceName());
			Assertions.assertNotEquals(0, aggregatedDevice.getProperties().size());
		});
	}

	@Test
	public void testFilterGainName() {

	}

	@Test
	public void testCustom() {

	}
}