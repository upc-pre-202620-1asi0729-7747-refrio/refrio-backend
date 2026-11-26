package com.refrio.platform.traceability.interfaces.rest;

import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.traceability.domain.model.repositories.CertificateRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.shared.domain.ResourceNotFoundException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/certificates/shipment")
public class ShipmentCertificateLookupController {
    private final CertificateRepository certificates;
    private final DocumentRepository store;
    public ShipmentCertificateLookupController(CertificateRepository certificates,DocumentRepository store){
        this.certificates=certificates;this.store=store;
    }
    @GetMapping("/{shipmentId}")
    @Transactional(readOnly=true)
    public ObjectNode byShipment(@PathVariable String shipmentId,
                                 @AuthenticationPrincipal AuthenticatedUser actor) {
        ObjectNode shipment=store.get("shipments",shipmentId);
        if(actor==null || (!actor.supervisor() &&
                !String.valueOf(actor.id()).equals(shipment.path("recipientId").asText())))
            throw new ForbiddenOperationException("Este envío pertenece a otro destinatario");
        String id=certificates.findByShipmentId(shipmentId)
            .orElseThrow(()->new ResourceNotFoundException("Aún no existe certificado para este envío"))
            .id();
        return store.get("certificates",id);
    }
}
