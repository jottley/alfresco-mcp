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
