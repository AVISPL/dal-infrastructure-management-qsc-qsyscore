import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.QSYSCoreAggregatorCommunicator;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.ProcessorDeviceMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreURL;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.ProcessorDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DesignInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DesignResult;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DeviceInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DeviceInfoData;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DeviceLANInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DeviceLANInfoData;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.InterfaceInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.LoginInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreMonitoringMetric;
import com.avispl.symphony.dal.util.ControllablePropertyFactory;
import com.avispl.symphony.dal.util.StringUtils;

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
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		List<AdvancedControllableProperty> advancedControllableProperty=extendedStatistics.getControllableProperties();
	}

	@Test
	public void testGetGain() {
		Map<String, String> stats = new HashMap<>();
		List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();
	}

	@Test
	public void testControlProperty() throws Exception {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty("Gain:ABC#Bypass");
		controllableProperty.setValue(true);
		qSYSCoreCommunicator.controlProperty(controllableProperty);
	}

	@Test
	public void testGetMonitoringAggregatedDevice() throws Exception {
		qSYSCoreCommunicator.deviceMap=new TreeMap<>();
		qSYSCoreCommunicator.deviceMap.put("Status_CeeSalt-Core110f",new ProcessorDevice());
		qSYSCoreCommunicator.getMultipleStatistics();
	}

	@Test
	public void testRetrieveAggregatorDevice() throws Exception {
		qSYSCoreCommunicator.getMultipleStatistics();
		qSYSCoreCommunicator.retrieveMultipleStatistics();
		TimeUnit.MILLISECONDS.sleep(60000);
		List<AggregatedDevice> aggregatedDevices=qSYSCoreCommunicator.retrieveMultipleStatistics();
		System.out.println(aggregatedDevices.size());
	}
}