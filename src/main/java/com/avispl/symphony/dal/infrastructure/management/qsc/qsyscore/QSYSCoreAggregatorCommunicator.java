/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.GainControllingMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreConstant;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.common.QSYSCoreURL;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.QSYSPeripheralDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.ControlInterfaceDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.DisplayDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.ProcessorDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.VideoIODevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.VideoSourceDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.streamiodevice.StreamInputDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.device.inventorydevice.streamiodevice.StreamOutputDevice;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.ComponentInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.ComponentWrapper;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DesignInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DeviceInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.DeviceLANInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.LoginInfo;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.QSYSCoreMonitoringMetric;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.rpc.RpcMethod;
import com.avispl.symphony.dal.util.ControllablePropertyFactory;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * QSYSCoreAggregatorCommunicator
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/30/2023
 * @since 1.0.0
 */
public class QSYSCoreAggregatorCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {

	/**
	 * Runner service responsible for collecting data and posting processes to {@link #devicesExecutionPool}
	 */
	private QSYSCoreDeviceDataLoader deviceDataLoader;

	/**
	 * Process is running constantly and triggers collecting data from QSYSCoreDevice API endpoints base on getMultipleStatistic
	 *
	 * @author Kevin
	 * @since 1.0.0
	 */
	class QSYSCoreDeviceDataLoader implements Runnable {
		//		private volatile int threadIndex;
		private volatile boolean inProgress = true;

		/**
		 * Id of next device to get information
		 */
		private String nextDeviceId = null;

		@Override
		public void run() {
			mainLoop:
			while (inProgress) {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// Ignore for now
				}

				if (!inProgress) {
					break mainLoop;
				}

				// next line will determine whether QSYSCore monitoring was paused
				updateAggregatorStatus();
				if (devicePaused) {
					continue mainLoop;
				}

				int devicesCount = deviceMap.size();
				if (devicesCount == 0) {
					continue mainLoop;
				}

				if (nextDeviceId == null) {
					nextDeviceId = deviceMap.firstKey();
				}

				boolean checkLoopRunToDevicesNeedToUpdate = false;

				List<String> deviceIdsNeedToUpdate = new ArrayList<>();
				int threadNum = 0;

				for (String deviceId : deviceMap.keySet()) {
					if (!inProgress) {
						break;
					}
					if (threadNum >= QSYSCoreConstant.MAX_THREAD) {
						nextDeviceId = deviceId;
						break;
					}

					if (deviceId.equals(nextDeviceId)) {
						checkLoopRunToDevicesNeedToUpdate = true;
					}

					if (checkLoopRunToDevicesNeedToUpdate) {
						if (deviceIdsNeedToUpdate.size() < QSYSCoreConstant.MAX_DEVICE_QUANTITY_PER_THREAD) {
							deviceIdsNeedToUpdate.add(deviceId);
						} else {
							List<String> finalDeviceIdsNeedToUpdate = new ArrayList<>(deviceIdsNeedToUpdate);
							devicesExecutionPool.add(executorService.submit(() -> {
								retrieveAggregatedDeviceByIdList(finalDeviceIdsNeedToUpdate);
							}));
							deviceIdsNeedToUpdate.clear();
							++threadNum;
							if (threadNum >= QSYSCoreConstant.MAX_THREAD) {
								nextDeviceId = null;
							}
						}
					}
				}

				do {
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e) {
						if (!inProgress) {
							break;
						}
					}
					devicesExecutionPool.removeIf(Future::isDone);
				} while (!devicesExecutionPool.isEmpty());

				nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 30000;

				while (nextDevicesCollectionIterationTimestamp > System.currentTimeMillis()) {
					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) {
						//
					}
				}
			}
			// Finished collecting
		}

		/**
		 * Triggers main loop to stop
		 */
		public void stop() {
			inProgress = false;
		}
		// Finished collecting
	}

	/**
	 * Update the status of the device.
	 * The device is considered as paused if did not receive any retrieveMultipleStatistics()
	 * calls during {@link QSYSCoreAggregatorCommunicator}
	 */
	private synchronized void updateAggregatorStatus() {
		devicePaused = validRetrieveStatisticsTimestamp < System.currentTimeMillis();
	}

	/**
	 * Aggregator inactivity timeout. If the {@link QSYSCoreAggregatorCommunicator#retrieveMultipleStatistics()}  method is not
	 * called during this period of time - device is considered to be paused, thus the Cloud API
	 * is not supposed to be called
	 */
	private static final long retrieveStatisticsTimeOut = 3 * 60 * 1000;

	/**
	 * Uptime time stamp to valid one
	 */
	private synchronized void updateValidRetrieveStatisticsTimestamp() {
		validRetrieveStatisticsTimestamp = System.currentTimeMillis() + retrieveStatisticsTimeOut;
		updateAggregatorStatus();
	}

	/**
	 * This parameter holds timestamp of when we need to stop performing API calls
	 * It used when device stop retrieving statistic. Updated each time of called #retrieveMultipleStatistics
	 */
	private volatile long validRetrieveStatisticsTimestamp;

	/**
	 * We don't want the statistics to be collected constantly, because if there's not a big list of devices -
	 * new devices' statistics loop will be launched before the next monitoring iteration. To avoid that -
	 * this variable stores a timestamp which validates it, so when the devices' statistics is done collecting, variable
	 * is set to currentTime + 30s, at the same time, calling {@link #retrieveMultipleStatistics()} and updating the
	 * {@link #aggregatedDeviceList} resets it to the currentTime timestamp, which will re-activate data collection.
	 */
	private long nextDevicesCollectionIterationTimestamp;

	/**
	 * Indicates whether a device is considered as paused.
	 * True by default so if the system is rebooted and the actual value is lost -> the device won't start stats
	 * collection unless the {@link QSYSCoreAggregatorCommunicator#retrieveMultipleStatistics()} method is called which will change it
	 * to a correct value
	 */
	private volatile boolean devicePaused = false;

	/**
	 * Executor that runs all the async operations, that is posting and
	 * {@link #devicesExecutionPool} is keeping track of
	 */
	private static ExecutorService executorService;

	private String qrcPort = String.valueOf(QSYSCoreConstant.QRC_PORT);
	private boolean isEmergencyDelivery = false;
	private ExtendedStatistics localExtStats;
	private LoginInfo loginInfo;

	/**
	 * Filter gain by name
	 */
	private String filterGainName;

	/**
	 * Set store all name for filter gain
	 */
	private Set<String> filterGainNameSet = new HashSet<>();

	/**
	 * Filter model by name
	 */
	private String filterModelName;


	/**
	 * Filter component by name
	 */
	private String filterComponentName;

	/**
	 * Polling interval which applied in adapter
	 */
//	private volatile int localPollingInterval = QSYSCoreConstant.MIN_POLLING_INTERVAL;

	/**
	 * store pollingInterval adapter properties
	 */
//	private volatile String pollingInterval;

	/**
	 * List save all device
	 */
	public volatile TreeMap<String, QSYSPeripheralDevice> deviceMap = new TreeMap<>();

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
	private List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

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
	 * Retrieves {@link #filterGainName}
	 *
	 * @return value of {@link #filterGainName}
	 */
	public String getFilterGainName() {
		return filterGainName;
	}

	/**
	 * Sets {@link #filterGainName} value
	 *
	 * @param filterGainName new value of {@link #filterGainName}
	 */
	public void setFilterGainName(String filterGainName) {
		this.filterGainName = filterGainName;
	}

	/**
	 * Retrieves {@link #filterModelName}
	 *
	 * @return value of {@link #filterModelName}
	 */
	public String getFilterModelName() {
		return filterModelName;
	}

	/**
	 * Sets {@link #filterModelName} value
	 *
	 * @param filterModelName new value of {@link #filterModelName}
	 */
	public void setFilterModelName(String filterModelName) {
		this.filterModelName = filterModelName;
	}

	/**
	 * Retrieves {@link #filterComponentName}
	 *
	 * @return value of {@link #filterComponentName}
	 */
	public String getFilterComponentName() {
		return filterComponentName;
	}

	/**
	 * Sets {@link #filterComponentName} value
	 *
	 * @param filterComponentName new value of {@link #filterComponentName}
	 */
	public void setFilterComponentName(String filterComponentName) {
		this.filterComponentName = filterComponentName;
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to get the list of statistics to be displayed
	 *
	 * @return List<Statistics> This return the list of statistics.
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		ExtendedStatistics extendedStatistics = new ExtendedStatistics();

		// This is to make sure if the statistics is being fetched before/after any set of control operations
		reentrantLock.lock();
		try {
			Map<String, String> stats = new HashMap<>();
			List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();
			updateFilterGainNameSet();
			//Create loginInfo
			if (loginInfo == null) {
				loginInfo = LoginInfo.createLoginInfoInstance();
			}

			if (!isEmergencyDelivery) {
				if (qrcCommunicator == null) {
					initQRCCommunicator();
				}

				populateQSYSAggregatorMonitoringData(stats);

				populateQSYSComponent(stats, controllableProperties);

				extendedStatistics.setStatistics(stats);
				extendedStatistics.setControllableProperties(controllableProperties);
				localExtStats = extendedStatistics;
			}
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
		try {
			//delete when done
			if (qrcCommunicator == null) {
				initQRCCommunicator();
			}

			String property = controllableProperty.getProperty();
			String value = String.valueOf(controllableProperty.getValue());

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("controlProperty property " + property);
				this.logger.debug("controlProperty value " + value);
			}

			String[] splitProperty = property.split(QSYSCoreConstant.HASH);

			// Ex: Gain:Named Component#Gain Value Control
			// metricName = Gain Value Control
			// namedComponent = Named Component
			String metricName = splitProperty[1];
			List<String> splitComponent = Arrays.asList(splitProperty[0].split(QSYSCoreConstant.COLON, 2));
			switch (splitComponent.get(0)) {
				case QSYSCoreConstant.GAIN:
					gainControl(metricName, splitComponent.get(1), value);
			}
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
		if (executorService == null) {
			// Due to the bug that after changing properties on fly - the adapter is destroyed but adapter is not initialized properly,
			// so executor service is not running. We need to make sure executorService exists
			executorService = Executors.newFixedThreadPool(8);
			executorService.submit(deviceDataLoader = new QSYSCoreDeviceDataLoader());
		}

		updateValidRetrieveStatisticsTimestamp();

		aggregatedDeviceList.clear();
		for (QSYSPeripheralDevice device : deviceMap.values()) {
			AggregatedDevice aggregatedDevice = new AggregatedDevice();
			aggregatedDevice.setProperties(device.getStats());
			aggregatedDevice.setControllableProperties(device.getAdvancedControllableProperties());
			aggregatedDeviceList.add(aggregatedDevice);
		}

		if (aggregatedDeviceList.isEmpty()) {
			return aggregatedDeviceList;
		}
//		return cloneAndPopulateAggregatedDeviceList();
		return aggregatedDeviceList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> listDeviceId) throws Exception {
		return retrieveMultipleStatistics().stream().filter(aggregatedDevice -> listDeviceId.contains(aggregatedDevice.getDeviceId())).collect(Collectors.toList());
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
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("QRC Port must be a valid port number");
		}

		qrcCommunicator = new QRCCommunicator();
		qrcCommunicator.setHost(this.host);
		qrcCommunicator.setPort(port);
		qrcCommunicator.init();
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
		if (loginInfo.getToken() != null) {
			headers.setBearerAuth(loginInfo.getToken());
		}
		return headers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalDestroy() {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal destroy is called.");
		}

//		localPollingInterval = QSYSCoreConstant.MIN_POLLING_INTERVAL;

		if (deviceDataLoader != null) {
			deviceDataLoader.stop();
			deviceDataLoader = null;
		}

		aggregatedDeviceList.clear();
		if (executorService != null) {
			executorService.shutdownNow();
			executorService = null;
		}
		devicesExecutionPool.forEach(future -> future.cancel(true));
		devicesExecutionPool.clear();
		super.internalDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}

		executorService = Executors.newFixedThreadPool(8);
		executorService.submit(deviceDataLoader = new QSYSCoreDeviceDataLoader());
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
	 * This method is used to populate all monitoring properties
	 *
	 * @param stats list statistic property
	 * @throws ResourceNotReachableException when failedMonitor said all device monitoring data are failed to get
	 */
	private void populateQSYSAggregatorMonitoringData(Map<String, String> stats) {
		Objects.requireNonNull(stats);
		if (!StringUtils.isNullOrEmpty(getPassword()) && !StringUtils.isNullOrEmpty(getLogin())) {
			retrieveTokenFromCore();
		} else {
			this.loginInfo.setToken(QSYSCoreConstant.AUTHORIZED);
		}

		retrieveQSYSAggregatorInfo(stats);
		retrieveQSYSAggregatorNetworkInfo(stats);
		retrieveQSYSAggregatorDesign(stats);
	}

	/**
	 * Get all information of aggregator
	 *
	 * @param stats Map store all information
	 */
	private void retrieveQSYSAggregatorInfo(Map<String, String> stats) {
		try {
			if (this.loginInfo.getToken() != null) {
				DeviceInfo deviceInfo = objectMapper.readValue(doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.DEVICE_INFO)), DeviceInfo.class);
				if (deviceInfo != null && deviceInfo.getDeviceInfoData() != null) {
					stats.put(QSYSCoreMonitoringMetric.DEVICE_ID.getName(), getDataOrDefaultDataIfNull(deviceInfo.getDeviceInfoData().getNaturalId()));
					stats.put(QSYSCoreMonitoringMetric.SERIAL_NUMBER.getName(), getDataOrDefaultDataIfNull(deviceInfo.getDeviceInfoData().getSerial()));
					stats.put(QSYSCoreMonitoringMetric.DEVICE_NAME.getName(), getDataOrDefaultDataIfNull(deviceInfo.getDeviceInfoData().getName()));
					stats.put(QSYSCoreMonitoringMetric.DEVICE_MODEL.getName(), getDataOrDefaultDataIfNull(deviceInfo.getDeviceInfoData().getModel()));
					if (deviceInfo.getDeviceInfoData().getFirmware() != null) {
						stats.put(QSYSCoreMonitoringMetric.FIRMWARE_VERSION.getName(), getDataOrDefaultDataIfNull(deviceInfo.getDeviceInfoData().getFirmware().getBuildName()));
					}
					stats.put(QSYSCoreMonitoringMetric.UPTIME.getName(), getDataOrDefaultDataIfNull(convertMillisecondsToDate(String.valueOf(deviceInfo.getDeviceInfoData().getUptime()))));
					if (deviceInfo.getDeviceInfoData().getStatus() != null) {
						stats.put(QSYSCoreMonitoringMetric.STATUS.getName(), getDataOrDefaultDataIfNull(deviceInfo.getDeviceInfoData().getStatus().getName()));
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage());
		}
	}

	/**
	 * Get network information of aggregator device
	 *
	 * @param stats Map store all information
	 */
	private void retrieveQSYSAggregatorNetworkInfo(Map<String, String> stats) {
		try {
			if (this.loginInfo.getToken() != null) {
				DeviceLANInfo deviceLANInfo = objectMapper.readValue(doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.DEVICE_LAN_INFO)), DeviceLANInfo.class);
				if (deviceLANInfo != null && deviceLANInfo.getData() != null) {
					stats.put(QSYSCoreMonitoringMetric.HOSTNAME.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getHostname()));
					if (deviceLANInfo.getData().getInterfaces().size() > 0) {
						stats.put(QSYSCoreMonitoringMetric.LAN_A_IP_ADDRESS.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(0).getIpAddress()));
					}
					if (deviceLANInfo.getData().getInterfaces().size() > 1) {
						stats.put(QSYSCoreMonitoringMetric.LAN_B_IP_ADDRESS.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(1).getIpAddress()));
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage());
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
			if (response.size() > 1) {
				DesignInfo designInfo = objectMapper.readValue(response.get(1), DesignInfo.class);
				if (designInfo != null && designInfo.getResult() != null) {
					stats.put(QSYSCoreMonitoringMetric.PLATFORM.getName(), getDataOrDefaultDataIfNull(designInfo.getResult().getPlatform()));
					stats.put(QSYSCoreMonitoringMetric.DESIGN_NAME.getName(), getDataOrDefaultDataIfNull(designInfo.getResult().getDesignName()));
					stats.put(QSYSCoreMonitoringMetric.DESIGN_CODE.getName(), getDataOrDefaultDataIfNull(designInfo.getResult().getDesignCode()));
					stats.put(QSYSCoreMonitoringMetric.STATE.getName(), getDataOrDefaultDataIfNull(designInfo.getResult().getState()));
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage());
		}
	}

	/**
	 * Get list of component
	 */
	private void populateQSYSComponent(Map<String, String> stats, List<AdvancedControllableProperty> controllableProperties) {
		try {
			RpcMethod method = RpcMethod.GET_COMPONENTS;
			String request = String.format(RpcMethod.getRequest(), method.getName(), RpcMethod.getParamsString(method));
			List<String> response = Arrays.asList(qrcCommunicator.send(request));
			if (response.size() > 1) {
				ComponentWrapper componentWrapper = objectMapper.readValue(response.get(1), ComponentWrapper.class);
				if (componentWrapper.getResult() != null) {
					for (ComponentInfo componentInfo : componentWrapper.getResult()) {
						if (QSYSCoreConstant.GAIN_TYPE.equals(componentInfo.getType()) && StringUtils.isNotNullOrEmpty(componentInfo.getId())
								&& (filterGainNameSet.isEmpty() || filterGainNameSet.contains(componentInfo.getId()))) {
							retrieveGainComponent(stats, controllableProperties, componentInfo.getId());
						} else {
							if (componentInfo.getType() != null && QSYSCoreConstant.SUPPORTED_DEVICE_TYPE.contains(componentInfo.getType()) && componentInfo.getId() != null && !deviceMap.containsKey(
									componentInfo.getId())) {
								QSYSPeripheralDevice device = createDeviceByType(componentInfo.getType());
								if (device != null) {
									deviceMap.put(componentInfo.getId(), device);
								} else {
									this.logger.error("Type of device " + componentInfo.getId() + " does not exist");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage());
		}
	}

	/**
	 * Create a peripheral device by type
	 *
	 * @param type device type need to create
	 * @return peripheral device
	 */
	private QSYSPeripheralDevice createDeviceByType(String type) {
		switch (type) {
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
			default:
				return null;
		}
	}

	/**
	 * Retrieve gain component
	 *
	 * @param stats stats of aggregator
	 * @param controllableProperties control list of agggregator
	 * @param deviceId Id of gain component
	 */
	private void retrieveGainComponent(Map<String, String> stats, List<AdvancedControllableProperty> controllableProperties, String deviceId) {
		try {
			String request = String.format(RpcMethod.getRequest(), RpcMethod.GET_CONTROLS.getName(), RpcMethod.getParamsString(RpcMethod.GET_CONTROLS));
			request = String.format(request, deviceId);
			List<String> response = Arrays.asList(qrcCommunicator.send(request));
			if (response.size() > 1) {
				JsonNode deviceControlInfo = objectMapper.readValue(response.get(1), JsonNode.class);

				if (deviceControlInfo != null && deviceControlInfo.get("result") != null) {
					String groupName = QSYSCoreConstant.GAIN + QSYSCoreConstant.COLON + deviceId;
					JsonNode deviceControls = deviceControlInfo.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS);
					for (JsonNode control : deviceControls) {
						if (GainControllingMetric.BYPASS_CONTROL.getProperty().equals(control.get(QSYSCoreConstant.CONTROL_NAME).asText())) {
							stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.BYPASS_CONTROL.getMetric(), "");
							controllableProperties.add(ControllablePropertyFactory.createSwitch(groupName + QSYSCoreConstant.HASH + GainControllingMetric.BYPASS_CONTROL.getMetric(),
									QSYSCoreConstant.FALSE.equals(control.get(QSYSCoreConstant.CONTROL_VALUE).asText()) ? 0 : 1));
						} else if (GainControllingMetric.GAIN_VALUE_CONTROL.getProperty().equals(control.get(QSYSCoreConstant.CONTROL_NAME).asText())) {
							Float value = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE).asText());
							if (value != null) {
								value = value;
								stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.CURRENT_GAIN_VALUE.getMetric(), String.valueOf(value));
								stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.GAIN_VALUE_CONTROL.getMetric(), String.valueOf(value));

								Float firstValue = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE_MIN).asText());
								Float secondValue = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE_MAX).asText());
								Float minValue = Math.min(firstValue, secondValue);
								Float maxValue = Math.max(firstValue, secondValue);

								if (minValue != null && maxValue != null) {
									controllableProperties.add(ControllablePropertyFactory.createSlider(groupName + QSYSCoreConstant.HASH + GainControllingMetric.GAIN_VALUE_CONTROL.getMetric(),
											minValue * 100, maxValue, value));
								}
							}
						} else if (GainControllingMetric.INVERT_CONTROL.getProperty().equals(control.get(QSYSCoreConstant.CONTROL_NAME).asText())) {
							stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.INVERT_CONTROL.getMetric(), "");
							controllableProperties.add(ControllablePropertyFactory.createSwitch(groupName + QSYSCoreConstant.HASH + GainControllingMetric.INVERT_CONTROL.getMetric(),
									QSYSCoreConstant.FALSE.equals(control.get(QSYSCoreConstant.CONTROL_VALUE).asText()) ? 0 : 1));
						} else if (GainControllingMetric.INVERT_CONTROL.getProperty().equals(control.get(QSYSCoreConstant.CONTROL_NAME).asText())) {
							stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.MUTE_CONTROL.getMetric(), "");
							controllableProperties.add(ControllablePropertyFactory.createSwitch(groupName + QSYSCoreConstant.HASH + GainControllingMetric.MUTE_CONTROL.getMetric(),
									QSYSCoreConstant.FALSE.equals(control.get(QSYSCoreConstant.CONTROL_VALUE).asText()) ? 0 : 1));
						}
					}
				}
			}
		} catch (
				Exception e) {
			throw new ResourceNotReachableException(e.getMessage());
		}
	}

	/**
	 * Convert String to Float or null if can not convert
	 *
	 * @param value String value need to convert to float
	 */
	private Float tryParseFloatOrNull(String value) {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Get a token to log in the device
	 */
	private void retrieveTokenFromCore() {
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
						throw new ResourceNotReachableException(QSYSCoreConstant.GETTING_TOKEN_ERR);
					}
				} else {
					throw new ResourceNotReachableException(QSYSCoreConstant.GETTING_TOKEN_ERR);
				}
			}
		} catch (Exception e) {
			this.loginInfo.setToken(null);
			throw new ResourceNotReachableException(QSYSCoreConstant.GETTING_TOKEN_ERR);
		}
	}

	/**
	 * Build Url to call API
	 *
	 * @param path path of Url
	 * @return Url
	 */
	private String buildDeviceFullPath(String path) {
		Objects.requireNonNull(path);

		return QSYSCoreConstant.HTTP
				+ getHost()
				+ path;
	}

	/**
	 * Return data or default data if data is null or empty
	 *
	 * @param data data to check null or empty
	 * @return data or default data
	 */
	private String getDataOrDefaultDataIfNull(String data) {
		return StringUtils.isNotNullOrEmpty(String.valueOf(data)) ? String.valueOf(data) : QSYSCoreConstant.DEFAUL_DATA;
	}

/**
 * calculating local polling interval
 *
 * @throws IllegalArgumentException when get limit rate exceed error
 */
//	private int calculatingLocalPollingInterval() {
//
//		try {
//			int pollingIntervalValue = QSYSCoreConstant.MIN_POLLING_INTERVAL;
//			if (StringUtils.isNotNullOrEmpty(pollingInterval)) {
//				pollingIntervalValue = Integer.parseInt(pollingInterval);
//			}
//
//			int minPollingInterval = calculatingMinPollingInterval();
//			if (pollingIntervalValue < minPollingInterval) {
//				logger.error(String.format("invalid pollingInterval value, pollingInterval must greater than: %s", minPollingInterval));
//				return minPollingInterval;
//			}
//			return pollingIntervalValue;
//		} catch (Exception e) {
//			throw new IllegalArgumentException(String.format("Unexpected pollingInterval value: %s", pollingInterval), e);
//		}
//	}

/**
 * calculating minimum of polling interval
 *
 * @return Number of polling interval
 */
//	private int calculatingMinPollingInterval() {
//		if (!deviceMap.isEmpty()) {
//			return IntMath.divide(deviceMap.size(), QSYSCoreConstant.MAX_THREAD_QUANTITY * QSYSCoreConstant.MAX_DEVICE_QUANTITY_PER_THREAD, RoundingMode.CEILING);
//		}
//		return QSYSCoreConstant.MIN_POLLING_INTERVAL;
//	}

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
				if (response.size() > 1) {
					JsonNode deviceControlResponse = objectMapper.readValue(response.get(1), JsonNode.class);
					deviceMap.get(deviceId).monitoringDevice(deviceControlResponse);
				}
			} catch (Exception e) {
				continue;
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
	private void gainControl(String metricName, String namedComponent, String value) throws IllegalAccessException {
		RpcMethod method = RpcMethod.SET_CONTROLS;
		String request = String.format(RpcMethod.getRequest(), method.getName(), RpcMethod.getParamsString(method));
		request = String.format(request, namedComponent, GainControllingMetric.getByMetric(metricName).getProperty(), String.valueOf(value));
		try {
			List<String> response = Arrays.asList(qrcCommunicator.send(request));
			if (response.size() > 1) {
				JsonNode responseControl = objectMapper.readValue(response.get(1), JsonNode.class);

				if (!responseControl.has(QSYSCoreConstant.RESULT) || !responseControl.get(QSYSCoreConstant.RESULT).asText().equals(QSYSCoreConstant.TRUE)) {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Error: cannot set gain value of component " + namedComponent);
					}
					throw new IllegalAccessException("Cannot set " + GainControllingMetric.getByMetric(metricName).getProperty() + " value of component \"" + namedComponent + "\"");
				}
			}
		} catch (Exception e) {
			throw new IllegalAccessException(e.getMessage());
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
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd HH:mm:ss");
			return dateFormat.format(date);
		} catch (Exception e) {
			logger.debug("Error when convert milliseconds to datetime");
		}
		return QSYSCoreConstant.DEFAUL_DATA;
	}

	/**
	 * Update filterGainNameSet
	 */
	private void updateFilterGainNameSet() {
		Set<String> newFilterGainNameSet = new HashSet<>();
		if (StringUtils.isNotNullOrEmpty(filterGainName)) {
			String[] splitNamed = filterGainName.split(QSYSCoreConstant.COMMAS);
			newFilterGainNameSet.addAll(Arrays.asList(splitNamed));
		}
		filterGainNameSet = newFilterGainNameSet;
	}
}