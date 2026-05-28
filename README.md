# Q-SYS Core Integration - Capabilities & Configuration
This document covers Q-SYS Core Aggregator Capabilities and Configuration.

Symphony integrates with Q-SYS Core processors to provide comprehensive monitoring and control of Q-SYS environments. The adapter communicates directly with Q-SYS Core devices and exposes monitoring, control, aggregated device management, and peripheral visibility through Symphony.

Main features are: real-time Q-SYS Core monitoring, aggregated QSC and third-party peripheral monitoring, gain component control, redundancy monitoring, streaming and networked AV visibility, and device inventory management.

## Main use cases
- **Monitor** Q-SYS Core health, processor status, network state, redundancy state, and device metrics
- **Track** QSC and non-QSC aggregated peripherals including amplifiers, displays, cameras, streaming I/O, and video devices
- **Control** Gain component values, mute states, bypass states, and inversion controls
- **Inventory** Q-SYS ecosystem devices and associated peripheral components
- **Visualize** historical metrics such as temperature, memory usage, and impedance graphs

## Q-SYS Core Configuration and Device Provisioning

### Configuration

The Q-SYS Core Aggregator device must be configured in Symphony with the following values:

| Field | Description |
|---|---|
| Device Type | Infrastructure |
| Category | Management |
| Manufacturer | QSC |
| Model | Core (Monitoring Proxy) |
| Monitoring Service | Advanced Monitoring |
| Monitoring Source | Direct |
| Management Address | IP address of the Q-SYS Core processor |
| Protocol | HTTPS |
| Username | Q-SYS username |
| Password | Q-SYS password |
| Port Number | 443 |

### Device Provisioning

By default, the unprovisioned devices will appear on Aggregated Devices -> Unprovisioned Devices tab.

To import a Q-SYS aggregated device for monitoring by the Q-SYS Core Aggregator:
1. Open Aggregated Devices
2. Select unprovisioned devices
3. Fill required provisioning fields
4. Import devices into Symphony

Required provisioning values:
- Type
- Category
- Manufacturer
- Model

### Filtering Device(s) and Component(s)
The Q-SYS Core Aggregator supports optional filtering and configuration properties to tune monitoring behavior.

| Property | Description |
|---|---|
| filterGainComponentByName | List of gain component names |
| filterDeviceByName | List of aggregated device names |
| filterDeviceByQSYSType | List of QSYS device types |
| filterPluginByName | List of plugin names |
| qrcPort | TCP/IP port used for QRC commands | 1710 by default |
| historicalProperties | Enables graphing support for historical metrics. Enabled for supported device properties |

For detailed information on aggregator and its configuration, please refer to our knowledgebase -> https://symphony.knowledgeowl.com/help/q-sys-core-aggregator-technical-breakdown

## Available Monitored Data

Q-SYS Core monitored data consists of 2 main parts: Aggregator Adapter properties, Aggregated Device extended properties (QSC Peripheral device properties, and External/Non-QSC device properties).

### Aggregator Adapter

| Property Type | Description |
| --- | --- |
| Adapter Metadata | Adapter and platform details. |
| Networking Properties | Network configuration details. |
| Redundancy Properties | Redundancy and failover information. |
| Gain Component Controls | Gain component monitoring and controls. |

### Aggregated Devices
The adapter supports monitoring and control for multiple QSC and third-party peripheral device types.

#### QSC Peripheral devices

QSC peripherals with 13 types: Processor, Streaming I/O, Control Interface, Video Source, Display, Video I/O, Camera, External, Amplifier, Loudspeaker, Transmitter, Receiver, and Status Amplifier.

| Device Type | Description |
| --- | --- |
| Processor | Provides processor synchronization, status, clocking, fan speed, and temperature monitoring. |
| Streaming I/O | Provides device metadata, HDCP information, status, and channel monitoring. |
| Control Interface | Provides device metadata, screen control settings, status and system monitoring. |
| Video Source | Provides device metadata, status information and video format telemetry (For example: AspectRatio, ColorFormatStatus, HDMI Output, etc.) for video source devices. |
| Display | Provides device metadata, display information, EDID data, HDMI connectivity, HDCP information, and status information. |
| Video I/O | Provides device metadata, network synchronization details, networked audio/video statistics, hardware telemetry, PoE monitoring and status information. |
| Speaker | Provides device metadata, audio controls, status information, speaker monitoring, impedance, pilot tone, power, and protection telemetry. |
| Encoder / Decoder | Provides device metadata, status information, encoder configuration, HDMI statistics, LAN telemetry, power and PSE information, and system monitoring (AUX Power, Fan speed, CPU Temperature, PoE information). |
| AES67 Receiver | Provides device metadata, status information, AES67 receive channel monitoring and connection configuration. |
| AES67 Transmitter | Provides device metadata, status information, AES67 transmit channel monitoring and connection configuration. |
| Q-LAN Receiver & Transmitter | Provides device metadata, status information, channel monitoring and connection configuration. |
| Amplifier | Provides device metadata, status information, amplifier controls, power and thermal monitoring, channel monitoring, and power management. |
| Status Amplifier | Provides device metadata, temperature information, status information, audio stream monitoring and network synchronization. |

#### External Devices (Non-QSC Devices)

External devices can be linked using one of the following integration types:

1. **Monitoring Proxy Type**
   - Generic external monitoring proxy device integration (metadata and status information).

2. **Plugin Type**
   - Provides metadata, status, firmware, and network information.
   - Supported plugin devices:
     - Sennheiser TeamConnect Ceiling 2 
     - Samsung Commercial Display
     - Netgear AV Line Switch

**Note:** Status information are usually represented by the Status and StatusLED properties. Status values include OK, Initializing, Compromised, Missing, Fault, Unknown, and Not Present, while StatusLED behavior may either be a simple On/Off state or mirror the device status depending on the device type.

For more information related to supported devices and their monitorable properties, please refer to our knowledgebase -> https://symphony.knowledgeowl.com/help/q-sys-core-aggregator-technical-breakdown

## Redundancy mode 
- Q-SYS is capable of several redundant configurations to ensure a high level of overall system reliability.
- A second or Backup Core can be paired with the Primary Core in an installation. Initially, the Primary Core is the Active Core, and the Backup Core is the Standby Core.
- Only the Active Core has a separate group showing redundancy configuration.

## Troubleshooting
**Login Error**
- Verify Q-SYS Core IP address and HTTPS accessibility
- Confirm username/password if access control is enabled
- Ensure port 443 is reachable

**Device Discovery Issues**
- Verify the Q-SYS design is running properly
- Confirm Script Access is set to All or External for monitored components
- Verify filtering properties are not excluding expected devices

**Monitoring or Status Issues**
- Confirm Q-SYS Core firmware compatibility and Q-SYS Designer version compatibility
- Check device redundancy states and network connectivity
- Validate plugin and Monitoring Proxy configurations for third-party devices

If none of the recommended steps help, please enter an SOS ticket at {https://avi-spl.atlassian.net/servicedesk/customer/portals}

## What AI Assistant can do with it:
- Find Q-SYS Aggregated Devices monitored by Q-SYS Core Aggregator
- Verify Q-SYS Core Aggregator configuration
- Identify QSC and supported third-party peripheral devices

## What AI Assistant cannot do with it:
- Provision devices automatically
- Create or modify Q-SYS Designer configurations
- Configure Q-SYS Core redundancy modes
- Automatically enable Script Access properties in Q-SYS Designer
