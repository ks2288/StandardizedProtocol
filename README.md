### Overview

This library provides a Kotlin implementation of a simple "standardized" 
protocol for sending and receiving packets between modules in multilayered 
systems such as JVM system process sand-boxing, RF communications such as 
BLE, serial/GPIO-based MCU interaction (such as a by-wire controller board 
for any one of myriad purposes), and other contexts wherein packets of a 
known format might benefit the mitigation of data integrity errors.

### WIP

Initial release of Kotlin JVM library - Standardized Protocol (SP) - for writing more-streamlined byte/array-based interprocess/inter-device communications that take place over signal-based generic data transfer protocols such as UART (wired and wireless), STDIO, and serial/GPIO.

SP is simply an idea that all packets should have a format, and that format should contain both a structured header and a payload of known but arbitrary type and contents. This is not a novel idea. Rather, this is SOP for highly-regulated industries wherein multilayered infrastructure (think by-wire controller boards that operate things like medical implants and cruise missiles, and multiple parts are working in cadence to achieve a singular goal - often with roles that are mutually exclusive to one another as they work) needs to have a known signal structure when multiple modules are communicating to and from a hypervisor. For the same reason we need a signal structure when we talk to one another, and it's also the same fundamental reason that your brain needs to have a rulebook it can communicate while its policing the carbon jungle that is your body and its myriad systems.

Spoken words that don't carry a meaning that the other can recognize are just sounds. Information without context isn't information; it's just useless data. So we create implementations based on ideas like this that give the modules/processes/devices of our multilayered systems some kind of dictionary for their communications between each another in a proprietary system.

Core uses for this library are JVM subprocess sandboxing and scripting, such 
as ARM-based SoCs like the CM4 and its many derivatives (or literally any other host/modular host device that can run a JRE) that act as controller units for multitiered infrastructure (such as smart home implementations, sensor data collection/aggregation/processing, FOTA-like applications, and controlling modules whose code is written in multiple languages running on the same host machine).

Full README and accompanying ICD to be added with v1.2