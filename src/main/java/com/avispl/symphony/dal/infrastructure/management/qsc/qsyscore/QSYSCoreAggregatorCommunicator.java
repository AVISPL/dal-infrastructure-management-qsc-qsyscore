/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import com.google.common.math.IntMath;

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
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.UpdateLocalExtStat;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.dto.rpc.RpcMethod;
import com.avispl.symphony.dal.infrastructure.management.qsc.qsyscore.statistics.DynamicStatisticsDefinitions;
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

	class DeviceLoader implements Runnable {
		private volatile List<String> deviceIds;

		/**
		 * Parameters constructors
		 *
		 * @param deviceIds list all id of device
		 */
		public DeviceLoader(List<String> deviceIds) {
			this.deviceIds = deviceIds;
		}

		@Override
		public void run() {

			if (!deviceMap.isEmpty()) {
				retrieveAggregatedDeviceByIdList(this.deviceIds);
			}
		}
	}

	/**
	 * Executor that runs all the async operations, that is posting and
	 * {@link #devicesExecutionPool} is keeping track of
	 */
	private static ExecutorService executorService;

	/**
	 * Configurable property for historical properties, comma separated values kept as set locally
	 */
	private Set<String> historicalProperties = new HashSet<>();

	/**
	 * Polling interval which applied in adapter
	 */
	private volatile int localPollingInterval = QSYSCoreConstant.MIN_POLLING_INTERVAL;

	/**
	 * Number of threads in a thread pool reserved for the device statistics collection
	 */
	private volatile int deviceStatisticsCollectionThreads;

	/**
	 * store pollingInterval adapter properties
	 */
	private volatile String pollingInterval;

	/**
	 * id of next device to get information
	 */
	private String nextDeviceId = null;

	private String qrcPort = String.valueOf(QSYSCoreConstant.QRC_PORT);
	private boolean isEmergencyDelivery = false;
	private ExtendedStatistics localExtStats;
	private LoginInfo loginInfo;
	private ExtendedStatistics localExtStat = null;
	private UpdateLocalExtStat updateLocalExtStatDto;

	/**
	 * Filter gain by name
	 */
	private String filterGainName;

	/**
	 * Set store all name for filter gain
	 */
	private Set<String> filterGainNameSet;

	private volatile Queue<String> priorityIdDeivceQueue = new ConcurrentLinkedQueue<>();

	/**
	 * Filter model by name
	 */
	private String filterDeviceType;

	/**
	 * Set store all type for filter device
	 */
	private Set<String> filterDeviceTypeSet;

	/**
	 * list all thread
	 */
	private List<Future> deviceExecutionPool = new ArrayList<>();

	/**
	 * Filter component by name
	 */
	private String filterComponentName;

	/**
	 * Set store all name for filter component
	 */
	private Set<String> filterComponentNameSet;

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
	 * Retrieves {@link #filterDeviceType}
	 *
	 * @return value of {@link #filterDeviceType}
	 */
	public String getFilterDeviceType() {
		return filterDeviceType;
	}

	/**
	 * Sets {@link #filterDeviceType} value
	 *
	 * @param filterDeviceType new value of {@link #filterDeviceType}
	 */
	public void setFilterDeviceType(String filterDeviceType) {
		this.filterDeviceType = filterDeviceType;
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
		ExtendedStatistics extendedStatistics = new ExtendedStatistics();

		// This is to make sure if the statistics is being fetched before/after any set of control operations
		reentrantLock.lock();
		try {

			Map<String, String> stats = new HashMap<>();
			List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();
			filterGainNameSet = convertUserInput(filterGainName);
			filterComponentNameSet = convertUserInput(filterComponentName);
			updateFilterDeviceTypeSet();

			if (qrcCommunicator == null) {
				initQRCCommunicator();
			}

			//Create loginInfo
			if (loginInfo == null) {
				loginInfo = LoginInfo.createLoginInfoInstance();
			}

			Objects.requireNonNull(stats);
			if (!StringUtils.isNullOrEmpty(getPassword()) && !StringUtils.isNullOrEmpty(getLogin())) {
				retrieveTokenFromCore();
			} else {
				this.loginInfo.setToken(QSYSCoreConstant.AUTHORIZED);
			}

			if (isEmergencyDelivery && localExtStat != null) {
				isEmergencyDelivery = false;
			} else {
				//Because there are some threads that keep running when the next getMultiple is called,
				// so we have to stop all those threads just before the next getMultiple runs
				if (executorService != null) {
					for (Future future : deviceExecutionPool) {
						future.cancel(true);
					}
					deviceExecutionPool.clear();
				}

				int currentSizeDeviceMap = deviceMap.size();

				populateQSYSAggregatorMonitoringData(stats);

				populateQSYSComponent(stats, controllableProperties);

				localPollingInterval = calculatingLocalPollingInterval();
				deviceStatisticsCollectionThreads = calculatingThreadQuantity();

				populateAggregatedMonitoringData(currentSizeDeviceMap);
				extendedStatistics.setStatistics(stats);
				extendedStatistics.setControllableProperties(controllableProperties);
				localExtStats = extendedStatistics;
			}

			if (updateLocalExtStatDto != null) {
				updateLocalExtStat(updateLocalExtStatDto);
				updateLocalExtStatDto = null;
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
					gainControl(metricName, splitComponent.get(1), value, property);
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
		aggregatedDeviceList.clear();
		for (Entry<String, QSYSPeripheralDevice> device : deviceMap.entrySet()) {
			AggregatedDevice aggregatedDevice = new AggregatedDevice();
			aggregatedDevice.setDeviceId(device.getKey());
			aggregatedDevice.setDeviceOnline(true);
			aggregatedDevice.setDeviceName(device.getKey());
			aggregatedDevice.setProperties(device.getValue().getStats());
			provisionTypedStatistics(aggregatedDevice.getProperties(), aggregatedDevice);
			aggregatedDevice.setControllableProperties(device.getValue().getAdvancedControllableProperties());
			aggregatedDeviceList.add(aggregatedDevice);
		}

		if (aggregatedDeviceList.isEmpty()) {
			return aggregatedDeviceList;
		}

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
				throw new IllegalArgumentException("Port must greater than " + QSYSCoreConstant.MIN_PORT + " and less than " + QSYSCoreConstant.MAX_PORT);
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

		aggregatedDeviceList.clear();
		deviceMap.clear();

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
			throw new ResourceNotReachableException("Error when retrieve aggregator information", e);
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
						String group = QSYSCoreMonitoringMetric.LAN_A.getName() + QSYSCoreConstant.HASH;
						stats.put(group + QSYSCoreMonitoringMetric.IP_ADDRESS.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(0).getIpAddress()));
						stats.put(group + QSYSCoreMonitoringMetric.SUBNET_MASK.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(0).getNetMask()));
						stats.put(group + QSYSCoreMonitoringMetric.GATEWAY.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(0).getGateway()));
						stats.put(group + QSYSCoreMonitoringMetric.MAC_ADDRESS.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(0).getMacAddress()));
					}
					if (deviceLANInfo.getData().getInterfaces().size() > 1) {
						String group = QSYSCoreMonitoringMetric.LAN_B.getName() + QSYSCoreConstant.HASH;
						stats.put(group + QSYSCoreMonitoringMetric.IP_ADDRESS.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(1).getIpAddress()));
						stats.put(group + QSYSCoreMonitoringMetric.SUBNET_MASK.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(1).getNetMask()));
						stats.put(group + QSYSCoreMonitoringMetric.GATEWAY.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(1).getGateway()));
						stats.put(group + QSYSCoreMonitoringMetric.MAC_ADDRESS.getName(), getDataOrDefaultDataIfNull(deviceLANInfo.getData().getInterfaces().get(1).getMacAddress()));
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when retrieve aggregator network information", e);
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
			throw new ResourceNotReachableException("Error when retrieve aggregator design", e);
		}
	}

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
			if (response.size() > 1) {
				ComponentWrapper componentWrapper = objectMapper.readValue(response.get(1), ComponentWrapper.class);
				if (componentWrapper.getResult() != null) {

					//Because some device could be removed, so we need save all existed device
					Set<String> existDeviceSet = new HashSet<>();

					for (ComponentInfo componentInfo : componentWrapper.getResult()) {
						if (QSYSCoreConstant.GAIN_TYPE.equals(componentInfo.getType()) && StringUtils.isNotNullOrEmpty(componentInfo.getId())
								&& (filterGainNameSet.isEmpty() || filterGainNameSet.contains(componentInfo.getId()))) {
							retrieveGainComponent(stats, controllableProperties, componentInfo.getId());
						} else {
							if (componentInfo.getType() != null && QSYSCoreConstant.SUPPORTED_DEVICE_TYPE.contains(componentInfo.getType())
									&& (filterDeviceTypeSet.isEmpty() || filterDeviceTypeSet.contains(componentInfo.getType()))
									&& componentInfo.getId() != null
									&& (filterComponentNameSet.isEmpty() || filterComponentNameSet.contains(componentInfo.getId()))) {
								existDeviceSet.add(componentInfo.getId());
								QSYSPeripheralDevice device = createDeviceByType(componentInfo.getType());
								if (device != null && !deviceMap.containsKey(componentInfo.getId())) {
									deviceMap.put(componentInfo.getId(), device);
								}
							}
						}
					}

					//Remove device does not exist
					for (String deviceId : deviceMap.keySet()) {
						if (!existDeviceSet.contains(deviceId)) {
							deviceMap.remove(deviceId);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when populate component", e);
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
						if (GainControllingMetric.BYPASS_CONTROL.getProperty().equals(control.get(QSYSCoreConstant.CONTROL_NAME).asText())) {
							stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.BYPASS_CONTROL.getMetric(), QSYSCoreConstant.EMPTY);
							controllableProperties.add(ControllablePropertyFactory.createSwitch(groupName + QSYSCoreConstant.HASH + GainControllingMetric.BYPASS_CONTROL.getMetric(),
									QSYSCoreConstant.FALSE.equals(control.get(QSYSCoreConstant.CONTROL_VALUE).asText()) ? 0 : 1));
						} else if (GainControllingMetric.GAIN_VALUE_CONTROL.getProperty().equals(control.get(QSYSCoreConstant.CONTROL_NAME).asText())) {
							Float value = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE).asText());
							if (value != null) {

								value = Float.valueOf((float) Math.ceil(value * 100)) / 100;

								stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.CURRENT_GAIN_VALUE.getMetric(), String.valueOf(value));
								stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.GAIN_VALUE_CONTROL.getMetric(), String.valueOf(value));

								Float firstValue = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE_MIN).asText());
								Float secondValue = tryParseFloatOrNull(control.get(QSYSCoreConstant.CONTROL_VALUE_MAX).asText());
								if (firstValue != null && secondValue != null) {
									Float minValue = Math.min(firstValue, secondValue);
									Float maxValue = Math.max(firstValue, secondValue);

									minValue = Float.valueOf((float) Math.ceil(minValue * 100)) / 100;
									maxValue = Float.valueOf((float) Math.ceil(maxValue * 100)) / 100;

									if (minValue != null && maxValue != null) {
										controllableProperties.add(ControllablePropertyFactory.createSlider(groupName + QSYSCoreConstant.HASH + GainControllingMetric.GAIN_VALUE_CONTROL.getMetric(),
												minValue, maxValue, value));
									}
								}
							}
						} else if (GainControllingMetric.INVERT_CONTROL.getProperty().equals(control.get(QSYSCoreConstant.CONTROL_NAME).asText())) {
							stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.INVERT_CONTROL.getMetric(), QSYSCoreConstant.EMPTY);
							controllableProperties.add(ControllablePropertyFactory.createSwitch(groupName + QSYSCoreConstant.HASH + GainControllingMetric.INVERT_CONTROL.getMetric(),
									QSYSCoreConstant.FALSE.equals(control.get(QSYSCoreConstant.CONTROL_VALUE).asText()) ? 0 : 1));
						} else if (GainControllingMetric.MUTE_CONTROL.getProperty().equals(control.get(QSYSCoreConstant.CONTROL_NAME).asText())) {
							stats.put(groupName + QSYSCoreConstant.HASH + GainControllingMetric.MUTE_CONTROL.getMetric(), QSYSCoreConstant.EMPTY);
							controllableProperties.add(ControllablePropertyFactory.createSwitch(groupName + QSYSCoreConstant.HASH + GainControllingMetric.MUTE_CONTROL.getMetric(),
									QSYSCoreConstant.FALSE.equals(control.get(QSYSCoreConstant.CONTROL_VALUE).asText()) ? 0 : 1));
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when retrieve " + deviceId + " gain component", e);
		}
	}

	/**
	 * This method used to update local extended statistics when control a property or in monitoring cycle
	 *
	 * @param updateLocalExtStatDto is the dto to update local extended statistic
	 */
	private void updateLocalExtStat(UpdateLocalExtStat updateLocalExtStatDto) {
		if (localExtStat.getStatistics() == null || localExtStat.getControllableProperties() == null) {
			return;
		}

		String stringValue = updateLocalExtStatDto.getValue();
		float value = tryParseFloatOrNull(stringValue);

		try {
			String request = String.format(RpcMethod.getRequest(), RpcMethod.GET.getName(), RpcMethod.getParamsString(RpcMethod.GET));

			request = String.format(request, updateLocalExtStatDto.getNamedComponent().split(QSYSCoreConstant.COLON)[1], updateLocalExtStatDto.getControllingMetric().getProperty());
			List<String> response = Arrays.asList(qrcCommunicator.send(request));
			if (response.size() > 1) {
				JsonNode jsonResponse = objectMapper.readValue(response.get(1), JsonNode.class);
				if (jsonResponse.hasNonNull(QSYSCoreConstant.RESULT) && jsonResponse.get(QSYSCoreConstant.RESULT).has(QSYSCoreConstant.CONTROLS)) {
					JsonNode jsonValue = jsonResponse.get(QSYSCoreConstant.RESULT).get(QSYSCoreConstant.CONTROLS);
					switch (updateLocalExtStatDto.getControllingMetric()) {
						case BYPASS_CONTROL:
						case MUTE_CONTROL:
						case INVERT_CONTROL:
							value = Integer.parseInt(jsonValue.get(QSYSCoreConstant.CONTROL_VALUE).asText());
							break;
						case GAIN_VALUE_CONTROL:
							value = Float.parseFloat(jsonValue.get(QSYSCoreConstant.CONTROL_VALUE).asText());
							value = (float) ((float) Math.ceil(value * 100)) / 100;
							stringValue = String.valueOf(value);
							String[] splitString = stringValue.split(QSYSCoreConstant.DOT);
							if (splitString.length == 1) {
								stringValue = stringValue + QSYSCoreConstant.ZERO + QSYSCoreConstant.ZERO;
							} else {
								if (splitString[1].length() < 2) {
									stringValue = stringValue + QSYSCoreConstant.ZERO;
								}
							}
							String[] splitProperty = updateLocalExtStatDto.getProperty().split(QSYSCoreConstant.HASH);
							localExtStat.getStatistics().put(splitProperty[0] + QSYSCoreConstant.HASH + GainControllingMetric.CURRENT_GAIN_VALUE.getMetric(), stringValue);
							break;
					}
				}
			}
		} catch (Exception e) {
			if (updateLocalExtStatDto.getControllingMetric() == GainControllingMetric.GAIN_VALUE_CONTROL) {
				String[] splitString = stringValue.split(QSYSCoreConstant.DOT);
				if (splitString.length == 1) {
					stringValue = stringValue + QSYSCoreConstant.ZERO + QSYSCoreConstant.ZERO;
				} else {
					if (splitString[1].length() < 2) {
						stringValue = stringValue + QSYSCoreConstant.ZERO;
					}
				}
				String[] splitProperty = updateLocalExtStatDto.getProperty().split(QSYSCoreConstant.HASH);
				if (splitProperty.length > 1) {
					localExtStat.getStatistics().put(splitProperty[0] + QSYSCoreConstant.HASH + GainControllingMetric.CURRENT_GAIN_VALUE.getMetric(), stringValue);
				}
			}
		}

		// Gain or Mute
		localExtStat.getStatistics().put(updateLocalExtStatDto.getProperty(), QSYSCoreConstant.EMPTY);

		float finalValue = value;
		localExtStat.getControllableProperties().stream()
				.filter(item -> Objects.equals(item.getName(), updateLocalExtStatDto.getProperty()))
				.forEach(item -> item.setValue(finalValue));
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
						throw new IllegalAccessException(QSYSCoreConstant.GETTING_TOKEN_ERR);
					}
				} else {
					throw new IllegalAccessException(QSYSCoreConstant.GETTING_TOKEN_ERR);
				}
			}
		} catch (Exception e) {
			this.loginInfo.setToken(null);
			throw new ResourceNotReachableException("Error when login", e);
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

		return QSYSCoreConstant.HTTP + getHost() + path;
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
				if (response.size() > 1) {
					JsonNode deviceControlResponse = objectMapper.readValue(response.get(1), JsonNode.class);
					deviceMap.get(deviceId).monitoringDevice(deviceControlResponse);
				}
			} catch (Exception e) {
				logger.error("Can not retrieve information of aggregated device have id is " + deviceId);
				priorityIdDeivceQueue.add(deviceId);
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
	private void gainControl(String metricName, String namedComponent, String value, String property) {
		RpcMethod method = RpcMethod.SET_CONTROLS;
		String request = String.format(RpcMethod.getRequest(), method.getName(), RpcMethod.getParamsString(method));
		request = String.format(request, namedComponent, GainControllingMetric.getByMetric(metricName).getProperty(), value);
		try {
			List<String> response = Arrays.asList(qrcCommunicator.send(request));
			if (response.size() > 1) {
				JsonNode responseControl = objectMapper.readValue(response.get(1), JsonNode.class);

				if ((!responseControl.has(QSYSCoreConstant.RESULT) || !responseControl.get(QSYSCoreConstant.RESULT).asText().equals(QSYSCoreConstant.TRUE)) && this.logger.isDebugEnabled()) {
					throw new IllegalStateException("Error: cannot set gain value of component " + namedComponent);
				}
			}
			if (localExtStat != null) {
				updateLocalExtStatDto = new UpdateLocalExtStat(property, value, namedComponent, GainControllingMetric.getByMetric(metricName));
				isEmergencyDelivery = true;
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
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			return dateFormat.format(date);
		} catch (Exception e) {
			logger.debug("Error when convert milliseconds to datetime");
		}
		return QSYSCoreConstant.DEFAUL_DATA;
	}

	/**
	 * Update filterDeviceTypeSet
	 */
	private void updateFilterDeviceTypeSet() {
		Set<String> stringSet = convertUserInput(filterDeviceType);
		filterDeviceTypeSet = new HashSet<>();
		for (String type : stringSet) {
			switch (type) {
				case QSYSCoreConstant.PROCESSOR_TYPE:
					filterDeviceTypeSet.add(QSYSCoreConstant.PROCESSOR_DEVICE);
					break;
				case QSYSCoreConstant.DISPLAY_TYPE:
					filterDeviceTypeSet.add(QSYSCoreConstant.DISPLAY_DEVICE);
					break;
				case QSYSCoreConstant.STREAM_IO_TYPE:
					filterDeviceTypeSet.add(QSYSCoreConstant.STREAM_INPUT_DEVICE);
					filterDeviceTypeSet.add(QSYSCoreConstant.STREAM_OUTPUT_DEVICE);
					break;
				case QSYSCoreConstant.VIDEO_IO_TYPE:
					filterDeviceTypeSet.add(QSYSCoreConstant.VIDEO_IO_DEVICE);
					break;
				case QSYSCoreConstant.VIDEO_SOURCE_TYPE:
					filterDeviceTypeSet.add(QSYSCoreConstant.VIDEO_SOURCE_DEVICE);
					break;
				case QSYSCoreConstant.CONTROL_INTERFACE_TYPE:
					filterDeviceTypeSet.add(QSYSCoreConstant.CONTROL_INTERFACE_DEVICE);
					break;
				default:
					logger.error("Type " + type + " does not exist");
			}
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
					setAdapterPropertiesElement.add(adapterPropertyElement.trim());
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
	 * Retrieve information of all device
	 *
	 * @param currentSizeDeviceMap Current size of cachedClients List, this param use to check number of clients was change or not
	 */
	private void populateAggregatedMonitoringData(int currentSizeDeviceMap) {
		if (executorService == null || currentSizeDeviceMap != deviceMap.size()) {
			executorService = Executors.newFixedThreadPool(deviceStatisticsCollectionThreads);
		}

		if (nextDeviceId != null && !deviceMap.containsKey(nextDeviceId)) {
			nextDeviceId = deviceMap.ceilingKey(nextDeviceId);
		}

		if (nextDeviceId == null) {
			nextDeviceId = deviceMap.firstKey();
		}

		boolean checkLoopRunToDevicesNeedToUpdate = false;

		List<String> deviceIdsNeedToUpdate = new ArrayList<>();
		int threadNum = 0;

		while (!priorityIdDeivceQueue.isEmpty() && threadNum < deviceStatisticsCollectionThreads) {
			deviceIdsNeedToUpdate.add(priorityIdDeivceQueue.poll());
			if (deviceIdsNeedToUpdate.size() >= deviceStatisticsCollectionThreads) {
				List<String> finalDeviceIdsNeedToUpdate = new ArrayList<>(deviceIdsNeedToUpdate);

				Future future = executorService.submit(new DeviceLoader(finalDeviceIdsNeedToUpdate));
				deviceExecutionPool.add(future);

				deviceIdsNeedToUpdate.clear();
				++threadNum;
			}
		}

		for (String deviceId : deviceMap.keySet()) {
			if (threadNum >= QSYSCoreConstant.MAX_THREAD) {
				nextDeviceId = deviceId;
				break;
			}

			if (deviceId.equals(nextDeviceId)) {
				checkLoopRunToDevicesNeedToUpdate = true;
			}

			if (checkLoopRunToDevicesNeedToUpdate) {
				deviceIdsNeedToUpdate.add(deviceId);
				if (deviceIdsNeedToUpdate.size() >= deviceStatisticsCollectionThreads) {
					List<String> finalDeviceIdsNeedToUpdate = new ArrayList<>(deviceIdsNeedToUpdate);

					Future future = executorService.submit(new DeviceLoader(finalDeviceIdsNeedToUpdate));
					deviceExecutionPool.add(future);

					deviceIdsNeedToUpdate.clear();
					++threadNum;
					if (threadNum >= deviceStatisticsCollectionThreads) {
						nextDeviceId = null;
					}
				}
			}
		}

		if (!deviceIdsNeedToUpdate.isEmpty()) {
			List<String> finalDeviceIdsNeedToUpdate = new ArrayList<>(deviceIdsNeedToUpdate);

			Future future = executorService.submit(new DeviceLoader(finalDeviceIdsNeedToUpdate));
			deviceExecutionPool.add(future);

			deviceIdsNeedToUpdate.clear();
			nextDeviceId = null;
		}
	}
}