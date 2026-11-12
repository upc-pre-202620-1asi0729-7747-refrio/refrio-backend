package com.refrio.platform.events.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import com.refrio.platform.events.domain.model.repositories.OperationalEventRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class OperationalEventRepositoryAdapter extends JsonAggregateRepositorySupport<OperationalEventAggregate> implements OperationalEventRepository {
    public OperationalEventRepositoryAdapter(DocumentRepository documents,ObjectMapper mapper){super("events",OperationalEventAggregate.class,documents,mapper);}
    @Override public OperationalEventAggregate append(OperationalEventAggregate event){
        if(findById(event.id()).isPresent())throw new DomainException("Los eventos de auditoría son inmutables");
        return super.save(event.id(),event);
    }
}
