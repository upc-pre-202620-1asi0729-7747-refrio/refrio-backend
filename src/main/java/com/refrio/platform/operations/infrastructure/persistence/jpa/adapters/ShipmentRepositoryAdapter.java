package com.refrio.platform.operations.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.operations.domain.model.aggregates.ShipmentAggregate;
import com.refrio.platform.operations.domain.model.repositories.ShipmentRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ShipmentRepositoryAdapter extends JsonAggregateRepositorySupport<ShipmentAggregate> implements ShipmentRepository {
    public ShipmentRepositoryAdapter(DocumentRepository documents,ObjectMapper mapper){super("shipments",ShipmentAggregate.class,documents,mapper);}
    @Override public ShipmentAggregate save(ShipmentAggregate shipment){return super.save(shipment.id(),shipment);}
}
