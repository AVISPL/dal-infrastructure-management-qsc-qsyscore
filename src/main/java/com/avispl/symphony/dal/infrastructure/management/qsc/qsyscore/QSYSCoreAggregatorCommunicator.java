/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.*;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.math.IntMath;
import javax.security.auth.login.FailedLoginException;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.streamiodevice.StreamInputDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.streamiodevice.StreamOutputDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.ComponentInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.ComponentWrapper;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DesignInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DeviceInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DeviceLANInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.LoginInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreDesignMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreNetworkMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreRedundancyMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreSystemMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.RedundancyWrapper;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.rpc.RpcMethod;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.statistics.DynamicStatisticsDefinitions;
import com.avispl.symphony.dal.util.ControllablePropertyFactory;
import com.avispl.symphony.dal.util.StringUtils;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * QSYSCoreAggregatorCommunicator
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/30/2023
 * @since 1.0.0
 */
public class QSYSCoreAggregatorCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {

	/**
	 * Indicates whether a device is considered as paused.
	 * True by default so if the system is rebooted and the actual value is lost -> the device won't start stats
	 * collection unless the {@link QSYSCoreAggregatorCommunicator#retrieveMultipleStatistics()} method is called which will change it
	 * to a correct value
	 */
	private volatile boolean devicePaused = true;

	/**
	 * We don't want the statistics to be collected constantly, because if there's not a big list of devices -
	 * new devices' statistics loop will be launched before the next monitoring iteration. To avoid that -
	 * this variable stores a timestamp which validates it, so when the devices' statistics is done collecting, variable
	 * is set to currentTime + 30s, at the same time, calling {@link #retrieveMultipleStatistics()} and updating the
	 */
	private long nextDevicesCollectionIterationTimestamp;

	/**
	 * This parameter holds timestamp of when we need to stop performing API calls
	 * It used when device stop retrieving statistic. Updated each time of called #retrieveMultipleStatistics
	 */

	private volatile long validRetrieveStatisticsTimestamp;

	/**
	 * Aggregator inactivity timeout. If the {@link QSYSCoreAggregatorCommunicator#retrieveMultipleStatistics()}  method is not
	 * called during this period of time - device is considered to be paused, thus the Cloud API
	 * is not supposed to be called
	 */
	private static final long retrieveStatisticsTimeOut = 3 * 60 * 1000;

	/**
	 * Update the status of the device.
	 * The device is considered as paused if did not receive any retrieveMultipleStatistics()
	 * calls during {@link QSYSCoreAggregatorCommunicator}
	 */
	private synchronized void updateAggregatorStatus() {
		devicePaused = validRetrieveStatisticsTimestamp < System.currentTimeMillis();
	}

	/**
	 * Uptime time stamp to valid one
	 */
	private synchronized void updateValidRetrieveStatisticsTimestamp() {
		validRetrieveStatisticsTimestamp = System.currentTimeMillis() + retrieveStatisticsTimeOut;
		updateAggregatorStatus();
	}

	class DeviceLoader implements Runnable {
		private volatile List<String> deviceIds;
		private volatile boolean inProgress;
		private volatile boolean dataFetchCompleted = false;
		/**
		 * Parameters constructors
		 *
		 * @param deviceIds list all id of device
		 */
		public DeviceLoader(List<String> deviceIds) {
			this.deviceIds = deviceIds;
			inProgress = true;
		}

		@Override
		public void run() {
			loop:
			while (inProgress) {
				try {
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e) {
						logger.info(String.format("Sleep for 0.5 second was interrupted with error message: %s", e.getMessage()));
					}

					if (!inProgress) {
						break loop;
					}

					// next line will determine whether QSYS monitoring was paused
					updateAggregatorStatus();
					if (devicePaused) {
						continue loop;
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Fetching aggregated devices statuses for device list: " + String.join(",", this.deviceIds));
					}

					long currentTimestamp = System.currentTimeMillis();
					if (!dataFetchCompleted && nextDevicesCollectionIterationTimestamp <= currentTimestamp) {
						if (!deviceMap.isEmpty()) {
							retrieveAggregatedDeviceByIdList(this.deviceIds);
						}
						dataFetchCompleted = true;
					}

					while (nextDevicesCollectionIterationTimestamp > System.currentTimeMillis()) {
						try {
							TimeUnit.MILLISECONDS.sleep(1000);
						} catch (InterruptedException e) {
							logger.info(String.format("Sleep for 1 second was interrupted with error message: %s", e.getMessage()));
						}
					}

					if (!inProgress) {
						break loop;
					}
					if (dataFetchCompleted) {
						nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 30000;
						dataFetchCompleted = false;
					}

					if (logger.isDebugEnabled()) {
						logger.debug("Finished collecting devices statistics cycle at " + new Date());
					}
				} catch (Exception e) {
					logger.error("Unexpected error occurred during main device collection cycle", e);
				}
			}
			logger.debug("Main device collection loop is completed, in progress marker: " + inProgress);
		}
	}

	/**
	 * Executor that runs all the async operations, that is posting and
	 * {@link #devicesExecutionPool} is keeping track of
	 */
	private ExecutorService executorService;
	/**
	 * Executor service for QRC requests
	 * @since 1.2.0
	 * */
	private ExecutorService qrcExecutorService;
	/**
	 * QRC process, responsible for aggregated devices collection from the remote API
	 * @since 1.2.0
	 * */
	private CompletableFuture<?> qrcProcess;

	/**
	 * Local cache for QRC statistics, collected in {@link #qrcProcess}
	 * @since 1.2.0
	 * */
	private ConcurrentHashMap<String, String> qrcStatistics = new ConcurrentHashMap<>();

	/**
	 * Local cache for QRC controls data, collected in {@link #qrcProcess}
	 * @since 1.2.0
	 * */
	private volatile List<AdvancedControllableProperty> qrcControls = new ArrayList<>();

	/**
	 * Configurable property for historical properties, comma separated values kept as set locally
	 */
	private Set<String> historicalProperties = new HashSet<>();

	/**
	 * Polling interval which applied in adapter
	 */
	private volatile int localPollingInterval = 0;

	/**
	 * Number of threads in a thread pool reserved for the device statistics collection
	 */
	private volatile int deviceStatisticsCollectionThreads;

	/** Adapter metadata properties - adapter version and build date */
	private Properties adapterProperties;

	/**
	 * Device adapter instantiation timestamp.
	 */
	private long adapterInitializationTimestamp;

	/**
	 * store pollingInterval adapter properties
	 */
	private volatile String pollingInterval;

	private String qrcPort = String.valueOf(QSYSCoreConstant.QRC_PORT);
	private boolean isEmergencyDelivery = false;
	private LoginInfo loginInfo;
	private ExtendedStatistics localExtStats;

	/**
	 * Enable/disable controllable properties on aggregated devices
	 * */
	private boolean configManagement = false;
	/**
	 * Filter by plugin name
	 */
	private String filterPluginByName;

	/**
	 * Set store all name for filter plugin
	 */
	private Set<String> filterPluginByNameSet = new HashSet<>();

	/**
	 * Filter gain by name
	 */
	private String filterGainComponentByName;

	/**
	 * Set store all name for filter gain
	 */
	private Set<String> filterGainComponentByNameSet = new HashSet<>();

	/**
	 * Map store and update all device can not get information
	 */
	private volatile Map<String, Integer> errorDeviceMap = new HashMap<>();

	/**
	 * Stack store all error device id to loop throughout it
	 */
	private Deque<String> deviceIdDequeue = new ArrayDeque<>();

	/**
	 * Filter model by name
	 */
	private String filterDeviceByQSYSType;

	/**
	 * Set store all type for filter device
	 */
	private Set<String> filterDeviceByQSYSTypeSet;

	/**
	 * list all thread
	 */
	private List<Future> deviceExecutionPool = new ArrayList<>();

	/**
	 * Filter component by name
	 */
	private String filterDeviceByName;

	/**
	 * Set store all name for filter component
	 */
	private Set<String> filterDeviceByNameSet;

	/**
	 * Store the device name
	 */
	private String aggregatorDeviceName;

	/**
	 * Map save all device
	 */
	public volatile TreeMap<String, QSYSPeripheralDevice> deviceMap = new TreeMap<>();

	/**
	 * Map of ID device and device detail
	 */
	public ConcurrentHashMap<String, QSYSPeripheralDevice> mapOfIdAndAggregatedDeviceList = new ConcurrentHashMap<>();

	/**
	 * Snapshot of candidates used by the round-robin selector.
	 * Replaced as a whole when refreshed; do not mutate in place.
	 */
	private List<String> rrSnapshot = new ArrayList<>();

	/**
	 * Next position in {@code rrSnapshot} for round-robin selection.
	 * Increments on each pick and wraps back to 0 at the end.
	 */
	private int rrIndex = 0;

	/**
	 * Pool for keeping all the async operations in, to track any operations in progress and cancel them if needed
	 */
	private List<Future> devicesExecutionPool = new ArrayList<>();

	/**
	 * qrcCommunicator instance
	 */
	private QRCCommunicator qrcCommunicator;

	/**
	 * A mapper for reading and writing JSON using Jackson library.
	 * ObjectMapper provides functionality for converting between Java objects and JSON.
	 * It can be used to serialize objects to JSON format, and deserialize JSON data to objects.
	 */
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * A private final ReentrantLock instance used to provide exclusive access to a shared resource
	 * that can be accessed by multiple threads concurrently. This lock allows multiple reentrant
	 * locks on the same shared resource by the same thread.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * List of aggregated device
	 */
	private List<AggregatedDevice> resultAggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * The variable checks if qrcCommunicator is initial at the first time
	 */
	private volatile boolean isQrcCommunicatorFirstTimeInit = true;

	public QSYSCoreAggregatorCommunicator() throws IOException {
		adapterProperties = new Properties();
		adapterProperties.load(getClass().getResourceAsStream("/version.properties"));
		this.setTrustAllCertificates(true);

		qrcExecutorService = Executors.newFixedThreadPool(1);
	}

	/**
	 * Retrieves {@link #configManagement}
	 *
	 * @return value of {@link #configManagement}
	 */
	public boolean isConfigManagement() {
		return configManagement;
	}

	/**
	 * Sets {@link #configManagement} value
	 *
	 * @param configManagement new value of {@link #configManagement}
	 */
	public void setConfigManagement(boolean configManagement) {
		this.configManagement = configManagement;
	}

	/**
	 * Retrieves {@link #historicalProperties}
	 *
	 * @return value of {@link #historicalProperties}
	 */
	public String getHistoricalProperties() {
		return String.join(",", this.historicalProperties);
	}

	/**
	 * Sets {@link #historicalProperties} value
	 *
	 * @param historicalProperties new value of {@link #historicalProperties}
	 */
	public void setHistoricalProperties(String historicalProperties) {
		this.historicalProperties.clear();
		Arrays.asList(historicalProperties.split(",")).forEach(propertyName -> {
			this.historicalProperties.add(propertyName.trim());
		});
	}

	/**
	 * Retrieves {@link #qrcPort}
	 *
	 * @return value of {@link #qrcPort}
	 */
	public String getQrcPort() {
		return qrcPort;
	}

	/**
	 * Sets {@link #qrcPort} value
	 *
	 * @param qrcPort new value of {@link #qrcPort}
	 */
	public void setQrcPort(String qrcPort) {
		this.qrcPort = qrcPort;
	}

	/**
	 * Retrieves {@link #filterGainComponentByName}
	 *
	 * @return value of {@link #filterGainComponentByName}
	 */
	public String getFilterGainComponentByName() {
		return filterGainComponentByName;
	}

	/**
	 * Sets {@link #filterGainComponentByName} value
	 *
	 * @param filterGainComponentByName new value of {@link #filterGainComponentByName}
	 */
	public void setFilterGainComponentByName(String filterGainComponentByName) {
		this.filterGainComponentByName = filterGainComponentByName;
	}

	/**
	 * Retrieves {@link #filterPluginByName
	 *
	 * @return value of {@link #filterPluginByName}
	 */
	public String getFilterPluginByName() {
		return filterPluginByName;
	}

	/**
	 * Sets {@link #filterPluginByName} value
	 *
	 * @param filterPluginByName new value of {@link #filterGainComponentByName}
	 */
	public void setFilterPluginByName(String filterPluginByName) {
		this.filterPluginByName = filterPluginByName;
	}

	/**
	 * Retrieves {@link #filterDeviceByQSYSType}
	 *
	 * @return value of {@link #filterDeviceByQSYSType}
	 */
	public String getFilterDeviceByQSYSType() {
		return filterDeviceByQSYSType;
	}

	/**
	 * Sets {@link #filterDeviceByQSYSType} value
	 *
	 * @param filterDeviceByQSYSType new value of {@link #filterDeviceByQSYSType}
	 */
	public void setFilterDeviceByQSYSType(String filterDeviceByQSYSType) {
		this.filterDeviceByQSYSType = filterDeviceByQSYSType;
	}

	/**
	 * Retrieves {@link #filterDeviceByName}
	 *
	 * @return value of {@link #filterDeviceByName}
	 */
	public String getFilterDeviceByName() {
		return filterDeviceByName;
	}

	/**
	 * Sets {@link #filterDeviceByName} value
	 *
	 * @param filterDeviceByName new value of {@link #filterDeviceByName}
	 */
	public void setFilterDeviceByName(String filterDeviceByName) {
		this.filterDeviceByName = filterDeviceByName;
	}

	/**
	 * Retrieves {@link #pollingInterval}
	 *
	 * @return value of {@link #pollingInterval}
	 */
	public String getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * Sets {@link #pollingInterval} value
	 *
	 * @param pollingInterval new value of {@link #pollingInterval}
	 */
	public void setPollingInterval(String pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to get the list of statistics to be displayed
	 *
	 * @return List<Statistics> This return the list of statistics.
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		if (qrcExecutorService == null) {
			qrcExecutorService = Executors.newFixedThreadPool(1);
		}
		ExtendedStatistics extendedStatistics = new ExtendedStatistics();
		// This is to make sure if populateMonitoringAndControllableProperties the statistics is being fetched before/after any set of control operations
		reentrantLock.lock();
		try {
			if (!isEmergencyDelivery) {
//				deviceMap.clear();
				Map<String, String> stats = new HashMap<>();
				Map<String, String> dynamicStatistics = new HashMap<>();
				List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();

				// The QSys Core socket closes automatically after 60 seconds (as specified in the API document),
				// however if we let it close automatically that results in unexpected behavior for the adapter, so we close it intentionally.
				resetSocketConnection();

				if (qrcCommunicator == null) {
					initQRCCommunicator();
					isQrcCommunicatorFirstTimeInit = false;
				}

				//Create loginInfo
				if (loginInfo == null) {
					loginInfo = new LoginInfo();
				}

				retrieveTokenFromCore();

				//Because there are some threads that keep running when the next getMultiple is called,
				// so we have to stop all those threads just before the next getMultiple runs
				if (executorService != null) {
					for (Future future : deviceExecutionPool) {
						future.cancel(true);
					}
					deviceExecutionPool.clear();
				}

				filterGainComponentByNameSet = handleGainInputFromUser(filterGainComponentByName);
				filterPluginByNameSet = handleSplitPluginConfig();
				if (localPollingInterval == 0) {
					filterDeviceByNameSet = convertUserInput(filterDeviceByName);
					updateFilterDeviceTypeSet();
				}
				populateQSYSAggregatorMonitoringData(stats);

				if (qrcProcess == null || qrcProcess.isDone() || qrcProcess.isCompletedExceptionally()) {
					qrcProcess = runAsync(() ->
                            populateQSYSComponent(qrcStatistics, qrcControls)
//                            populateQSYSComponent(stats, controllableProperties);
									, qrcExecutorService).whenComplete((unused, throwable) -> {
						if (logger.isDebugEnabled() && throwable == null) {
							logger.debug("QRC Process is completed. Ready for the new cycle when getMultipleStatistics() call is addressed.");
						} else {
							logger.error("Unable to retrieve QRC Data.", throwable);
						}
						stats.putAll(qrcStatistics);
						controllableProperties.addAll(qrcControls);
					});
				}

				retrieveMetadata(stats);
				reconcileCacheWithDeviceMap(stats);

				int currentSizeDeviceMap = deviceMap.isEmpty()
						? mapOfIdAndAggregatedDeviceList.size()
						: deviceMap.size();
				dynamicStatistics.put(QSYSCoreConstant.NUMBER_OF_DEVICE, String.valueOf(currentSizeDeviceMap));

				extendedStatistics.setStatistics(stats);
				extendedStatistics.setDynamicStatistics(dynamicStatistics);
				extendedStatistics.setControllableProperties(controllableProperties);
				localExtStats = extendedStatistics;
			}
			isEmergencyDelivery = false;
		} finally {
			reentrantLock.unlock();
		}

		return Collections.singletonList(localExtStats);
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to control specific property
	 *
	 * @param controllableProperty This is the property to be controlled
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		reentrantLock.lock();

		if (localExtStats == null) {
			return;
		}

		try {
			isEmergencyDelivery = true;

			resetSocketConnection();

			//delete when done
			if (qrcCommunicator == null) {
				initQRCCommunicator();
			}
			String propertyControl = controllableProperty.getProperty();
			String value = String.valueOf(controllableProperty.getValue());
			String deviceId = removeAggregatorPrefix(controllableProperty.getDeviceId());

			if (propertyControl == null) {
				throw new IllegalArgumentException("PropertyControl must not be null");
			}
			String[] splitProperty = propertyControl.split(QSYSCoreConstant.HASH);

			// Ex: Gain:Named Component#Gain Value Control
			// metricName = Gain Value Control
			// namedComponent = Named Component
			String metricName = propertyControl;
			if (propertyControl.contains(QSYSCoreConstant.HASH)) {
				metricName = splitProperty[1];
			}
			QSYSPeripheralDevice aggregatedDevice = mapOfIdAndAggregatedDeviceList.get(deviceId);
			if (aggregatedDevice != null) {
				Map<String, String> properties = aggregatedDevice.getStats();
				List<AdvancedControllableProperty> advancedControllableProperties = aggregatedDevice.getAdvancedControllableProperties();
				String QSYSTypeCommand = aggregatedDevice.getType();

				if (QSYSTypeCommand == null) {
					logger.error(QSYSCoreConstant.MISSING_QSYS_TYPE_ERR + deviceId + QSYSCoreConstant.SEMICOLON + properties);
					throw new IllegalArgumentException(QSYSCoreConstant.MISSING_QSYS_TYPE_ERR + deviceId);
				}
				String metricProperty = getMetricProperty(propertyControl, QSYSTypeCommand, metricName);
				String metric = getMetric(propertyControl, QSYSTypeCommand, metricName);

				if (StringUtils.isNotNullOrEmpty(metricProperty)) {
					if (splitProperty[0].contains(QSYSCoreConstant.CHANNEL)) {
						String indexChannel = convertChannelNameToIndex(splitProperty[0]);
						metricProperty = metricProperty.replace(QSYSCoreConstant.FORMAT_STRING, indexChannel);
					}
					handleControlAggregated(metricProperty, deviceId, value);
					aggregatedDevice.controlDevice(aggregatedDevice, metric, value, propertyControl);
					updateValueForTheControllableProperty(propertyControl, value, properties, advancedControllableProperties);
				}
			}
			List<String> splitComponent = Arrays.asList(splitProperty[0].split(QSYSCoreConstant.COLON, 2));
			switch (splitComponent.get(0)) {
				case QSYSCoreConstant.GAIN:
					String gainComponent = splitComponent.size() > 1 ? splitComponent.get(1) : splitComponent.get(0);
					gainControl(metricName, gainComponent, value);
					updateGainControlByMetricName(metricName, value, propertyControl);
					break;
				default:
					logger.debug("Component Name doesn't support: " + metricName);
			}
			TimeUnit.MILLISECONDS.sleep(500);
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to control a list of properties
	 *
	 * @param controllableProperties This is the list of properties to be controlled
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("QSYSCoreCommunicator: Controllable properties cannot be null or empty");
		}

		for (ControllableProperty controllableProperty : controllableProperties) {
			controlProperty(controllableProperty);
		}

	}

	/**
	 * {@inheritDoc}
	 * Get information of aggregated device
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		if (mapOfIdAndAggregatedDeviceList.isEmpty()) {
			return Collections.emptyList();
		}
		refreshTimestamps();

//		if (!deviceMap.isEmpty()) {
//			retrieveAggregatedDeviceByIdList(new ArrayList<>(deviceMap.keySet()));
//		}
		Map<String, QSYSPeripheralDevice> snapshot = snapshotDevices();
		resultAggregatedDeviceList.clear();
		for (Map.Entry<String, QSYSPeripheralDevice> entry : snapshot.entrySet()) {
			AggregatedDevice aggregatedDevice = buildAggregatedDevice(entry.getKey(), entry.getValue());
			if (aggregatedDevice != null) {
				if (!configManagement) {
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("configManagement is set to false, removing device %s controllable properties.", aggregatedDevice.getDeviceId()));
					}
					// Since SY core caches controllable properties, we need to create a dummy object to replace existing controls with
					List<AdvancedControllableProperty> controls = aggregatedDevice.getControllableProperties();
					controls.clear();
					controls.add(ControllablePropertyFactory.createText(QSYSCoreConstant.EMPTY,QSYSCoreConstant.EMPTY));
				}
				resultAggregatedDeviceList.add(aggregatedDevice);
			}
		}

		if (localPollingInterval == 0) {
			localPollingInterval = QSYSCoreConstant.MIN_POLLING_INTERVAL;
			localPollingInterval = calculatingLocalPollingInterval();
			deviceStatisticsCollectionThreads = calculatingThreadQuantity();

			for (String deviceId : errorDeviceMap.keySet()) {
				if (deviceMap.containsKey(deviceId)) {
					deviceIdDequeue.addLast(deviceId);
				}
			}

			for (String deviceId : deviceMap.keySet()) {
				if (!errorDeviceMap.containsKey(deviceId)) {
					deviceIdDequeue.addLast(deviceId);
				}
			}
		} else {
			int batchSize = QSYSCoreConstant.MAX_DEVICE_QUANTITY_PER_THREAD;
			refillQueueRoundRobin(batchSize);
		}
		int currentSizeDeviceMap = deviceMap.isEmpty()
				? mapOfIdAndAggregatedDeviceList.size()
				: deviceMap.size();

		populateAggregatedMonitoringData(currentSizeDeviceMap);
		return resultAggregatedDeviceList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> listDeviceId) throws Exception {
		return retrieveMultipleStatistics().stream().filter(aggregatedDevice -> listDeviceId.contains(withAggregatorPrefix(aggregatedDevice.getDeviceId()))).collect(Collectors.toList());
	}

	/**
	 * Init instance of QRCCommunicator
	 *
	 * @throws Exception if init fail
	 */
	public void initQRCCommunicator() throws Exception {
		int port;
		try {
			port = Integer.parseInt(this.qrcPort);
			if (port < QSYSCoreConstant.MIN_PORT || port > QSYSCoreConstant.MAX_PORT) {
				throw new IllegalArgumentException("Port must be greater than or equal " + QSYSCoreConstant.MIN_PORT + " and less than or equal " + QSYSCoreConstant.MAX_PORT);
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("QRC Port must be a valid port number", e);
		}
		qrcCommunicator = new QRCCommunicator();
		qrcCommunicator.setHost(this.host);
		qrcCommunicator.setPort(port);
		qrcCommunicator.init();
	}

	/**
	 * Reset socket connection for each polling interval
	 */
	public void resetSocketConnection() {
		if (!isQrcCommunicatorFirstTimeInit && qrcCommunicator != null) {
			qrcCommunicator.destroyChannel();
			qrcCommunicator = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void authenticate() throws Exception {
		// The device has its own authentication behavior, do not use the common one
	}

	/**
	 * {@inheritDoc}
	 * set Bearer Token into Header of Request
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) {
		if (loginInfo.getToken() != null && !uri.contains(QSYSCoreURL.BASE_URI + QSYSCoreURL.TOKEN)) {
			headers.setBearerAuth(loginInfo.getToken());
		}
		return headers;
	}

	// fix when done

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void internalDestroy() {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal destroy is called.");
		}

		mapOfIdAndAggregatedDeviceList.clear();
		deviceIdDequeue = new ArrayDeque<>();
		nextDevicesCollectionIterationTimestamp = 0;
		resultAggregatedDeviceList.clear();
		deviceMap.clear();
		loginInfo = null;
		localPollingInterval = 0;
		if (localExtStats != null) {
			if (localExtStats.getStatistics() != null) {
				localExtStats.getStatistics().clear();
			}
			if (localExtStats.getDynamicStatistics() != null) {
				localExtStats.getDynamicStatistics().clear();
			}
			if (localExtStats.getControllableProperties() != null) {
				localExtStats.getControllableProperties().clear();
			}
		}
		if (executorService != null) {
			executorService.shutdownNow();
			executorService = null;
		}
		if (qrcExecutorService != null) {
			qrcExecutorService.shutdownNow();
			qrcExecutorService = null;
		}
		if (qrcCommunicator != null) {
			qrcCommunicator.destroyChannel();
			qrcCommunicator = null;
		}
		isQrcCommunicatorFirstTimeInit = true;
		devicesExecutionPool.forEach(future -> future.cancel(true));
		devicesExecutionPool.clear();
		filterPluginByNameSet.clear();
		super.internalDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		loginInfo = null;
		localPollingInterval = 0;
		adapterInitializationTimestamp = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}
	}

	/**
	 * Init instance of LoginInfo
	 *
	 * @return LoginInfo
	 */
	protected LoginInfo initLoginInfo() {
		return new LoginInfo();
	}

	/**
	 * Retrieves metadata information and updates the provided statistics and dynamic map.
	 *
	 * @param stats the map where statistics will be stored
	 * @throws Exception if there is an error during the retrieval process
	 */
	private void retrieveMetadata(Map<String, String> stats) throws Exception {
		try {
			stats.put(QSYSCoreConstant.ADAPTER_VERSION,
					getDefaultValueForNullData(adapterProperties.getProperty("aggregator.version")));
			stats.put(QSYSCoreConstant.ADAPTER_BUILD_DATE,
					getDefaultValueForNullData(adapterProperties.getProperty("aggregator.build.date")));

			long adapterUptime = System.currentTimeMillis() - adapterInitializationTimestamp;
			stats.put(QSYSCoreConstant.ADAPTER_UPTIME_MIN, String.valueOf(adapterUptime / (1000 * 60)));
			stats.put(QSYSCoreConstant.ADAPTER_UPTIME, formatUpTime(String.valueOf(adapterUptime / 1000)));
		} catch (Exception e) {
			logger.error("Failed to populate metadata information", e);
		}
	}

	/**
	 * This method is used to populate all monitoring properties
	 *
	 * @param stats list statistic property
	 * @throws ResourceNotReachableException when failedMonitor said all device monitoring data are failed to get
	 */
	private void populateQSYSAggregatorMonitoringData(Map<String, String> stats) throws Exception {
		retrieveQSYSAggregatorInfo(stats);
		retrieveQSYSAggregatorNetworkInfo(stats);
		retrieveQSYSAggregatorRedundancy(stats);
		retrieveQSYSAggregatorDesign(stats);
	}

	/**
	 * Get all information of aggregator
	 *
	 * @param stats Map store all information
	 */
	private void retrieveQSYSAggregatorInfo(Map<String, String> stats) throws Exception {
		try {
			DeviceInfo deviceInfo = objectMapper.readValue(doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.DEVICE_INFO)), DeviceInfo.class);
			if (deviceInfo != null && deviceInfo.getDeviceInfoData() != null) {
				for (QSYSCoreSystemMetric propertiesName : QSYSCoreSystemMetric.values()) {
					if (QSYSCoreSystemMetric.UPTIME.getName().equals(propertiesName.getName())) {
						stats.put(propertiesName.getName(), getDataOrDefaultDataIfNull(convertMillisecondsToDate(deviceInfo.getValueByMetricName(propertiesName))));
						continue;
					} else if (QSYSCoreSystemMetric.DEVICE_NAME.getName().equals(propertiesName.getName())) {
						aggregatorDeviceName = deviceInfo.getValueByMetricName(propertiesName);
						stats.put(propertiesName.getName(), getDataOrDefaultDataIfNull(deviceInfo.getValueByMetricName(propertiesName)));
						continue;
					}
					stats.put(propertiesName.getName(), getDataOrDefaultDataIfNull(deviceInfo.getValueByMetricName(propertiesName)));
				}
			}
		} catch (FailedLoginException e) {
			throw new FailedLoginException("Unable to login. Please check device credentials");
		} catch (Exception e) {
			throw new RuntimeException("Unable to retrieve core information.", e);
//			//Populate default value if request is error
//			for (QSYSCoreSystemMetric propertiesName : QSYSCoreSystemMetric.values()) {
//				stats.put(propertiesName.getName(), QSYSCoreConstant.DEFAUL_DATA);
//			}
//			logger.error("Error when retrieve aggregator information", e);
		}
	}

	/**
	 * Retrieves the redundancy group information
	 * The method fetches data from a remote service, and populates the `stats` map with redundancy metrics.
	 *
	 * @param stats Map store all the relevant redundancy information
	 */
	private void retrieveQSYSAggregatorRedundancy(Map<String, String> stats) {
		try{
			String response = doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.LIST_ITEM_ACTIVE));
			JsonNode root = objectMapper.readTree(response);
			JsonNode dataNode = root.get("data");
			List<RedundancyWrapper> redundancyList = objectMapper.readValue(
					dataNode.toString(),
					new TypeReference<List<RedundancyWrapper>>() {}
			);
			Optional<RedundancyWrapper> optional = redundancyList.stream().findFirst();
			if (optional.isPresent()) {
				RedundancyWrapper processorRedundancy = optional.get();
				for (QSYSCoreRedundancyMetric metric : QSYSCoreRedundancyMetric.values()) {
					stats.put(
							QSYSCoreConstant.REDUNDANCY + QSYSCoreConstant.HASH + metric.getName(),
							getDataOrDefaultDataIfNull(processorRedundancy.getValueByMetricName(metric))
					);
				}
			}
		}catch (Exception e) {
			throw new RuntimeException("Unable to retrieve core redundancy information.", e);
//			logger.error("Error when retrieve aggregator redundancy information", e);
		}
	}

	/**
	 * Get network information of aggregator device
	 *
	 * @param stats Map store all information
	 */
	private void retrieveQSYSAggregatorNetworkInfo(Map<String, String> stats) {
		try {
			DeviceLANInfo deviceLANInfo = objectMapper.readValue(doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.DEVICE_LAN_INFO)), DeviceLANInfo.class);
			if (deviceLANInfo != null && deviceLANInfo.getNetworkInfo() != null) {
				for (QSYSCoreNetworkMetric networkMetric : QSYSCoreNetworkMetric.values()) {
					if (QSYSCoreNetworkMetric.HOSTNAME.getName().equals(networkMetric.getName())) {
						stats.put(networkMetric.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getValueByMetricName(networkMetric, false)));
						continue;
					}
					stats.put(QSYSCoreConstant.LAN_A + QSYSCoreConstant.HASH + networkMetric.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getValueByMetricName(networkMetric, false)));
					stats.put(QSYSCoreConstant.LAN_B + QSYSCoreConstant.HASH + networkMetric.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getValueByMetricName(networkMetric, true)));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to populate core LAN information.", e);
//			populateNetworkDefaultValue(stats);
//			logger.error("Error when retrieve aggregator network information", e);
		}
	}

	/**
	 * Get information of design that is running on aggregator device
	 *
	 * @param stats Map store all information
	 */
	private void retrieveQSYSAggregatorDesign(Map<String, String> stats) {
		try {
			RpcMethod method = RpcMethod.STATUS_GET;
			String request = String.format(RpcMethod.getRequest(), method.getName(), RpcMethod.getParamsString(method));
			List<String> response = Arrays.asList(qrcCommunicator.send(request));

			Optional<String> validResponse = response.stream()
					.filter(res -> res.contains(QSYSCoreConstant.CMD_RESULT) && !res.contains(QSYSCoreConstant.CMD_METHOD))
					.findFirst();

			if (validResponse.isPresent()) {
				DesignInfo designInfo = objectMapper.readValue(validResponse.get(), DesignInfo.class);
				if (designInfo != null && designInfo.getResult() != null) {
					for (QSYSCoreDesignMetric qsysCoreDesignMetric : QSYSCoreDesignMetric.values()) {
						stats.put(qsysCoreDesignMetric.getName(), designInfo.getValueByMetricName(qsysCoreDesignMetric));
					}
				}
			} else {
				response.stream()
						.filter(res -> res.contains(QSYSCoreConstant.CMD_ERROR))
						.findFirst()
						.ifPresent(err -> logger.warn("STATUS_GET request resulted in an error for aggregator " + aggregatorDeviceName + ": " + err));
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to retrieve core design metrics.", e);
//			for (QSYSCoreDesignMetric qsysCoreDesignMetric : QSYSCoreDesignMetric.values()) {
//				stats.put(qsysCoreDesignMetric.getName(), QSYSCoreConstant.DEFAUL_DATA);
//			}
//			logger.error("Error when retrieve aggregator design", e);
		}
	}

//	/**
//	 * Populate default is none for network properties
//	 *
//	 * @param stats list of Statistics
//	 */
//	private void populateNetworkDefaultValue(Map<String, String> stats) {
//		for (QSYSCoreNetworkMetric qsysCoreNetworkMetric : QSYSCoreNetworkMetric.values()) {
//			if (QSYSCoreNetworkMetric.HOSTNAME.getName().equals(qsysCoreNetworkMetric.getName())) {
//				stats.put(qsysCoreNetworkMetric.getName(), QSYSCoreConstant.DEFAUL_DATA);
//				continue;
//			}
//			stats.put(QSYSCoreConstant.LAN_A + QSYSCoreConstant.HASH + qsysCoreNetworkMetric.getName(), QSYSCoreConstant.DEFAUL_DATA);
//			stats.put(QSYSCoreConstant.LAN_B + QSYSCoreConstant.HASH + qsysCoreNetworkMetric.getName(), QSYSCoreConstant.DEFAUL_DATA);
//		}
//	}

	/**
	 * Get list of component
	 *
	 * @param stats statistic of aggregator
	 * @param controllableProperties controllable list of aggregator
	 */
	private void populateQSYSComponent(Map<String, String> stats, List<AdvancedControllableProperty> controllableProperties) {
		try {
			RpcMethod method = RpcMethod.GET_COMPONENTS;
			String request = String.format(RpcMethod.getRequest(), method.getName(), RpcMethod.getParamsString(method));
			List<String> response = Arrays.asList(qrcCommunicator.send(request));

			Optional<String> validResponse = response.stream()
					.filter(res -> res.contains(QSYSCoreConstant.CMD_RESULT) && !res.contains(QSYSCoreConstant.CMD_METHOD))
					.findFirst();

			if (validResponse.isPresent() ) {
				ComponentWrapper componentWrapper = objectMapper.readValue(response.get(0), ComponentWrapper.class);
				if (componentWrapper.getResult() != null) {

					//Because some device could be removed, so we need save all existed device
					Set<String> existDeviceSet = new HashSet<>();
					deviceMap.clear();
					for (ComponentInfo componentInfo : componentWrapper.getResult()) {
						if (QSYSCoreConstant.GAIN_TYPE.equals(componentInfo.getType())) {
							retrieveGainComponent(stats, controllableProperties, componentInfo.getId());
							continue;
						}
						if (componentInfo.getType() != null && QSYSCoreConstant.SUPPORTED_DEVICE_TYPE.contains(componentInfo.getType()) || componentInfo.getType()
								.contains(QSYSCoreConstant.PLUGIN)) {
							retrieveDevice(existDeviceSet, componentInfo);
						} else {
							existDeviceSet.add(componentInfo.getId());
						}
					}
					//Remove device does not exist
					deviceMap.keySet().retainAll(existDeviceSet);
					mapOfIdAndAggregatedDeviceList.keySet().retainAll(deviceMap.keySet());
				}
			} else {
				response.stream()
						.filter(res -> res.contains(QSYSCoreConstant.CMD_ERROR))
						.findFirst()
						.ifPresent(err -> logger.warn("Have error response: " + err));
			}
		} catch (Exception e) {
			deviceMap.clear();
			logger.error("Unable to retrieve QSYS Component.", e);
		}
	}

	/**
	 * Synchronizes the cache with the current discovery results: adds missing IDs from {@code deviceMap}
	 * (reusing existing instances) and removes IDs that no longer exist in {@code deviceMap}.
	 * Call after updating {@code deviceMap} and before rendering statistics.
	 * Mutates shared state
	 */
	private void reconcileCacheWithDeviceMap(Map<String, String> stats) {
		for (String id : deviceMap.keySet()) {
			QSYSPeripheralDevice dev = deviceMap.get(id);
			if (dev != null) {
				mapOfIdAndAggregatedDeviceList.putIfAbsent(id, dev);
			}
		}
		if("Standby".equals(stats.get("State"))){
			mapOfIdAndAggregatedDeviceList.keySet().retainAll(deviceMap.keySet());
		}
	}

	/**
	 * Refills the device-processing queue using round-robin scheduling.
	 *
	 * @param batchSize maximum number of device IDs to enqueue.
	 */
	private void refillQueueRoundRobin(int batchSize) {
		if (!deviceIdDequeue.isEmpty() || deviceMap.isEmpty()) return;

		if (rrSnapshot.size() != deviceMap.size() || !rrSnapshot.containsAll(deviceMap.keySet())) {
			rrSnapshot = new ArrayList<>(deviceMap.keySet());
			rrIndex = 0;
		}

		int added = 0;
		while (added < batchSize && !rrSnapshot.isEmpty()) {
			String id = rrSnapshot.get(rrIndex);
			rrIndex = (rrIndex + 1) % rrSnapshot.size();
			deviceIdDequeue.addLast(id);
			added++;
		}
	}

	/**
	 * Retrieve information of device
	 *
	 * @param existDeviceSet set store all exist device
	 * @param componentInfo component info of device
	 */
	private void retrieveDevice(Set<String> existDeviceSet, ComponentInfo componentInfo) {
		if (!StringUtils.isNullOrEmpty(filterDeviceByName) && !filterDeviceByNameSet.contains(componentInfo.getName())
				|| !StringUtils.isNullOrEmpty(filterDeviceByQSYSType) && !filterDeviceByQSYSTypeSet.contains(componentInfo.getType())) {
			return;
		}
		existDeviceSet.add(componentInfo.getId());
		QSYSPeripheralDevice device;
		if (componentInfo.getType().contains(QSYSCoreConstant.PLUGIN)) {
			device = populateDeviceHasTypeIsPlugin(componentInfo);
			if (device != null) {
				device.setType(QSYSCoreConstant.EXTERNAL);
			}
		} else {
			device = createDeviceByType(componentInfo.getType());
			device.setType(componentInfo.getType());
		}
		if (device != null && !deviceMap.containsKey(componentInfo.getId())) {
			deviceMap.put(componentInfo.getId(), device);
		}
	}

	/**
	 * populate device with type is Plugin
	 *
	 * @param componentInfo component info of device
	 * @return QSYSPeripheralDevice instances
	 */
	private QSYSPeripheralDevice populateDeviceHasTypeIsPlugin(ComponentInfo componentInfo) {
		QSYSPeripheralDevice device = null;
		if (componentInfo.getId() == null) {
			return device;
		}

		String id = componentInfo.getId().toLowerCase(Locale.ROOT);
		if (!filterPluginByNameSet.isEmpty() && isPluginNameExits(id)) {
			return new PluginDevice();
		}
		return device;
	}

	/**
	 * Checks if the provided plugin ID contains any of the names in the filter set.
	 *
	 * @param id the id of the plugin to check
	 * @return if the Iid contains any of the names in the filter set;
	 * {false} otherwise
	 */
	private boolean isPluginNameExits(String id) {
		return filterPluginByNameSet.contains(id);
	}

	/**
	 * Create a peripheral device by type
	 *
	 * @param type device type need to create
	 * @return peripheral device
	 */
	private QSYSPeripheralDevice createDeviceByType(String type) {
		switch (type) {
			case QSYSCoreConstant.CAMERA_DEVICE:
				return new CameraDevice();
			case QSYSCoreConstant.STREAM_INPUT_DEVICE:
				return new StreamInputDevice();
			case QSYSCoreConstant.STREAM_OUTPUT_DEVICE:
				return new StreamOutputDevice();
			case QSYSCoreConstant.PROCESSOR_DEVICE:
				return new ProcessorDevice();
			case QSYSCoreConstant.VIDEO_IO_DEVICE:
				return new VideoIODevice();
			case QSYSCoreConstant.CONTROL_INTERFACE_DEVICE:
				return new ControlInterfaceDevice();
			case QSYSCoreConstant.DISPLAY_DEVICE:
				return new DisplayDevice();
			case QSYSCoreConstant.VIDEO_SOURCE_DEVICE:
				return new VideoSourceDevice();
			case QSYSCoreConstant.MONITORING_PROXY:
				return new MonitoringProxyDevice();
			case QSYSCoreConstant.TRANSMITTER_DEVICE:
				return new TransmitterDevice();
			case QSYSCoreConstant.AMPLIFIER_DEVICE:
				return new AmplifierDevice();
            case QSYSCoreConstant.STATUS_NV_ENCODER_DECODER:
                return new EncoderDecoderDevice();
			case QSYSCoreConstant.RECEIVER_DEVICE:
			    return new ReceiverDevice();
			case QSYSCoreConstant.LOUDSPEAKER_DEVICE:
				return new LoudSpeakerDevice();
			case QSYSCoreConstant.STATUS_AMP_DEVICE:
				return new Amplifier_CXQ_StatusDevice();
			default:
				this.logger.error("Type " + type + " does not exist");
				return null;
		}
	}

	/**
	 * Retrieve gain component
	 *
	 * @param stats stats of aggregator
	 * @param controllableProperties control list of aggregator
	 * @param deviceId id of gain component
	 */
	private void retrieveGainComponent(Map<String, String> stats, List<AdvancedControllableProperty> controllableProperties, String deviceId) {
		if (StringUtils.isNullOrEmpty(deviceId) || !filterGainComponentByNameSet.contains(deviceId)) {
			return;
		}
		try {
			String request = String.format(RpcMethod.getRequest(), RpcMethod.GET_CONTROLS.getName(), RpcMethod.getParamsString(RpcMethod.GET_CONTROLS));
			request = String.format(request, deviceId);
			List<String> response = Arrays.asList(qrcCommunicator.send(request));
			if (response.size() > 1) {
				JsonNode deviceControlInfo = objectMapper.readValue(response.get(1), JsonNode.class);

				if (deviceControlInfo != null && deviceControlInfo.get(QSYSCoreConstant.RESULT) != null) {
					String groupName = QSYSCoreConstant.GAIN + QSYSCoreConstant.COLON + deviceId;
					JsonNode deviceControls = deviceControlInfo.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS);
					for (JsonNode control : deviceControls) {
						GainControllingMetric gainControllingMetric = GainControllingMetric.getByName(control.get(QSYSCoreConstant.CONTROL_NAME).asText());
						if (gainControllingMetric == null) {
							continue;
						}
						String propertyName = groupName + QSYSCoreConstant.HASH + gainControllingMetric.getMetric();
						switch (gainControllingMetric) {
							case GAIN_VALUE_CONTROL:
								Float value = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE).asText());
								if (value != null) {
									value = Math.round(value * 100) / 100.0f;
									stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.CURRENT_GAIN_VALUE.getMetric(), String.valueOf(value));

									Float firstValue = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE_MIN).asText());
									Float secondValue = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE_MAX).asText());

									if (firstValue != null && secondValue != null) {
										Float minValue = Math.min(firstValue, secondValue);
										Float maxValue = Math.max(firstValue, secondValue);

										minValue = Math.round(minValue * 100) / 100.0f;
										maxValue = Math.round(maxValue * 100) / 100.0f;

										if (minValue != null && maxValue != null && !minValue.equals(maxValue)) {
											stats.put(propertyName, String.valueOf(value));
											controllableProperties.add(ControllablePropertyFactory.createSlider(propertyName, minValue, maxValue, value));
										}
									}
								}
								break;
							case BYPASS_CONTROL:
							case MUTE_CONTROL:
							case INVERT_CONTROL:
								stats.put(propertyName, QSYSCoreConstant.EMPTY);
								controllableProperties.add(ControllablePropertyFactory.createSwitch(propertyName, QSYSCoreConstant.FALSE.equals(control.get(QSYSCoreConstant.CONTROL_VALUE).asText()) ? 0 : 1));
								break;
							default:
								logger.debug("The property name doesn't support:" + gainControllingMetric.getMetric());
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when retrieve " + deviceId + " gain component", e);
		}
	}

	/**
	 * Update Gain Control by metric name bypass,invert, mute, GainControl,gain
	 *
	 * @param metricName The metricName is name of the metric
	 * @param value the value is value of control
	 * @param property the property i property name
	 */
	private void updateGainControlByMetricName(String metricName, String value, String property) {
		Map<String, String> stats = localExtStats.getStatistics();
		List<AdvancedControllableProperty> advancedControllableProperties = localExtStats.getControllableProperties();

		GainControllingMetric gainControllingMetric = GainControllingMetric.getByMetric(metricName);
		switch (gainControllingMetric) {
			case GAIN_VALUE_CONTROL:
				stats.put(property.split(QSYSCoreConstant.HASH)[0] + QSYSCoreConstant.HASH + GainControllingMetric.CURRENT_GAIN_VALUE.getMetric(), value);
				break;
			case BYPASS_CONTROL:
			case MUTE_CONTROL:
			case INVERT_CONTROL:
				break;
			default:
				logger.debug("The property name doesn't support:" + gainControllingMetric.getMetric());
		}
		updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
	}


	/**
	 * Update the value for the control metric
	 *
	 * @param property is name of the metric
	 * @param value the value is value of properties
	 * @param extendedStatistics list statistics property
	 * @param advancedControllableProperties the advancedControllableProperties is list AdvancedControllableProperties
	 */
	private void updateValueForTheControllableProperty(String property, String value, Map<String, String> extendedStatistics, List<AdvancedControllableProperty> advancedControllableProperties) {
		if (!advancedControllableProperties.isEmpty()) {
			for (AdvancedControllableProperty advancedControllableProperty : advancedControllableProperties) {
				if (advancedControllableProperty.getName().equals(property)) {
					extendedStatistics.put(property, value);
					advancedControllableProperty.setValue(value);
					break;
				}
			}
		}
	}

	/**
	 * Handles the control of an aggregated device by sending a request to set a specific control metric.
	 * The communication with the device is carried out using the RPC protocol, where:
	 * - The request is sent using the `SET_CONTROLS` RPC method.
	 * - The parameters for the request are formatted as: `property`, `metricName`, and `value` (the value is wrapped in quotes if not "1" or "0").
	 *
	 * @param metricName The name of the metric to be controlled.
	 * @param property   The property associated with the metric.
	 * @param value      The value to be set for the control. If the value is "1" or "0", it is sent as-is; otherwise, it is wrapped in double quotes.
	 */
	private void handleControlAggregated(String metricName, String property, String value) {
		RpcMethod method = RpcMethod.SET_CONTROLS;
		String request = String.format(RpcMethod.getRequest(), method.getName(), RpcMethod.getParamsString(method));
		request = String.format(request, property, metricName,
				("1".equals(value) || "0".equals(value)) ? value : "\"" + value + "\"");

		try {
			List<String> response = Arrays.asList(qrcCommunicator.send(request));
			if (response.size() > 1) {
				JsonNode responseControl = objectMapper.readValue(response.get(1), JsonNode.class);

				if (!responseControl.has(QSYSCoreConstant.RESULT) || !responseControl.get(QSYSCoreConstant.RESULT).asText().equals(QSYSCoreConstant.TRUE)) {
					throw new IllegalStateException("Error: cannot set value of component " + metricName);
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when control " + metricName + " component", e);
		}
	}

	/**
	 * Retrieves the property associated with a given metric name for a specific device model.
	 *
	 * @param property  The name of the metric to look up.
	 * @param deviceModel The model of the device to determine the appropriate metric class.
	 * @return The corresponding property of the metric if found, otherwise null.
	 */
	private String getMetricProperty(String property, String deviceModel, String metricValue) {
		Class<? extends DeviceMetric> metricClass = getDeviceMetricClass(deviceModel);
		if (metricClass == null) {
			return null;
		}
		for (DeviceMetric metric : metricClass.getEnumConstants()) {
			String metricPattern = metric.getMetric();
			if (metricPattern.contains(QSYSCoreConstant.FORMAT_STRING)
					&& property.contains(QSYSCoreConstant.HASH)
					&& property.startsWith(QSYSCoreConstant.CHANNEL) && metricPattern.contains(metricValue)) {
				return metric.getProperty();
			}
			if (metricPattern.equalsIgnoreCase(property)) {
				return metric.getProperty();
			}
		}
		return null;
	}

	/**
	 * Retrieves the metric based on the given property, device model, and metric value.
	 *
	 * @param property The property to be matched with the metric pattern.
	 * @param deviceModel The model of the device, used to retrieve the corresponding metric class.
	 * @param metricValue The metric value to check if the property and metric pattern match.
	 */
	private String getMetric(String property, String deviceModel, String metricValue) {
		Class<? extends DeviceMetric> metricClass = getDeviceMetricClass(deviceModel);
		if (metricClass == null) {
			return null;
		}
		for (DeviceMetric metric : metricClass.getEnumConstants()) {
			String metricPattern = metric.getMetric();
			if (metricPattern.contains(QSYSCoreConstant.FORMAT_STRING)
					&& property.contains(QSYSCoreConstant.HASH)
					&& property.startsWith(QSYSCoreConstant.CHANNEL) && metricPattern.contains(metricValue)) {
				return metric.getMetric();
			}
			if (metricPattern.equalsIgnoreCase(property)) {
				return metric.getMetric();
			}
		}
		return null;
	}


	/**
	 * Retrieves the corresponding DeviceMetric class based on the given device type.
	 *
	 * @param deviceType The type of the device.
	 * @return The corresponding DeviceMetric class, or null if the device type is unknown.
	 */
	private Class<? extends DeviceMetric> getDeviceMetricClass(String deviceType) {
		if (QSYSCoreConstant.TRANSMITTER_DEVICE.equals(deviceType)) {
			return TransmitterDeviceMetric.class;
		} else if (QSYSCoreConstant.RECEIVER_DEVICE.equals(deviceType)) {
			return ReceiverDeviceMetric.class;
		} else if (QSYSCoreConstant.LOUDSPEAKER_DEVICE.equals(deviceType)) {
			return LoudSpeakerDeviceMetric.class;
		} else if (QSYSCoreConstant.AMPLIFIER_DEVICE.equals(deviceType)) {
			return AmplifierDeviceMetric.class;
		}
		return null;
	}

	/**
	 * Convert String to Float or null if It can not convert
	 * Handle null pointer exception when use this method
	 *
	 * @param value String value need to convert to float
	 */
	private Float tryParseFloatOrNull(String value) {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			// Handle null pointer exception when use this method
			return null;
		}
	}

	/**
	 * check value is null or empty
	 *
	 * @param value input value
	 * @return value after checking
	 */
	private String getDefaultValueForNullData(String value) {
		return StringUtils.isNotNullOrEmpty(value) && !QSYSCoreConstant.NULL.equalsIgnoreCase(value) ? uppercaseFirstCharacter(value) : QSYSCoreConstant.NONE_VALUE;
	}

	/**
	 * Adds the aggregator device name as a prefix to the given device ID.
	 *
	 * @param deviceId the original device identifier (without prefix)
	 * @return the device ID with the aggregator prefix applied
	 */
	private String withAggregatorPrefix(String deviceId) {
		return aggregatorDeviceName + "_" + deviceId;
	}

	/**
	 * Removes the aggregator device name prefix from the given device ID if present.
	 *
	 * @param prefixedId the device ID with aggregator prefix
	 * @return the original device ID without the aggregator prefix, or the input value if no prefix is found
	 */
	private String removeAggregatorPrefix(String prefixedId) {
		if (prefixedId != null && prefixedId.startsWith(aggregatorDeviceName + "_")) {
			return prefixedId.substring((aggregatorDeviceName + "_").length());
		}
		return prefixedId;
	}

	/**
	 * Formats uptime from a string representation "hh:mm:ss" into "X hour(s) Y minute(s)" format.
	 *
	 * @param time the uptime string to format
	 * @return formatted uptime string or "None" if input is invalid
	 */
	private String formatUpTime(String time) {
		int seconds = Integer.parseInt(time);
		if (seconds < 0) {
			return QSYSCoreConstant.NONE_VALUE;
		}

		int days = seconds / (24 * 3600);
		seconds %= 24 * 3600;
		int hours = seconds / 3600;
		seconds %= 3600;
		int minutes = seconds / 60;
		seconds %= 60;

		StringBuilder result = new StringBuilder();
		if (days > 0) {
			result.append(days).append(" day(s) ");
		}
		if (hours > 0) {
			result.append(hours).append(" hour(s) ");
		}
		if (minutes > 0) {
			result.append(minutes).append(" minute(s) ");
		}
		if (seconds > 0) {
			result.append(seconds).append(" second(s) ");
		}

		if (result.length() == 0) {
			return "0 second(s)";
		}
		return result.toString().trim();
	}

	/**
	 * capitalize the first character of the string
	 *
	 * @param input input string
	 * @return string after fix
	 */
	public String uppercaseFirstCharacter(String input) {
		char firstChar = input.charAt(0);
		return Character.toUpperCase(firstChar) + input.substring(1);
	}

	/**
	 * Get a token to log in the device
	 */
	private void retrieveTokenFromCore() throws Exception {
		String login = getLogin();
		String password = getPassword();

		ObjectNode request = JsonNodeFactory.instance.objectNode();
		request.put(QSYSCoreConstant.USERNAME, login);
		request.put(QSYSCoreConstant.PASSWORD, password);
		try {
			if (this.loginInfo.isTimeout() || this.loginInfo.getToken() == null) {
				JsonNode responseData = doPost(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.TOKEN), request, JsonNode.class);
				if (responseData != null) {
					String token = responseData.get(QSYSCoreConstant.TOKEN).asText();
					if (token != null) {
						this.loginInfo.setToken(token);
						this.loginInfo.setLoginDateTime(System.currentTimeMillis());
					} else {
						this.loginInfo.setToken(null);
					}
				} else {
					this.loginInfo.setToken(null);
				}
			}
		} catch (CommandFailureException e) {
			if (!StringUtils.isNullOrEmpty(getPassword()) && !StringUtils.isNullOrEmpty(getLogin())) {
				throw new FailedLoginException("Unable to login. Please check device credentials");
			}
			this.loginInfo.setToken(QSYSCoreConstant.AUTHORIZED);
			this.loginInfo.setLoginDateTime(System.currentTimeMillis());
		} catch (Exception e) {
			throw new ResourceNotReachableException("Unable to retrieve the authorization token, endpoint not reachable", e);
		}
	}

	/**
	 * Refresh aggregator-level timestamps to keep the data loader active.
	 */
	private void refreshTimestamps() {
		nextDevicesCollectionIterationTimestamp = System.currentTimeMillis();
		updateValidRetrieveStatisticsTimestamp();
	}

	/**
	 * Take a thread-safe snapshot of the device map.
	 *
	 * @return a shallow copy of the current device map
	 */
	private Map<String, QSYSPeripheralDevice> snapshotDevices() {
		synchronized (mapOfIdAndAggregatedDeviceList) {
			return new HashMap<>(mapOfIdAndAggregatedDeviceList);
		}
	}

	/**
	 * Build an AggregatedDevice object from a QSYSPeripheralDevice.
	 *
	 * @param id  device identifier
	 * @param device peripheral device with raw stats
	 * @return AggregatedDevice or null if filtered out
	 */
	private AggregatedDevice buildAggregatedDevice(String id, QSYSPeripheralDevice device) {
		if (isFilteredOut(id, device)) {
			return null;
		}

		AggregatedDevice aggregatedDevice = new AggregatedDevice();
		aggregatedDevice.setDeviceId(withAggregatorPrefix(id));

		Map<String, String> props = device.getStats();
		setDeviceStatus(props, aggregatedDevice);
		aggregatedDevice.setProperties(props);

		String displayName = applyTypeSpecificProperties(id, device, props, aggregatedDevice);
		aggregatedDevice.setDeviceName(withAggregatorPrefix(displayName));

		provisionTypedStatistics(aggregatedDevice.getProperties(), aggregatedDevice);
		aggregatedDevice.setControllableProperties(device.getAdvancedControllableProperties());
		aggregatedDevice.setTimestamp(System.currentTimeMillis());
		return aggregatedDevice;
	}

	/**
	 * Check if a device should be excluded based on type or name filters.
	 *
	 * @param id  device identifier
	 * @param dev peripheral device
	 * @return true if the device should be skipped
	 */
	private boolean isFilteredOut(String id, QSYSPeripheralDevice dev) {
		if (!StringUtils.isNullOrEmpty(filterDeviceByQSYSType)
				&& !filterDeviceByQSYSTypeSet.contains(dev.getType())) {
			return true;
		}
		return !StringUtils.isNullOrEmpty(filterDeviceByName)
				&& !filterDeviceByNameSet.contains(id);
	}

	/**
	 * Set online/offline status on an aggregated device based on its STATUS property.
	 *
	 * @param props device properties map
	 * @param agg   aggregated device
	 */
	private void setDeviceStatus(Map<String, String> props, AggregatedDevice agg) {
		String status = props.get(QSYSCoreConstant.STATUS);
		if (status == null || QSYSCoreConstant.NULL.equalsIgnoreCase(status)) {
			agg.setDeviceOnline(false);
			return;
		}
		boolean online = QSYSCoreConstant.LIST_ONLINE_STATUS.stream()
				.anyMatch(s -> s.equalsIgnoreCase(status.trim()));
		agg.setDeviceOnline(online);
	}

	/**
	 * Apply type-specific properties and adjust display name if needed.
	 *
	 * @param id    device identifier
	 * @param dev   peripheral device
	 * @param props device properties
	 * @param agg   aggregated device
	 * @return the display name for this device
	 */
	private String applyTypeSpecificProperties(String id, QSYSPeripheralDevice dev, Map<String, String> props, AggregatedDevice agg) {
		String name = id;

		if (QSYSCoreConstant.EXTERNAL.equals(dev.getType())) {
			String deviceName = props.get(PluginDeviceMetric.DEVICE_NAME.getMetric());
			if (StringUtils.isNotNullOrEmpty(deviceName)
					&& !QSYSCoreConstant.DEFAUL_DATA.equalsIgnoreCase(deviceName)) {
				name = deviceName;
			}
			agg.getProperties().put(QSYSCoreConstant.QSYS_TYPE, QSYSCoreConstant.EXTERNAL);
			props.remove(PluginDeviceMetric.DEVICE_NAME.getMetric());
		} else {
			agg.getProperties().put(QSYSCoreConstant.QSYS_TYPE,
					getTypeByResponseType(dev.getType()));
		}
		return name;
	}

	/**
	 * Build Url to call API
	 *
	 * @param path path of Url
	 * @return Url
	 */
	private String buildDeviceFullPath(String path) {
		Objects.requireNonNull(path);
		String protocol = StringUtils.isNullOrEmpty(this.getProtocol()) ? "https" : this.getProtocol();
		return protocol + "://" + getHost() + path;
	}

	/**
	 * Return data or default data if data is null or empty
	 *
	 * @param data data to check null or empty
	 * @return data or default data
	 */
	private String getDataOrDefaultDataIfNull(String data) {
		return StringUtils.isNotNullOrEmpty(data) ? data : QSYSCoreConstant.DEFAUL_DATA;
	}

	/**
	 * Get all information of device list in a thread
	 *
	 * @param deviceIdsNeedToUpdate device id list need to get information
	 */
	private void retrieveAggregatedDeviceByIdList(List<String> deviceIdsNeedToUpdate) {
		for (String deviceId : deviceIdsNeedToUpdate) {
			try {
				String request = String.format(RpcMethod.getRequest(), RpcMethod.GET_CONTROLS.getName(), RpcMethod.getParamsString(RpcMethod.GET_CONTROLS));
				request = String.format(request, deviceId);
				List<String> response = Arrays.asList(qrcCommunicator.send(request));
				Optional<String> validResponse = response.stream()
						.filter(res -> res.contains(QSYSCoreConstant.CMD_RESULT) && !res.contains(QSYSCoreConstant.CMD_METHOD))
						.findFirst();

				if (validResponse.isPresent()) {
					JsonNode deviceControlResponse = objectMapper.readValue(validResponse.get(), JsonNode.class);
					deviceMap.get(deviceId).monitoringDevice(deviceControlResponse);
					mapOfIdAndAggregatedDeviceList.put(deviceId, deviceMap.get(deviceId));
					errorDeviceMap.remove(deviceId);
				} else {
					response.stream()
							.filter(res -> res.contains(QSYSCoreConstant.CMD_ERROR))
							.findFirst()
							.ifPresent(err -> logger.warn("Error retrieving controls for device " + deviceId + ": " + err));
				}
			} catch (Exception e) {
				logger.error("Can not retrieve information of aggregated device have id is " + deviceId, e);
			}
		}
	}

	/**
	 * Control the gain component
	 *
	 * @param metricName metricName of gain
	 * @param namedComponent namedComponent of gain component
	 * @param value value to change of gain component
	 */
	private void gainControl(String metricName, String namedComponent, String value) {
		RpcMethod method = RpcMethod.SET_CONTROLS;
		String request = String.format(RpcMethod.getRequest(), method.getName(), RpcMethod.getParamsString(method));
		request = String.format(request, namedComponent, GainControllingMetric.getByMetric(metricName).getProperty(), value);
		try {
			List<String> response = Arrays.asList(qrcCommunicator.send(request));
			if (response.size() > 1) {
				JsonNode responseControl = objectMapper.readValue(response.get(1), JsonNode.class);

				if (!responseControl.has(QSYSCoreConstant.RESULT) || !responseControl.get(QSYSCoreConstant.RESULT).asText().equals(QSYSCoreConstant.TRUE)) {
					throw new IllegalStateException("Error: cannot set gain value of component " + namedComponent);
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when control " + namedComponent + " component", e);
		}
	}


	/**
	 * Convert milliseconds to date
	 *
	 * @param value time by millisecond
	 * @return time by date time
	 */
	private String convertMillisecondsToDate(String value) {
		if (QSYSCoreConstant.DEFAUL_DATA.equals(value)) {
			return value;
		}
		try {
			long milliseconds = Long.parseLong(value);
			Date date = new Date(milliseconds);
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			return dateFormat.format(date);
		} catch (Exception e) {
			logger.debug("Error when convert milliseconds to datetime", e);
		}
		return QSYSCoreConstant.DEFAUL_DATA;
	}

	/**
	 * Update filterDeviceTypeSet
	 */
	private void updateFilterDeviceTypeSet() {
		Set<String> stringSet = convertUserInput(filterDeviceByQSYSType);
		filterDeviceByQSYSTypeSet = new HashSet<>();
		for (String type : stringSet) {
			switch (type) {
				case QSYSCoreConstant.CAMERA_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.CAMERA_DEVICE);
					break;
				case QSYSCoreConstant.PROCESSOR_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.PROCESSOR_DEVICE);
					break;
				case QSYSCoreConstant.DISPLAY_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.DISPLAY_DEVICE);
					break;
				case QSYSCoreConstant.STREAM_IO_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.STREAM_INPUT_DEVICE);
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.STREAM_OUTPUT_DEVICE);
					break;
				case QSYSCoreConstant.VIDEO_IO_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.VIDEO_IO_DEVICE);
					break;
				case QSYSCoreConstant.VIDEO_SOURCE_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.VIDEO_SOURCE_DEVICE);
					break;
				case QSYSCoreConstant.CONTROL_INTERFACE_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.CONTROL_INTERFACE_DEVICE);
					break;
				case QSYSCoreConstant.TRANSMITTER_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.TRANSMITTER_DEVICE);
					break;
				case QSYSCoreConstant.AMPLIFIER_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.AMPLIFIER_DEVICE);
					break;
				case QSYSCoreConstant.STATUS_AMPLIFIER_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.STATUS_AMP_DEVICE);
					break;
				case QSYSCoreConstant.RECEIVER_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.RECEIVER_DEVICE);
					break;
				case QSYSCoreConstant.LOUDSPEAKER_TYPE:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.LOUDSPEAKER_DEVICE);
					break;
				case QSYSCoreConstant.EXTERNAL:
					filterDeviceByQSYSTypeSet.add(QSYSCoreConstant.MONITORING_PROXY);
					break;
				default:
					logger.error("Type " + type + " does not exist");
			}
		}
	}

	/**
	 * get type of device by response type
	 *
	 * @param responseType response type of device
	 * @return type of device
	 */
	private String getTypeByResponseType(String responseType) {
		switch (responseType) {
			case QSYSCoreConstant.CAMERA_DEVICE:
				return QSYSCoreConstant.CAMERA_TYPE;
			case QSYSCoreConstant.STREAM_INPUT_DEVICE:
			case QSYSCoreConstant.STREAM_OUTPUT_DEVICE:
				return QSYSCoreConstant.STREAM_IO_TYPE;
			case QSYSCoreConstant.CONTROL_INTERFACE_DEVICE:
				return QSYSCoreConstant.CONTROL_INTERFACE_TYPE;
			case QSYSCoreConstant.VIDEO_IO_DEVICE:
				return QSYSCoreConstant.VIDEO_IO_TYPE;
            case QSYSCoreConstant.STATUS_NV_ENCODER_DECODER:
                return QSYSCoreConstant.ENCODER_DECODER_TYPE;
			case QSYSCoreConstant.VIDEO_SOURCE_DEVICE:
				return QSYSCoreConstant.VIDEO_SOURCE_TYPE;
			case QSYSCoreConstant.MONITORING_PROXY:
			case QSYSCoreConstant.PLUGIN:
				return QSYSCoreConstant.EXTERNAL;
			case QSYSCoreConstant.DISPLAY_DEVICE:
				return QSYSCoreConstant.DISPLAY_TYPE;
			case QSYSCoreConstant.PROCESSOR_DEVICE:
				return QSYSCoreConstant.PROCESSOR_TYPE;
			case QSYSCoreConstant.TRANSMITTER_DEVICE:
				return  QSYSCoreConstant.TRANSMITTER_TYPE;
			case QSYSCoreConstant.AMPLIFIER_DEVICE:
				return QSYSCoreConstant.AMPLIFIER_TYPE;
			case QSYSCoreConstant.RECEIVER_DEVICE:
				return QSYSCoreConstant.RECEIVER_TYPE;
			case QSYSCoreConstant.LOUDSPEAKER_DEVICE:
				return QSYSCoreConstant.LOUDSPEAKER_TYPE;
			case QSYSCoreConstant.STATUS_AMP_DEVICE:
				return QSYSCoreConstant.STATUS_AMPLIFIER_TYPE;
			default:
				return QSYSCoreConstant.DEFAUL_DATA;
		}
	}

	/**
	 * This method is used to handle input from adapter properties and convert it to Set of String for control
	 *
	 * @param input input need to convert
	 * @return Set<String> is the Set of String of filter element
	 */
	private Set<String> convertUserInput(String input) {
		try {
			if (!StringUtils.isNullOrEmpty(input)) {
				String[] listAdapterPropertyElement = input.split(QSYSCoreConstant.COMMA);

				// Remove start and end spaces of each adapterProperty
				Set<String> setAdapterPropertiesElement = new HashSet<>();
				for (String adapterPropertyElement : listAdapterPropertyElement) {
					setAdapterPropertiesElement.add(removeAggregatorPrefix(adapterPropertyElement.trim()));
				}
				return setAdapterPropertiesElement;
			}
		} catch (Exception e) {
			logger.error(String.format("Invalid adapter properties input: %s", e.getMessage()), e);
		}
		return Collections.emptySet();
	}

	/**
	 * calculating local polling interval
	 *
	 * @throws IllegalArgumentException when get limit rate exceed error
	 */
	private int calculatingLocalPollingInterval() {
		try {
			int pollingIntervalValue = QSYSCoreConstant.MIN_POLLING_INTERVAL;
			if (StringUtils.isNotNullOrEmpty(pollingInterval)) {
				pollingIntervalValue = Integer.parseInt(pollingInterval);
			}

			int minPollingInterval = calculatingMinPollingInterval();
			if (pollingIntervalValue < minPollingInterval) {
				logger.error(String.format("invalid pollingInterval value, pollingInterval must greater than: %s", minPollingInterval));
				return minPollingInterval;
			}
			return pollingIntervalValue;
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Unexpected pollingInterval value: %s", pollingInterval), e);
		}
	}

	/**
	 * calculating minimum of polling interval
	 *
	 * @return Number of polling interval
	 */
	private int calculatingMinPollingInterval() {
		if (!deviceMap.isEmpty()) {
			return IntMath.divide(deviceMap.size(), QSYSCoreConstant.MAX_THREAD_QUANTITY * QSYSCoreConstant.MAX_DEVICE_QUANTITY_PER_THREAD, RoundingMode.CEILING);
		}
		return QSYSCoreConstant.MIN_POLLING_INTERVAL;
	}

	/**
	 * calculating thread quantity
	 */
	private int calculatingThreadQuantity() {
		if (deviceMap.isEmpty()) {
			return QSYSCoreConstant.MIN_THREAD_QUANTITY;
		}
		if (deviceMap.size() / localPollingInterval < QSYSCoreConstant.MAX_THREAD_QUANTITY * QSYSCoreConstant.MAX_DEVICE_QUANTITY_PER_THREAD) {
			return IntMath.divide(deviceMap.size(), localPollingInterval * QSYSCoreConstant.MAX_DEVICE_QUANTITY_PER_THREAD, RoundingMode.CEILING);
		}
		return QSYSCoreConstant.MAX_THREAD_QUANTITY;
	}

	/**
	 * Add a property as a regular statistics property, or as dynamic one, based on the {@link #historicalProperties} configuration
	 * and DynamicStatisticsDefinitions static definitions.
	 *
	 * @param statistics map of all device properties
	 * @param aggregatedDevice device statistics object
	 */
	private void provisionTypedStatistics(Map<String, String> statistics, AggregatedDevice aggregatedDevice) {
		Map<String, String> dynamicStatistics = new HashMap<>();
		Map<String, String> staticStatistics = new HashMap<>();
		statistics.forEach((propertyName, propertyValue) -> {
			// To ignore the group properties are in, we need to split it
			// whenever there's a hash involved and take the 2nd part
			boolean propertyListed = false;
			if (!historicalProperties.isEmpty()) {
				if (propertyName.contains(QSYSCoreConstant.HASH)) {
					propertyListed = historicalProperties.contains(propertyName.split(QSYSCoreConstant.HASH)[1]);
				} else {
					propertyListed = historicalProperties.contains(propertyName);
				}
			}
			if (propertyListed && DynamicStatisticsDefinitions.checkIfExists(propertyName)) {
				dynamicStatistics.put(propertyName, propertyValue);
			} else {
				staticStatistics.put(propertyName, propertyValue);
			}
		});
		aggregatedDevice.setDynamicStatistics(dynamicStatistics);
		aggregatedDevice.setProperties(staticStatistics);
	}

	/**
	 * Retrieve information for all remote devices
	 *
	 * @param currentSizeDeviceMap Current size of cachedClients List, this param use to check number of clients was change or not
	 */
	private void populateAggregatedMonitoringData(int currentSizeDeviceMap) {
		if (executorService == null || currentSizeDeviceMap != deviceMap.size()) {
			executorService = Executors.newFixedThreadPool(deviceStatisticsCollectionThreads);
		}

		List<String> deviceIdsNeedToUpdate = new ArrayList<>();
		int threadNum = 0;

		while (!deviceIdDequeue.isEmpty() && threadNum < deviceStatisticsCollectionThreads) {
			String deviceId = deviceIdDequeue.pollFirst();
			if (!deviceMap.containsKey(deviceId)) {
				errorDeviceMap.remove(deviceId);
				continue;
			}

			deviceIdsNeedToUpdate.add(deviceId);

			int errorCount = errorDeviceMap.getOrDefault(deviceId, 0);
			if (errorCount >= QSYSCoreConstant.MAX_ERROR_COUNT) {
				errorDeviceMap.remove(deviceId);
			} else {
				errorDeviceMap.put(deviceId, errorCount + 1);
			}

			if (deviceIdsNeedToUpdate.size() >= QSYSCoreConstant.MAX_DEVICE_QUANTITY_PER_THREAD) {
				List<String> finalDeviceIdsNeedToUpdate = new ArrayList<>(deviceIdsNeedToUpdate);
				deviceExecutionPool.add(executorService.submit(new DeviceLoader(finalDeviceIdsNeedToUpdate)));
				deviceIdsNeedToUpdate.clear();
				++threadNum;
			}
		}

		if (!deviceIdsNeedToUpdate.isEmpty()) {
			List<String> finalDeviceIdsNeedToUpdate = new ArrayList<>(deviceIdsNeedToUpdate);
			deviceExecutionPool.add(executorService.submit(new DeviceLoader(finalDeviceIdsNeedToUpdate)));
		}

		--localPollingInterval;
	}

	/**
	 * This method is used to handle gain input from adapter properties and convert it to String array of named gain components for control
	 *
	 * @return Set</ String> is the String array of named gain components return from Gain input
	 */
	private Set<String> handleGainInputFromUser(String gain) {
		if (StringUtils.isNullOrEmpty(gain)) {
			return Collections.emptySet();
		}
		List<String> gainSplit = Arrays.asList(gain.split(QSYSCoreConstant.COMMA));
		Set<String> namedGainComponents = new HashSet<>();
		gainSplit.stream().forEach(namedGain -> namedGainComponents.add(namedGain.trim()));

		StringBuilder errorMessages = new StringBuilder();
		boolean isFirstComponent = true;

		// Remove start and end spaces of each gain
		for (String namedGain : gainSplit) {
			String trimmedNamedGain = namedGain.trim();

			if (trimmedNamedGain.matches(QSYSCoreConstant.SPECIAL_CHARS_PATTERN)) {
				if (!isFirstComponent) {
					errorMessages.append(", ");
				}
				errorMessages.append(trimmedNamedGain);
				isFirstComponent = false;
			} else {
				namedGainComponents.add(trimmedNamedGain);
			}
		}

		// Has error message
		if (errorMessages.length() > 0) {
			errorMessages.append(" contains one of these special characters: ~ ! @ # $ % ^ & \\\\ ' or <? or <\\\\ \"");
			throw new IllegalArgumentException("Component " + errorMessages);
		}

		return namedGainComponents;
	}

	/**
	 * handle split plugin name
	 *
	 * @return Set<String> are all name of plugin
	 */
	private Set<String> handleSplitPluginConfig() {
		if (StringUtils.isNullOrEmpty(filterPluginByName)) {
			return new HashSet<>();
		}
		Set<String> pluginConfigs = new HashSet<>();
		String[] slip = filterPluginByName.split(QSYSCoreConstant.COMMA);
		for (String deviceName : slip) {
			pluginConfigs.add(deviceName.trim().toLowerCase(Locale.ROOT));
		}
		return pluginConfigs;
	}

	/**
	 * Converts a channel name into its corresponding index representation as a string.
	 * The method removes the prefix defined by {@code QSYSCoreConstant.CHANNEL} from the
	 * input channel name, then converts any letters to their alphabetical index (A=1, B=2, ...)
	 * and appends digits as-is to form the final index string.
	 * @param channelName the full channel name string, expected to start with the prefix defined
	 *                    in {@code QSYSCoreConstant.CHANNEL}, e.g. "ChannelA"
	 * @return a string representing the channel index where letters are converted to numbers
	 *         (A=1, B=2, ...) and digits are preserved; returns an empty string if
	 *         {@code channelName} is null or does not contain any letters/digits after the prefix
	 */
	private String convertChannelNameToIndex(String channelName) {
		String valuePart = channelName.replaceFirst("^" + QSYSCoreConstant.CHANNEL, "");
		StringBuilder result = new StringBuilder();

		for (char c : valuePart.toCharArray()) {
			if (Character.isLetter(c)) {
				int num = Character.toUpperCase(c) - 'A' + 1;
				result.append(num);
			} else if (Character.isDigit(c)) {
				result.append(c);
			}
		}
		return result.toString();
	}
}