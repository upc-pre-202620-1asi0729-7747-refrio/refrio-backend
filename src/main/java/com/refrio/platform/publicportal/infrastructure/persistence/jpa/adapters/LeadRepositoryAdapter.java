package com.refrio.platform.publicportal.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.publicportal.domain.model.repositories.LeadRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class LeadRepositoryAdapter extends JsonAggregateRepositorySupport<LeadAggregate> implements LeadRepository {
    public LeadRepositoryAdapter(DocumentRepository documents,ObjectMapper mapper){super("leads",LeadAggregate.class,documents,mapper);}
    @Override public LeadAggregate save(LeadAggregate lead){return super.save(lead.id(),lead);}
}
