package com.refrio.platform.alerting.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.operations.application.OperationsApplicationService;
import com.refrio.platform.shared.infrastructure.persistence.ResourceStore;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name="Alerting")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api")
public class AlertingController {
 private final ResourceStore store;private final OperationsApplicationService operations;
 public AlertingController(ResourceStore store,OperationsApplicationService operations){this.store=store;this.operations=operations;}
 @GetMapping("/alerts") public ArrayNode alerts(@AuthenticationPrincipal AuthenticatedUser user){return operations.visible("alerts",store.all("alerts"),user);}
 @GetMapping("/incidents") public ArrayNode incidents(@AuthenticationPrincipal AuthenticatedUser user){return operations.visible("incidents",store.all("incidents"),user);}
}
