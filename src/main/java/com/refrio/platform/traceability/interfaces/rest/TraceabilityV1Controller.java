package com.refrio.platform.traceability.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.traceability.application.TraceabilityQueryService;
import com.refrio.platform.traceability.application.CertificationService;
import org.springframework.http.HttpStatus;
import com.refrio.platform.traceability.domain.model.aggregates.ColdChainCertificateAggregate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="Traceability")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/v1/traceability")
public class TraceabilityV1Controller {
    private final TraceabilityQueryService service;
    private final CertificationService certification;
    public TraceabilityV1Controller(TraceabilityQueryService service,CertificationService certification){this.service=service;this.certification=certification;}
    @GetMapping("/certificates") public List<ColdChainCertificateAggregate> list(@AuthenticationPrincipal AuthenticatedUser user){return service.visibleTo(user);}
    @GetMapping("/certificates/qr/{code}") public ColdChainCertificateAggregate qr(@PathVariable String code,@AuthenticationPrincipal AuthenticatedUser user){return service.byQr(code,user);}
    @PostMapping("/certificates/shipment/{shipmentId}") @ResponseStatus(HttpStatus.CREATED)
    public com.fasterxml.jackson.databind.node.ObjectNode certify(@PathVariable String shipmentId,
              @AuthenticationPrincipal AuthenticatedUser user){return certification.certify(shipmentId,user);}
}
