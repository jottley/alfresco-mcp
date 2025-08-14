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

package org.alfresco.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.alfresco.mcp.service.AuditService;
import org.alfresco.mcp.service.NodeService;
import org.alfresco.mcp.service.ProbeService;
import org.alfresco.mcp.service.QueryService;

@SpringBootApplication
public class AlfrescoMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlfrescoMcpApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider auditSerivceTools(AuditService auditService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(auditService).build();
    }

    @Bean
    public ToolCallbackProvider queryServiceTools(QueryService queryService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(queryService).build();
    }

    @Bean
    public ToolCallbackProvider probeServiceTools(ProbeService probeService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(probeService).build();
    }

    @Bean
    public ToolCallbackProvider nodeServiceTools(NodeService nodeService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(nodeService).build();
    }

}
