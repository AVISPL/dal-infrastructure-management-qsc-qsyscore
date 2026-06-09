# Q-SYS Core Integration - Capabilities & Configuration
This document covers Q-SYS Core Aggregator Capabilities and Configuration.

Note: Not to be confused with Q-SYS Reflect Aggregator.

Symphony integrates with Q-SYS Core processors to provide comprehensive monitoring and control of Q-SYS environments. The adapter communicates directly with Q-SYS Core devices and exposes monitoring, control, aggregated device management, and peripheral visibility through Symphony.

Main features are: real-time Q-SYS Core monitoring, aggregated QSC and third-party peripheral monitoring, gain component control, redundancy monitoring, streaming and networked AV visibility, and device inventory management.

## Main use cases for Q-SYS Core Integration
- **Monitor** Q-SYS Core health, processor status, network state, redundancy state, and device metrics
- **Track** QSC and non-QSC aggregated peripherals including amplifiers, displays, cameras, streaming I/O, and video devices
- **Control** Gain component values, mute states, bypass states, and inversion controls
- **Inventory** Q-SYS ecosystem devices and associated peripheral components
- **Visualize** historical metrics such as temperature, memory usage, and impedance graphs

## Q-SYS Core Connection Setup and Device Provisioning

### Connection Setup

Note: The connection configuration below describe a successful Q-SYS Core integration setup, these should not be confused with the adapter configuration properties. They are not to be infered as troubleshooting checks and should not be used when diagnosing specific errors unless a troubleshooting entry (provided in the Troubleshooting section) explicitly references them.

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

### Q-SYS Core Adapter configuration properties - For filtering Device(s) and component(s)
The Q-SYS Core Aggregator supports optional filtering and configuration properties to tune monitoring behavior.

| Property | Description |
| --- | --- |
| filterGainComponentByName | The list of gain component names. Default: blank (shows NO gain components). |
| filterDeviceByName | The list of aggregated devices. Default: blank (shows all devices). |
| filterDeviceByQSYSType | The list of Q-SYS device types. |
| filterPluginByName | The list of plugin names.Default: blank (shows NO plugins). |
| qrcPort | TCP/IP port used to send QRC commands. Default: 1710.|
| historicalProperties | These properties vary by Device Type: Processor: ProcessorTemperature(C), SystemTemperature(C); Control Interface: MemoryUsage(%);Video I/O: CPUTemperature(C), I/OTemperature(C), VPUTemperature(C); Loudspeaker: HighPilotImpedance(Ohm), LowPilotImpedance(Ohm), Impedance(Ohm). Default: blank (shows all devices) |

For detailed information on aggregator and its configuration, please refer to our knowledgebase -> https://symphony.knowledgeowl.com/help/q-sys-core-aggregator-technical-breakdown

## Available Monitored Data for Q-SYS Core Integration

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

## Q-SYS Core - Redundancy mode 
- Q-SYS is capable of several redundant configurations to ensure a high level of overall system reliability.
- A second or Backup Core can be paired with the Primary Core in an installation. Initially, the Primary Core is the Active Core, and the Backup Core is the Standby Core.
- Only the Active Core has a separate group showing redundancy configuration.

## Troubleshooting for Q-SYS Core Integration

**Troubleshooting guidance**
- If an error occurs, focus only on troubleshooting steps that are provided in the section below.
- Do not include prerequisite/setup information.
- Do not add unrelated configuration details from other sections.
- If the document does not provide a direct error troubleshooting step, state that the document does not contain enough guidance for that specific issue.

**Login Error**
- Verify Q-SYS Core IP address and HTTPS accessibility
- Confirm username/password if access control is enabled
- Ensure port 443 is reachable

**API Error**
- Check API error description
- Verify Q-SYS Core IP address
- Ensure port 443 is reachable
- Verify that device and component names do not contain unsupported characters: ! @ % ^ & # ~ \ ' <? </. Names containing these characters will cause Symphony to return an API error.

**Device Discovery Issues**
- Verify the Q-SYS design is running properly
- Confirm Script Access is set to All or External for monitored components
- Verify filtering properties are not excluding expected devices

If none of the recommended steps help, please enter an SOS ticket at {https://avi-spl.atlassian.net/servicedesk/customer/portals}

## What AI Assistant can do with the Q-SYS Core Integration:
- Find Q-SYS Aggregated Devices monitored by Q-SYS Core Aggregator
- Verify Q-SYS Core Aggregator configuration
- Identify QSC and supported third-party peripheral devices

## What AI Assistant cannot do with the Q-SYS Core Integration:
- Provision devices automatically
- Create or modify Q-SYS Designer configurations
- Configure Q-SYS Core redundancy modes
- Automatically enable Script Access properties in Q-SYS Designer
