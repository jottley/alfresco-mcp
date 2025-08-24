/*
 * Copyright 2025 Jared Ottley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.mcp.service;

import java.util.List;
import org.alfresco.discovery.handler.DiscoveryApi;
import org.alfresco.discovery.model.DiscoveryEntry;
import org.alfresco.mcp.model.OperationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DiscoveryService {

  private static final Logger log = LoggerFactory.getLogger(DiscoveryService.class);

  public static final String RECEIVED_RESPONSE = "Received response: {}";

  private final DiscoveryApi discoveryApi;

  public DiscoveryService(DiscoveryApi discoveryApi) {
    this.discoveryApi = discoveryApi;
  }

  @Tool(
      name = "get_alfresco_discovery_info",
      description =
          "Retrieves the capabilities and detailed version information from the Alfresco Content Repository.")
  public OperationResponse<Object> getDiscoveryInfo() {
    log.info("Requesting discovery information from Alfresco Content Service");

    OperationResponse.Builder<Object> responseBuilder = OperationResponse.builder();

    try {
      ResponseEntity<DiscoveryEntry> response = discoveryApi.getRepositoryInformation();
      log.info(RECEIVED_RESPONSE, response);

      if (!response.getStatusCode().is2xxSuccessful()) {
        log.error("Failed to retrieve discovery information: {}", response.getStatusCode());
        return responseBuilder
            .success(false)
            .data(null)
            .messages(
                List.of("Error retrieving discovery information: " + response.getStatusCode()))
            .build();
      }

      DiscoveryEntry discoveryEntry = response.getBody();
      if (discoveryEntry == null
          || discoveryEntry.getEntry() == null
          || discoveryEntry.getEntry().getRepository() == null) {
        log.error(
            "Failed to retrieve discovery information: response body or repository info was null");
        return responseBuilder
            .success(false)
            .data(null)
            .messages(
                List.of(
                    "Error retrieving discovery information: response body or repository info was null"))
            .build();
      }

      return responseBuilder.success(true).data(discoveryEntry.getEntry().getRepository()).build();

    } catch (Exception e) {
      log.error("Failed to retrieve discovery information", e);
      return responseBuilder
          .success(false)
          .data(null)
          .messages(List.of("Error retrieving discovery information: " + e.getMessage()))
          .build();
    }
  }
}
