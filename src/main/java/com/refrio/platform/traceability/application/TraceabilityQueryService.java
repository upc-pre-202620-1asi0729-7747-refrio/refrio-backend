package com.refrio.platform.traceability.application;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.operations.domain.model.repositories.ShipmentRepository;
import com.refrio.platform.traceability.domain.model.aggregates.ColdChainCertificateAggregate;
import com.refrio.platform.traceability.domain.model.repositories.CertificateRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class TraceabilityQueryService {
    private final CertificateRepository certificates;
    private final ShipmentRepository shipments;
    public TraceabilityQueryService(CertificateRepository certificates,ShipmentRepository shipments){
        this.certificates=certificates;this.shipments=shipments;
    }
    @Transactional(readOnly=true) public List<ColdChainCertificateAggregate> visibleTo(AuthenticatedUser user){
        return certificates.findAll().stream().filter(c->authorized(c.shipmentId(),user)).toList();
    }
    @Transactional(readOnly=true) public ColdChainCertificateAggregate byQr(String qr,AuthenticatedUser user){
        var certificate=certificates.findByQrCode(qr).orElseThrow(()->new DomainException("Certificado inexistente"));
        if(!authorized(certificate.shipmentId(),user))throw new ForbiddenOperationException("No autorizado");
        return certificate;
    }
    private boolean authorized(String shipmentId,AuthenticatedUser user){
        return shipments.findById(shipmentId).map(s->s.canReceive(user.id(),user.supervisor())).orElse(false);
    }
}
