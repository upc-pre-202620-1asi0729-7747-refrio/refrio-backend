package com.refrio.platform.traceability.domain.model.repositories;
import com.refrio.platform.traceability.domain.model.aggregates.ColdChainCertificateAggregate;
import java.util.*;
public interface CertificateRepository {
    Optional<ColdChainCertificateAggregate> findById(String id);
    Optional<ColdChainCertificateAggregate> findByShipmentId(String shipmentId);
    Optional<ColdChainCertificateAggregate> findByQrCode(String qrCode);
    List<ColdChainCertificateAggregate> findAll();
    ColdChainCertificateAggregate save(ColdChainCertificateAggregate certificate);
}
