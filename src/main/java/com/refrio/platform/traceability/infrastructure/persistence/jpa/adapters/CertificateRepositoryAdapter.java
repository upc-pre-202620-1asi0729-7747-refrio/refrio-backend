package com.refrio.platform.traceability.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.traceability.domain.model.aggregates.ColdChainCertificateAggregate;
import com.refrio.platform.traceability.domain.model.repositories.CertificateRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class CertificateRepositoryAdapter extends JsonAggregateRepositorySupport<ColdChainCertificateAggregate> implements CertificateRepository {
    public CertificateRepositoryAdapter(DocumentRepository documents,ObjectMapper mapper){super("certificates",ColdChainCertificateAggregate.class,documents,mapper);}
    @Override public Optional<ColdChainCertificateAggregate> findByShipmentId(String shipmentId){
        return findAll().stream().filter(c->shipmentId.equals(c.shipmentId())).findFirst();
    }
    @Override public Optional<ColdChainCertificateAggregate> findByQrCode(String code){
        return findAll().stream().filter(c->code.equals(c.qrCode())).findFirst();
    }
    @Override public ColdChainCertificateAggregate save(ColdChainCertificateAggregate c){return super.save(c.id(),c);}
}
