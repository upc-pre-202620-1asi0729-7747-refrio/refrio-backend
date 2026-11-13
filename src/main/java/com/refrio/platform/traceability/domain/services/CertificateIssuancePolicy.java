package com.refrio.platform.traceability.domain.services;

import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;

/** A shipment cannot be certified merely because some temperature readings exist. */
public final class CertificateIssuancePolicy {
    private CertificateIssuancePolicy() {}

    public static void validate(String shipmentStatus, boolean canAccessShipment) {
        if (!canAccessShipment) {
            throw new ForbiddenOperationException("No autorizado para certificar este despacho");
        }
        if (!"Delivered".equals(shipmentStatus)) {
            throw new DomainException("Solo es posible certificar despachos recibidos");
        }
    }
}
