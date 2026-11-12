package com.refrio.platform.shared.infrastructure.persistence;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class ResourceStore implements com.refrio.platform.shared.application.compatibility.DocumentRepository {
  private final ResourceJpaRepository records;
  private final ObjectMapper mapper;
  public ResourceStore(ResourceJpaRepository records,ObjectMapper mapper){this.records=records;this.mapper=mapper;}
  public ArrayNode all(String resource){ArrayNode result=mapper.createArrayNode(); records.findByResourceOrderByIdAsc(resource).forEach(x->result.add(read(x.getPayload())));return result;}
  public ObjectNode get(String resource,String id){return records.findByResourceAndExternalId(resource,id).map(x->read(x.getPayload())).orElseThrow(()->new ResourceNotFoundException("No existe " + resource + " con ID " +id));}
  public Optional<ObjectNode> find(String resource,String id){return records.findByResourceAndExternalId(resource,id).map(x->read(x.getPayload()));}
  public void put(String resource,ObjectNode value){String id=value.path("id").asText("");if(id.isBlank())throw new DomainException("El identificador es obligatorio");
    ResourceRecord record=records.findByResourceAndExternalId(resource,id).orElseGet(()->new ResourceRecord(resource,id,"{}"));
    try{record.setPayload(mapper.writeValueAsString(value));}catch(Exception e){throw new IllegalStateException(e);}
    records.saveAndFlush(record);
  }
  public void delete(String resource,String id){records.findByResourceAndExternalId(resource,id).ifPresent(records::delete);records.flush();}
  public ObjectNode object(){return mapper.createObjectNode();}
  public ArrayNode array(){return mapper.createArrayNode();}
  public ObjectNode copy(JsonNode value){if(!value.isObject())throw new DomainException("Se esperaba un objeto JSON");return ((ObjectNode)value).deepCopy();}
  public ObjectNode read(String json){try{return (ObjectNode)mapper.readTree(json);}catch(Exception e){throw new IllegalStateException("Invalid data in database",e);}}
}
