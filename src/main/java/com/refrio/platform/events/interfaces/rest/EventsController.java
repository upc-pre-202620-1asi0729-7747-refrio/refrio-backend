package com.refrio.platform.events.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.operations.application.OperationsApplicationService;
import com.refrio.platform.shared.infrastructure.persistence.ResourceStore;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name="Events")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/events")
public class EventsController {
 private final ResourceStore store;private final OperationsApplicationService operations;
 public EventsController(ResourceStore store,OperationsApplicationService operations){this.store=store;this.operations=operations;}
 @GetMapping public ArrayNode list(@AuthenticationPrincipal AuthenticatedUser user){return operations.visible("events",store.all("events"),user);}
}
