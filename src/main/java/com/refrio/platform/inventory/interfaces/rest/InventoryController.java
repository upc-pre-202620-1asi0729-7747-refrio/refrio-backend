package com.refrio.platform.inventory.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.shared.infrastructure.persistence.ResourceStore;
import com.refrio.platform.operations.application.OperationsApplicationService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name="Inventory")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api")
public class InventoryController {
 private final ResourceStore store;private final OperationsApplicationService ops;
 public InventoryController(ResourceStore store,OperationsApplicationService ops){this.store=store;this.ops=ops;}
 @GetMapping("/inventory") public ArrayNode inventory(@AuthenticationPrincipal AuthenticatedUser user){return ops.visible("inventory",store.all("inventory"),user);}
 @GetMapping("/bodeguero-inventory") public ArrayNode retail(@AuthenticationPrincipal AuthenticatedUser user){return ops.visible("bodeguero-inventory",store.all("bodeguero-inventory"),user);}
 @GetMapping("/batches") public ArrayNode batches(@AuthenticationPrincipal AuthenticatedUser user){return ops.visible("batches",store.all("batches"),user);}
 @GetMapping("/telemetry") public ArrayNode telemetry(@AuthenticationPrincipal AuthenticatedUser user){return ops.visible("telemetry",store.all("telemetry"),user);}
 @GetMapping("/losses") public ArrayNode losses(@AuthenticationPrincipal AuthenticatedUser user){return ops.visible("losses",store.all("losses"),user);}
}
