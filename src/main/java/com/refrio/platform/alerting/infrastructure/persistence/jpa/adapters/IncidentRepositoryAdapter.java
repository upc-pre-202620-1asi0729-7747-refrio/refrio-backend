package com.refrio.platform.alerting.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.alerting.domain.model.aggregates.IncidentAggregate;
import com.refrio.platform.alerting.domain.model.repositories.IncidentRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class IncidentRepositoryAdapter extends JsonAggregateRepositorySupport<IncidentAggregate> implements IncidentRepository {
    public IncidentRepositoryAdapter(DocumentRepository documents,ObjectMapper mapper){super("incidents",IncidentAggregate.class,documents,mapper);}
    @Override public IncidentAggregate save(IncidentAggregate incident){return super.save(incident.id(),incident);}
}
