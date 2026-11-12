package com.refrio.platform.traceability;

import com.refrio.platform.traceability.domain.services.CertificateIssuancePolicy;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CertificateIssuancePolicyTest {
    @Test void onlyRecipientOrSupervisorCanCertifyDeliveredShipment() {
        assertDoesNotThrow(() -> CertificateIssuancePolicy.validate("Delivered",true));
        assertThrows(DomainException.class,() -> CertificateIssuancePolicy.validate("In Transit",true));
        assertThrows(DomainException.class,() -> CertificateIssuancePolicy.validate("Delivered",false));
    }
}
