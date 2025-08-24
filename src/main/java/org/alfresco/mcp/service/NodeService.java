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
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeEntry;
import org.alfresco.mcp.model.OperationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NodeService {

  private static final Logger log = LoggerFactory.getLogger(NodeService.class);

  private final NodesApi nodesApi;

  public NodeService(NodesApi nodesApi) {
    this.nodesApi = nodesApi;
  }

  @Tool(
      name = "get_node_entry_by_id",
      description = "Get the node entry for a specific nodeId in Alfresco Content Service")
  public OperationResponse<Object> getNodeEntryById(String nodeId) {
    try {
      ResponseEntity<NodeEntry> response = nodesApi.getNode(nodeId, null, null, null);
      if (!response.getStatusCode().is2xxSuccessful()) {
        log.error("Failed to retrieve node entry for nodeId: {}", nodeId);
        return OperationResponse.builder()
            .success(false)
            .data(null)
            .messages(List.of("Error retrieving node entry for nodeId: " + nodeId))
            .build();
      }

      NodeEntry nodeEntry = response.getBody();
      if (nodeEntry == null || nodeEntry.getEntry() == null) {
        log.error("NodeEntry or its entry is null for nodeId: {}", nodeId);
        return OperationResponse.builder()
            .success(false)
            .data(null)
            .messages(List.of("Node not found for nodeId: " + nodeId))
            .build();
      }

      log.info("Node entry for nodeId {}: {}", nodeId, nodeEntry.getEntry());
      return OperationResponse.builder().success(true).data(nodeEntry.getEntry()).build();
    } catch (Exception e) {
      log.error("Exception retrieving node entry for nodeId: {}", nodeId, e);
      return OperationResponse.builder()
          .success(false)
          .data(null)
          .messages(List.of("Exception retrieving node entry for nodeId: " + nodeId))
          .build();
    }
  }

  @Tool(
      name = "get_node_entries_by_ids",
      description = "Get the node entries for a list of nodeIds in Alfresco Content Service")
  public OperationResponse<Object> getNodeEntriesByIds(java.util.List<String> nodeIds) {
    List<Node> nodes = new java.util.ArrayList<>();
    for (String nodeId : nodeIds) {
      try {
        ResponseEntity<NodeEntry> response = nodesApi.getNode(nodeId, null, null, null);
        NodeEntry nodeEntry = response.getBody();
        if (response.getStatusCode().is2xxSuccessful()
            && nodeEntry != null
            && nodeEntry.getEntry() != null) {
          nodes.add(nodeEntry.getEntry());
        } else {
          log.error("Failed to retrieve node entry for nodeId: {}", nodeId);
          return OperationResponse.builder()
              .success(false)
              .data(null)
              .messages(List.of("Error retrieving node entry for nodeId: " + nodeId))
              .build();
        }
      } catch (Exception e) {
        log.error("Exception retrieving node entry for nodeId: {}", nodeId, e);
        nodes.add(new Node().id("Exception: " + nodeId));
      }
    }
    return OperationResponse.builder().success(true).data(nodes).build();
  }

  // TOOO
  public OperationResponse<Object> createNode(Node node) {
    try {

      NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
      nodeBodyCreate.setName(node.getName());
      nodeBodyCreate.setNodeType(node.getNodeType());
      nodeBodyCreate.setAspectNames(node.getAspectNames());

      ResponseEntity<NodeEntry> response =
          nodesApi.createNode(
              "-root-",
              null,
              Boolean.TRUE, // autoRename if a node with the same name exists
              Boolean.TRUE, // nodeType
              Boolean.TRUE, // aspectNames
              null, // relativePath
              null);
      if (!response.getStatusCode().is2xxSuccessful()) {
        log.error("Failed to create node: {}", node);
      } else {
        log.info("Node created successfully: {}", response.getBody());
      }
    } catch (Exception e) {
      log.error("Exception creating node: {}", node, e);
    }
    return null;
  }
}
