package com.refrio.platform.operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.refrio.platform.shared.infrastructure.persistence.ResourceStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** REST-level happy/unhappy paths, including role separation and transactional inventory. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ShipmentRestIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ResourceStore resources;
 @Autowired ObjectMapper mapper;
 @Autowired JdbcTemplate jdbc;
 private String token(String role) throws Exception {
  String username="r"+UUID.randomUUID().toString().substring(0,12);
  String registration=mapper.createObjectNode().put("username",username).put("email",username+"@example.org")
   .put("password","Str0ng-passw0rd!").put("firstName","Ana").put("lastName","Rios")
   .put("role",role).put("subscriptionPlan","Basico").put("sede","Lima").toString();
  mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(registration))
    .andExpect(status().isCreated());
  String login=mapper.createObjectNode().put("username",username).put("password","Str0ng-passw0rd!").toString();
  String response=mvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON).content(login))
    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  return mapper.readTree(response).path("token").asText();
 }
 @Test void shipmentTransitionsAndProtectedOperationsAreAvailableThroughTypedREST() throws Exception {
  jdbc.update("MERGE INTO operation_mutex (id) KEY(id) VALUES (1)");
  String sup=token("Supervisor");String minor=token("Minorista");
  String retailProfile=mvc.perform(get("/api/session").header("Authorization","Bearer "+minor))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  String retailId=mapper.readTree(retailProfile).path("id").asText();
  String prefix=UUID.randomUUID().toString().substring(0,8);
  String product="PRD-T-"+prefix, batch="BT-T-"+prefix, client="CLI-T-"+prefix;
  resources.put("inventory",(com.fasterxml.jackson.databind.node.ObjectNode)mapper.readTree("{\"id\":\""+product+"\",\"name\":\"Yogurt\",\"category\":\"Dairy\",\"stock\":\"12\",\"temp\":\"4°C\",\"exp\":\"2099-01-01\",\"warehouse\":\"Lima\"}"));
  resources.put("batches",(com.fasterxml.jackson.databind.node.ObjectNode)mapper.readTree("{\"id\":\""+batch+"\",\"productId\":\""+product+"\",\"quantity\":12,\"expirationDate\":\"2099-01-01\",\"receptionDate\":\"2026-11-10\",\"status\":\"Disponible\",\"audience\":\"Supervisor\"}"));
  // A supervisor may create a client linked to a recipient. The REST test also checks forbidden writes.
  resources.put("clients",(com.fasterxml.jackson.databind.node.ObjectNode)mapper.readTree("{\"id\":\""+client+"\",\"name\":\"Bodega\",\"recipientId\":\""+retailId+"\"}"));
  String body="{\"clientId\":\""+client+"\",\"origin\":\"Lima\",\"destination\":\"Surco\",\"driver\":\"Juan\",\"vehicle\":\"ABC-123\",\"eta\":\"2026-11-12T12:00:00\",\"lines\":[{\"productId\":\""+product+"\",\"quantity\":4}],\"scanned\":[\""+batch+"\"]}";
  mvc.perform(post("/api/v1/operations/shipments").contentType(MediaType.APPLICATION_JSON).content(body))
     .andExpect(status().isUnauthorized());
  mvc.perform(post("/api/v1/operations/shipments").header("Authorization","Bearer "+minor).contentType(MediaType.APPLICATION_JSON).content(body))
     .andExpect(status().isForbidden());
  String response=mvc.perform(post("/api/v1/operations/shipments").header("Authorization","Bearer "+sup).contentType(MediaType.APPLICATION_JSON).content(body))
     .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("Preparing"))
     .andReturn().getResponse().getContentAsString();
  String shipmentId=mapper.readTree(response).path("id").asText();
  assertFalse(shipmentId.isBlank());
  assertEquals(8,resources.get("batches",batch).path("quantity").asInt());
  mvc.perform(post("/api/v1/operations/shipments/"+shipmentId+"/advance").header("Authorization","Bearer "+sup))
     .andExpect(status().isOk());
  mvc.perform(post("/api/v1/operations/shipments/"+shipmentId+"/advance").header("Authorization","Bearer "+sup))
     .andExpect(status().isOk());
  mvc.perform(get("/api/v1/operations/shipments/"+shipmentId).header("Authorization","Bearer "+sup))
     .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Awaiting Receipt"));
 }
 @Test void invalidGherkinLikeRequestGivesLocalizedValidationMessage() throws Exception {
  mvc.perform(post("/api/leads").header("Accept-Language","en-US")
    .contentType(MediaType.APPLICATION_JSON).content("{\"companyName\":\"X\",\"contactName\":\"Y\",\"email\":\"invalid\"}"))
     .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("REQUEST_VALIDATION"))
     .andExpect(jsonPath("$.message").value("The request contains invalid or missing fields."));
  mvc.perform(post("/api/leads").header("Accept-Language","es-419")
    .contentType(MediaType.APPLICATION_JSON).content("{\"companyName\":\"X\",\"contactName\":\"Y\",\"email\":\"invalid\"}"))
     .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("La solicitud contiene campos faltantes o inválidos."));
 }
}
