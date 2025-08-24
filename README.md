# Alfresco MCP

## Overview
Alfresco MCP (Model Context Protocol) Proof of Concept is a Spring Boot-based service for interacting with Alfresco Content Services. It provides tool-annotated APIs for audit, discovery, node management, probing, and querying, making it easy to automate and integrate with Alfresco repositories.

## Features
- Audit Service: Retrieve and analyze audit entries and applications.
- Discovery Service: Get repository version, modules, and status.
- Node Service: Manage and query nodes by ID.
- Probe Service: Check Alfresco health and readiness.
- Query Service: Advanced queries for nodes and audit data.
- Standardized response objects for all tool calls.

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Alfresco Content Services (Enterprise or Community)

### Build & Run

```sh
mvn clean package
```

### Configuration
Edit `src/main/resources/application.properties` to set Alfresco endpoint and credentials.

## Usage

The generated MCP server is using stdio for communication.  Configure your client application according to its documented configuration guidelines to communicate with it.  For example, if you are enabling the VSCode to use the Alfresco MCP server it would look similar to the following:

```json
"alfresco-mcp": {
    "type": "stdio",
    "command": "/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home/bin/java",
    "args": [
        "-Xms512M",
        "-Xmx2G",
        "-jar",
        "alfresco-mcp-0.0.1-SNAPSHOT.jar",
        "--port",
        "8081",
        "--host",
        "localhost"
    ],
    "cwd": "/Users/jottley/communityLive/alfresco-mcp/target"
}
```

### Example Tool Call

```java
@Tool(
	name = "get_alfresco_discovery_info",
	description = "Retrieves the capabilities and detailed version information from the Alfresco Content Repository."
)
public OperationResponse<Object> getDiscoveryInfo() { ... }
```

## Contributing
Pull requests are welcome! Please follow the existing code style and add tests for new features.

## License
See `LICENSE` for details.

## Support
For issues, open a GitHub issue or contact the maintainer.
