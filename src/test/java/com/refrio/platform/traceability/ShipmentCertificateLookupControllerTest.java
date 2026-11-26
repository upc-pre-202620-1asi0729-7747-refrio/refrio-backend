package com.refrio.platform.traceability;

import com.refrio.platform.traceability.interfaces.rest.ShipmentCertificateLookupController;
import com.refrio.platform.traceability.domain.model.repositories.CertificateRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ShipmentCertificateLookupControllerTest {
    @Test void cannotReadOtherRetailersCertificate() {
        var store=mock(DocumentRepository.class);
        var document=JsonNodeFactory.instance.objectNode().put("recipientId","100");
        when(store.get("shipments","ENV-1")).thenReturn(document);
        var controller=new ShipmentCertificateLookupController(mock(CertificateRepository.class),store);
        assertThrows(ForbiddenOperationException.class,
            ()->controller.byShipment("ENV-1",new AuthenticatedUser(2L,"other","Minorista")));
    }
}
