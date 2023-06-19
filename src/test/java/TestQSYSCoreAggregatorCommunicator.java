import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.QSYSCoreAggregatorCommunicator;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.LoginInfo;
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
		qSYSCoreCommunicator.setHost("127.0.0.1");
		qSYSCoreCommunicator.setLogin("technican");
		qSYSCoreCommunicator.setPassword("12345678");
		qSYSCoreCommunicator.init();
		qSYSCoreCommunicator.connect();
	}

	@Test
	public void testGetMultiplestatictis() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stast = extendedStatistics.getStatistics();
		System.out.println(stast.size());
		for (Entry<String, String> s : stast.entrySet()) {
			System.out.println(s.getKey() + ",  " + s.getValue());
		}

		LoginInfo loginInfo = LoginInfo.createLoginInfoInstance();
		System.out.println(loginInfo.getToken());
	}

	@Test
	public void testGetGain() {
		Map<String, String> stats = new HashMap<>();
		List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();
	}

}