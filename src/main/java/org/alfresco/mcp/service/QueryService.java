package org.alfresco.mcp.service;

import java.util.List;

import org.alfresco.core.handler.QueriesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeEntry;
import org.alfresco.core.model.NodePaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    private static final Logger log = LoggerFactory.getLogger(QueryService.class);

    private final QueriesApi queriesApi;

    public QueryService(QueriesApi queriesApi) {
        this.queriesApi = queriesApi;
    }

    @Tool(name = "get_node_id_for_file", description = "Get the node ID for a specific file in Alfresco Content Service")
    public String getNodeIdForSpecificFile(String fileName) {
        // Pass empty lists for include, orderBy, and fields to avoid invalid query errors
        ResponseEntity<NodePaging> response = queriesApi.findNodes(
                fileName,
                "-root-",
                0,
                100,
                "cm:content",
                null, // include
                null, // orderBy
                List.of("id")  // fields
        );
        log.info("Requesting node ID for file: {}", fileName);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to retrieve node ID for file: {}", fileName);
            return "Error retrieving node ID";
        }

        NodePaging nodePaging = response.getBody();
        if (nodePaging == null || nodePaging.getList() == null || nodePaging.getList().getEntries() == null) {
            log.error("NodePaging or its entries are null for file: {}", fileName);
            return "Node ID not found";
        }

        log.info("Node ID for file {}: {}", fileName, nodePaging);
        return nodePaging.getList().getEntries().stream()
                .findFirst()
                .map(NodeEntry::getEntry)
                .map(Node::getId)
                .orElse("Node ID not found");
    }


    @Tool(name = "all_the_files_with_the_same_name", description = "Get all the files with the same name in Alfresco Content Service")
    public List<Node> getAllTheFilesWithTheSameName(String fileName) {
        // Pass empty lists for include, orderBy, and fields to avoid invalid query errors
        ResponseEntity<NodePaging> response = queriesApi.findNodes(
                fileName,
                "-root-",
                0,
                100,
                "cm:content",
                null, // include
                null, // orderBy
                null  // fields
        );
        log.info("Requesting nodes for file: {}", fileName);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to retrieve nodes for file: {}", fileName);
            return List.of(new Node().id("Error retrieving nodes"));
        }

        NodePaging nodePaging = response.getBody();
        if (nodePaging == null || nodePaging.getList() == null || nodePaging.getList().getEntries() == null) {
            log.error("NodePaging or its entries are null for file: {}", fileName);
            return List.of(new Node().id("Nodes not found"));
        }

        log.info("Nodes for the file {}: {}", fileName, nodePaging);
        return nodePaging.getList().getEntries().stream()
                .map(NodeEntry::getEntry)
                .toList();
    }

}
