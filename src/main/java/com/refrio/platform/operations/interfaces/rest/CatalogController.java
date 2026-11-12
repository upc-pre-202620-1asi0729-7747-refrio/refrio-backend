package com.refrio.platform.operations.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.operations.application.OperationsApplicationService;
import com.refrio.platform.operations.domain.model.aggregates.*;
import com.refrio.platform.operations.domain.model.repositories.BusinessPartnerRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Tag(name="Operations")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api")
public class CatalogController {
 private final DocumentRepository store;private final UnitOfWorkLock mutex;private final OperationsApplicationService operations;private final BusinessPartnerRepository partners;
 public CatalogController(DocumentRepository store,UnitOfWorkLock mutex,OperationsApplicationService operations,BusinessPartnerRepository partners){this.store=store;this.mutex=mutex;this.operations=operations;this.partners=partners;}
 @GetMapping("/clients") public ArrayNode clients(@AuthenticationPrincipal AuthenticatedUser user){return operations.visible("clients",store.all("clients"),user);}
 @GetMapping("/suppliers") public ArrayNode suppliers(@AuthenticationPrincipal AuthenticatedUser user){return operations.visible("suppliers",store.all("suppliers"),user);}
 @GetMapping("/shipments") public ArrayNode shipments(@AuthenticationPrincipal AuthenticatedUser user){return operations.visible("shipments",store.all("shipments"),user);}
 @PostMapping("/clients") @ResponseStatus(HttpStatus.CREATED) @Transactional public ObjectNode addClient(@RequestBody ObjectNode input,@AuthenticationPrincipal AuthenticatedUser user){return add("clients","CLI",input,user);}
 @PostMapping("/suppliers") @ResponseStatus(HttpStatus.CREATED) @Transactional public ObjectNode addSupplier(@RequestBody ObjectNode input,@AuthenticationPrincipal AuthenticatedUser user){return add("suppliers","SUP",input,user);}
 private ObjectNode add(String resource,String prefix,ObjectNode input,AuthenticatedUser user){
  if(!user.supervisor())throw new ForbiddenOperationException("Sólo Supervisor puede crear registros comerciales");
  mutex.acquire();if(input.path("name").asText("").isBlank())throw new DomainException("El nombre es obligatorio");
  ObjectNode entry=input.deepCopy();String id=prefix+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();entry.put("id",id);
  if("clients".equals(resource))partners.save(new ClientAggregate(id,entry.path("name").asText(),
      entry.path("type").asText(null),entry.path("province").asText(null),entry.path("district").asText(null),
      entry.path("recipientId").asText(null),entry.has("orders")?entry.path("orders").asInt():null));
  else partners.save(new SupplierAggregate(id,entry.path("name").asText(),entry.path("category").asText(null),
      entry.has("deliveries")?entry.path("deliveries").asInt():null,
      entry.has("score")?entry.path("score").asInt():null,entry.path("rating").asText(null)));
  return store.get(resource,id);
 }
}
