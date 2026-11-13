package com.refrio.platform.operations.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.operations.application.OperationsApplicationService;
import com.refrio.platform.shared.domain.DomainException;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name="Operations")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/operations")
public class OperationsController {
 private final OperationsApplicationService service;
 public OperationsController(OperationsApplicationService service){this.service=service;}
 @GetMapping public ObjectNode snapshot(@AuthenticationPrincipal AuthenticatedUser user){return service.snapshot(user);}
 @PostMapping("/{command}") public ObjectNode execute(@PathVariable String command,@RequestBody ObjectNode body,@AuthenticationPrincipal AuthenticatedUser user){
  if(!(body.path("args") instanceof ArrayNode args))throw new DomainException("Se esperaba args: []");return service.execute(command,args,user);
 }
}
