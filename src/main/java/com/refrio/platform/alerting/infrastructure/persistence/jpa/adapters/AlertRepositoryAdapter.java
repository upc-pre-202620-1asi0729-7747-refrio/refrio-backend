package com.refrio.platform.alerting.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.alerting.domain.model.aggregates.AlertAggregate;
import com.refrio.platform.alerting.domain.model.repositories.AlertRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class AlertRepositoryAdapter extends JsonAggregateRepositorySupport<AlertAggregate> implements AlertRepository {
    public AlertRepositoryAdapter(DocumentRepository documents,ObjectMapper mapper){super("alerts",AlertAggregate.class,documents,mapper);}
}
