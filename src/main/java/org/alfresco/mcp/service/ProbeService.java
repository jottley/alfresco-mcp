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

import org.alfresco.core.handler.ProbesApi;
import org.alfresco.core.model.ProbeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProbeService {

    public static final Logger log = LoggerFactory.getLogger(ProbeService.class);

    private final ProbesApi probeAPI;
    private static final String IS_LIVE = "-live-";
    private static final String IS_READY = "-ready-";

    public ProbeService(ProbesApi probeAPI) {
        this.probeAPI = probeAPI;
    }

    @Tool(name = "is_alfresco_live", description = "Check if Alfresco Content Service is up and running.")
    public String isAlfrescoLive() {
        ResponseEntity<ProbeEntry> response = probeAPI.getProbe(IS_LIVE);
        log.info("Checking if Alfresco Content Service is live");

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to check if Alfresco Content Service is live");
            if (response.getStatusCode().is4xxClientError()) {
                return "Alfresco Content Service is not live (Client Error)";
            } else if (response.getStatusCode().isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
                return "Alfresco Content Service is not live";
            } else {
                return "Alfresco Content Service is not live (Unexpected Error) " + response.getStatusCode();
            }
        }

        ProbeEntry probeEntry = response.getBody();
        if (probeEntry != null) {
            log.info("Alfresco Content Service is live: {}", probeEntry);
            return "Alfresco Content Service is live";
        } else {
            log.warn("Alfresco Content Service is live, but response body is empty");
            return "Alfresco Content Service is live (no details)";
        }
    }

    @Tool(name = "is_alfresco_ready", description = "Check if Alfresco Content Service is ready to accept requests.")
    public String isAlfrescoReady() {
        ResponseEntity<ProbeEntry> response = probeAPI.getProbe(IS_READY);
        log.info("Checking if Alfresco Content Service is ready");

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to check if Alfresco Content Service is ready");
            if (response.getStatusCode().is4xxClientError()) {
                return "Alfresco Content Service is not ready (Client Error)";
            } else if (response.getStatusCode().isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
                return "Alfresco Content Service is not ready";
            } else {
                return "Alfresco Content Service is not ready (Unexpected Error) " + response.getStatusCode();
            }
        }

        ProbeEntry probeEntry = response.getBody();
        if (probeEntry != null) {
            log.info("Alfresco Content Service is ready: {}", probeEntry);
            return "Alfresco Content Service is ready";
        } else {
            log.warn("Alfresco Content Service is ready, but response body is empty");
            return "Alfresco Content Service is ready (no details)";
        }
    }
}
