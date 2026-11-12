package com.refrio.platform.shared.application.compatibility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import java.util.Optional;
public interface DocumentRepository {
 ArrayNode all(String resource);
 ObjectNode get(String resource,String id);
 Optional<ObjectNode> find(String resource,String id);
 void put(String resource,ObjectNode value);
 void delete(String resource,String id);
 ObjectNode object();
 ArrayNode array();
 ObjectNode copy(JsonNode value);
}
