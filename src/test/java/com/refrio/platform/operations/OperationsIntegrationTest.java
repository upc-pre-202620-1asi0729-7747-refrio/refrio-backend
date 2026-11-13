package com.refrio.platform.operations;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;
import com.refrio.platform.operations.application.OperationsApplicationService;
import com.refrio.platform.shared.infrastructure.persistence.ResourceStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @ActiveProfiles("test") @Transactional
class OperationsIntegrationTest {
 @Autowired private OperationsApplicationService ops;
 @Autowired private ResourceStore store;
 @Autowired private ObjectMapper mapper;
 @Autowired private JdbcTemplate jdbc;
 @Autowired private UserAccountRepository userAccounts;
 @Autowired private PasswordEncoder passwordEncoder;
 private final AuthenticatedUser supervisor=new AuthenticatedUser(1L,"admin","Supervisor");
 private AuthenticatedUser retailer;
 @BeforeEach void arrange() throws Exception {
  jdbc.update("MERGE INTO operation_mutex (id) KEY(id) VALUES (1)");
  String name="bodega-"+UUID.randomUUID().toString().substring(0,8);
  var account=userAccounts.save(new UserAccount(null,name,name+"@example.org",passwordEncoder.encode("secure-pass-123"),
      "Bodeguero","Demo","Minorista","Basico","Lima"));
  retailer=new AuthenticatedUser(account.id(),name,"Minorista");
  ObjectNode product=(ObjectNode)mapper.readTree(""" 
    {"id":"TEST-P1","name":"Leche","category":"Dairy","stock":"20","temp":"4°C","exp":"2099-12-31","warehouse":"Lima"}
    """);store.put("inventory",product);
  ObjectNode batch=(ObjectNode)mapper.readTree(""" 
    {"id":"TEST-B1","productId":"TEST-P1","quantity":20,"expirationDate":"2099-12-31","receptionDate":"2026-11-09","status":"Disponible","audience":"Supervisor"}
    """);store.put("batches",batch);
  store.put("clients",(ObjectNode)mapper.readTree("{\"id\":\"TEST-C1\",\"name\":\"Cliente\",\"recipientId\":\""+retailer.id()+"\"}"));
 }
 @Test void fullShipmentLifecyclePersistsAndPreservesQuantities(){
  ObjectNode draft=store.object();draft.put("clientId","TEST-C1");draft.put("origin","Lima");draft.put("destination","Surco");draft.put("driver","Juan");draft.put("vehicle","ABC-123");draft.put("eta","2026-11-10");
  ArrayNode lines=store.array();ObjectNode line=store.object();line.put("productId","TEST-P1");line.put("quantity",5);lines.add(line);draft.set("lines",lines);
  ArrayNode scanned=store.array().add("TEST-B1");draft.set("scanned",scanned);
  String id=ops.execute("createShipment",store.array().add(draft),supervisor).path("result").asText();
  assertEquals(15,store.get("batches","TEST-B1").path("quantity").asInt());
  ops.execute("advanceShipment",store.array().add(id),supervisor);
  ops.execute("advanceShipment",store.array().add(id),supervisor);
  ArrayNode received=store.array();received.add(line.deepCopy().put("batchId","TEST-B1"));
  ops.execute("receiveShipment",store.array().add(id).add("accepted").add(received).add(""),retailer);
  assertEquals("Delivered",store.get("shipments",id).path("status").asText());
  assertEquals("5",store.get("bodeguero-inventory","TEST-P1").path("stock").asText());
  assertThrows(RuntimeException.class,()->ops.execute("receiveShipment",store.array().add(id).add("accepted").add(received).add(""),retailer));
 }
 @Test void invalidScanDoesNotConsumeStock(){
  ObjectNode draft=store.object();draft.put("clientId","TEST-C1");draft.set("lines",store.array().add(store.object().put("productId","TEST-P1").put("quantity",5)));
  draft.set("scanned",store.array().add("WRONG"));
  assertThrows(RuntimeException.class,()->ops.execute("createShipment",store.array().add(draft),supervisor));
  assertEquals(20,store.get("batches","TEST-B1").path("quantity").asInt());
 }
}
