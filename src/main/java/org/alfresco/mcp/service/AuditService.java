package org.alfresco.mcp.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.alfresco.core.handler.AuditApi;
import org.alfresco.core.model.AuditApp;
import org.alfresco.core.model.AuditAppEntry;
import org.alfresco.core.model.AuditAppPaging;
import org.alfresco.core.model.AuditAppPagingList;
import org.alfresco.core.model.AuditEntry;
import org.alfresco.core.model.AuditEntryEntry;
import org.alfresco.core.model.AuditEntryPaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    public static final String RECEIVED_RESPONSE = "Received response: {}";

    private final AuditApi auditApi;

    public AuditService(AuditApi auditApi) {
        this.auditApi = auditApi;
    }


    @Tool(name = "get_audit_applications", description = "Get audit applications and their details from Alfresco Content Service")
    public Set<AuditApp> getAuditApps()
    {
        ResponseEntity<AuditAppPaging> response = auditApi.listAuditApps(0, 100, null);
        log.info("Requesting audit applications from Alfresco Content Service");

        if (!response.getStatusCode().is2xxSuccessful()) {
            return Collections.emptySet();
        } else {
            log.info(RECEIVED_RESPONSE, response);
        }

        AuditAppPaging auditAppPaging = response.getBody();
        if (auditAppPaging == null) {
            return Collections.emptySet();
        }

        AuditAppPagingList auditAppPagingList = auditAppPaging.getList();
        if (auditAppPagingList == null) {
            return Collections.emptySet();
        }

        List<AuditAppEntry> entries = auditAppPagingList.getEntries();
        if (entries == null || entries.isEmpty()) {
            return Collections.emptySet();
        }

        return extractAuditApps(entries);
    }


    private Set<AuditApp> extractAuditApps(List<AuditAppEntry> entries) {
        return entries.stream()
                .map(AuditAppEntry::getEntry)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    @Tool(name = "get_audit_entries", description = "Get the last 100 audit entries for a specific audit application")
    public Set<AuditEntry> getAuditEntries(
    @ToolParam(description = "The ID of the audit application. An ID is a string value." + 
        " The default audit apps for Alfresco are alfresco-access, tagging, and CMISChangeLog."+
        " You can also create custom audit applications in Alfresco.")
        String appId) 
    {
        return getXAuditEntries(appId, 100);
    }


    @Tool(name = "get_x_audit_entries", description = "Get the x number of audit entries for a specific audit application."+
        " This is useful for retrieving a specific number of audit entries, such as the last 100 entries. " + 
        " Or you can specify a limit to retrieve a smaller number of entries. " + 
        " This is useful for pagination or when you only need a subset of the audit entries. " +
        " You can also use this to retrieve the latest entries for a specific audit application. Or all entries for a specific audit application.")
    public Set<AuditEntry> getXAuditEntries(
        @ToolParam(description = "The ID of the audit application. An ID is a string value. The default audit apps for Alfresco are alfresco-access, "+
        " tagging, and CMISChangeLog. You can also create custom audit applications in Alfresco.")
        String appId, 
        @ToolParam(description = "The maximum number of audit entries to retrieve. This is a positive integer.")
        int limit) 
    {
        return getXAuditEntriesWhereY(appId, limit, null);
    }


    @Tool(name = "get_x_audit_entries_where_y", description = "Get the x number of audit entries for a specific audit application where y is a specific condition is meet.")
    public Set<AuditEntry> getXAuditEntriesWhereY(
        @ToolParam(description = "The ID of the audit application. An ID is a string value. The default audit apps for Alfresco are alfresco-access, tagging, and CMISChangeLog."+
        " You can also create custom audit applications in Alfresco.") 
        String appId,
        @ToolParam(description = "The maximum number of audit entries to retrieve. This is a positive integer.") 
        int limit,
        @ToolParam(description = "The condition to filter audit entries by. This is a string value. The format is a where/filter statement between two parentheses."+
        " Here are examples of queries that can be done (createdByUser='jbloggs') (id BETWEEN ('1234', '4321')) "+
        " (createdAt BETWEEN ('2017-06-02T12:13:51.593+01:00' , '2017-06-04T10:05:16.536+01:00')) "+
        " (createdByUser='jbloggs' and createdAt BETWEEN ('2017-06-02T12:13:51.593+01:00' , '2017-06-04T10:05:16.536+01:00')) (valuesKey='/alfresco-access/login/user') "+
        " (valuesKey='/alfresco-access/transaction/action' and valuesValue='DELETE'). This operator is not allowed OR. "+
        " Here is an example of an invalid where statement: (createdByUser='chim-chim' OR createdByUser='admin')). " +
        " Another invalid statement would be (createdByUser='chim-chim') AND (createdByUser='admin')). This would return a result but it only evaluates the first condition and ignores the second one.")
        String where)
    {
        ResponseEntity<AuditEntryPaging> response = auditApi.listAuditEntriesForAuditApp(appId, 0, Boolean.FALSE,
                Collections.emptyList(), limit, where, List.of("values"), Collections.emptyList());
        log.info("Requesting {} audit entries for application: {}", limit, appId);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return Collections.emptySet();
        } else {
            log.info(RECEIVED_RESPONSE, response);
        }

        AuditEntryPaging auditEntryPaging = response.getBody();
        if (auditEntryPaging == null) {
            return Collections.emptySet();
        }

        List<AuditEntryEntry> entries = auditEntryPaging.getList().getEntries();
        if (entries == null || entries.isEmpty()) {
            return Collections.emptySet();
        }

        return extractAuditEntries(entries);
    }


    private Set<AuditEntry> extractAuditEntries(List<AuditEntryEntry> entries) {
        return entries.stream()
                .map(AuditEntryEntry::getEntry)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

    }


    @Tool(name = "get_audit_entries_for_specific_node", description = "Get 100 audit entries for a specific node")
    public Set<AuditEntry> getAuditEntriesForSpecificNode(
            @ToolParam(description = "The ID of the node to retrieve audit entries for. This is a string value. The node ID is a unique identifier for a node in Alfresco Content Service. "+
            "It follows the format of a UUID, such as '12345678-1234-1234-1234-123456789012'.")
            String nodeId)
    {
        return getXAuditEntriesForSpecificNode(nodeId, 100);
    }


     @Tool(name = "get_x_audit_entries_for_specific_node", description = "Get x number audit entries for a specific node")
    public Set<AuditEntry> getXAuditEntriesForSpecificNode(
            @ToolParam(description = "The ID of the node to retrieve audit entries for. This is a string value. The node ID is a unique identifier for a node in Alfresco Content Service. "+
            " It follows the format of a UUID, such as '12345678-1234-1234-1234-123456789012'.")
            String nodeId,
            @ToolParam(description = "The number of audit entries to retrieve. This is a positive integer.")
            int limit) 
    {
        ResponseEntity<AuditEntryPaging> response = auditApi.listAuditEntriesForNode(nodeId, 0, null,
                limit, null, List.of("values"), Collections.emptyList());
        log.info("Requesting audit entries for node: {}", nodeId);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return Collections.emptySet();
        } else {
            log.info(RECEIVED_RESPONSE, response);
        }

        AuditEntryPaging auditEntryPaging = response.getBody();
        if (auditEntryPaging == null) {
            return Collections.emptySet();
        }

        List<AuditEntryEntry> entries = auditEntryPaging.getList().getEntries();
        if (entries == null || entries.isEmpty()) {
            return Collections.emptySet();
        }

        return extractAuditEntries(entries);
    }
}
