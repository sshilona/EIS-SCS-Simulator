# Event Info Scheduler (EIS) Client Simulator

This Spring Boot Java project implements the EIS client, conforming to the EIS protocol version 3 as specified in [ETSI TS 103 197 V1.3.1](https://www.etsi.org/deliver/etsi_ts/103100_103199/103197/01.03.01_60/ts_103197v010301p.pdf). Designed for flexibility, it can be easily adjusted to support additional versions of the EIS protocol. The application features a web UI accessible on port 9090 and offers a REST API for integration and automation purposes.

## Features

- **EIS Protocol Version 3 Implementation:** Full implementation of the specified EIS protocol, ensuring compatibility and interoperability.
- **Web UI:** A user-friendly web interface available on port 9090, facilitating easy interaction with the EIS client.
- **REST API:** Comprehensive API support for integration with other systems and automation of tasks.
- **Flexible Protocol Support:** Designed with scalability in mind, allowing for straightforward updates to accommodate new versions of the EIS protocol.

## Setup

### Prerequisites

- **Java:** Ensure Java is installed on your system. Update the `java.version` in `pom.xml` if necessary.
- **Maven:** [Download Maven](https://maven.apache.org/download.cgi) and add it to your system's PATH. Maven is used for managing project dependencies and building the project.

### Installation

1. **Clone the Repository**


Replace `<repository-url>` with the URL of your project's repository.

2. **Build the Project**

Navigate to the project directory and run.

### UI Example
![eis](https://github.com/sshilona/EIS-SCS-Simulator/assets/163118477/39f19695-8755-40cd-8b65-141685f896d1)




https://github.com/sshilona/EIS-SCS-Simulator/assets/163118477/b181eb5e-1bd1-4f9d-bd81-4e04c6951a06



### REST API Interface
This project exposes several REST endpoints for managing clients, channels, and SCG (Service Component Group) provisions. Below are the details of these endpoints along with example request payloads.

**Create Client**
Endpoint: POST /eis/create-client
Description: Provisions a new client with the specified IP and port.
Example Request Payload:
```json
{
    "ip": "10.42.21.223",
    "port": 11000
}

**Channel Setup**
Endpoint: POST /eis/channel-setup
Description: Sets up a channel with the given ID.
Example Request Payload:
```json
{
    "channelId": 111
}

**Channel Close**
Endpoint: POST /eis/channel-close
Description: Closes the specified channel.
Example Request Payload:
```json
{
    "channelId": 111
}

**Channel Test**
Endpoint: POST /eis/channel-test
Description: Tests the specified channel.
Example Request Payload:
```json
{
    "channelId": 111
}

**Channel Reset**
Endpoint: POST /eis/channel-reset
Description: Resets the specified channel.
Example Request Payload:
```json
{
    "channelId": 111
}

**SCG Provision**
Endpoint: POST /eis/scg-provision
Description: Provisions an SCG with detailed configurations.
Example Request Payload:
```json
{
    "channelId": 111,
    "scgId": 2,
    "transportStreamId": 10,
    "originalNetworkId": 1,
    "scgReferenceId": 1463935,
    "recommendedCpDuration": 0,
    "serviceId": 1,
    "ecmGroups": [
        {
            "ecmId": 1001,
            "superCasId": "10000",
            "accessCriteria": "AABBCCDDEEFF",
            "acChangeFlag": true
        },
        {
            "ecmId": 1002,
            "superCasId": "10000",
            "accessCriteria": "112233445566",
            "acChangeFlag": false
        }
    ]
}

**SCG List**
Endpoint: POST /eis/scg-list
Description: Retrieves a list of SCGs based on the provided criteria.
Example Request Payload:
```json
{
    "channelId": 111
}

**SCG Test**
Endpoint: POST /eis/scg-test
Description: Tests the specified SCG provision.
Example Request Payload:
```json
{
    "channelId": 111,
    "scgId": 2
}
