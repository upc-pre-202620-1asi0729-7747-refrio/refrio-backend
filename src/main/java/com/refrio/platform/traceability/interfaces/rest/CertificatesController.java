package com.refrio.platform.traceability.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.traceability.application.CertificationService;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name="Traceability")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/certificates")
public class CertificatesController {
 private final CertificationService service;
 public CertificatesController(CertificationService service){this.service=service;}
 @GetMapping public ArrayNode all(@AuthenticationPrincipal AuthenticatedUser user){return service.all(user);}
 @PostMapping("/shipment/{shipmentId}") public ObjectNode certify(@PathVariable String shipmentId,@AuthenticationPrincipal AuthenticatedUser user){return service.certify(shipmentId,user);}
 @GetMapping("/qr/{qrCode}") public ObjectNode qr(@PathVariable String qrCode,@AuthenticationPrincipal AuthenticatedUser user){return service.qr(qrCode,user);}
}
