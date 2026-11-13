package com.refrio.platform.analytics.infrastructure.persistence.jpa.adapters;
import com.refrio.platform.analytics.domain.model.OperationalMetrics;
import com.refrio.platform.analytics.domain.model.repositories.AnalyticsReadRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Repository;
import java.util.stream.*;

@Repository
public class AnalyticsReadRepositoryAdapter implements AnalyticsReadRepository {
    private final DocumentRepository documents;
    public AnalyticsReadRepositoryAdapter(DocumentRepository documents){this.documents=documents;}
    @Override public OperationalMetrics calculate(String audience,String recipientId){
        boolean supervisor="Supervisor".equals(audience);
        var products=documents.all(supervisor?"inventory":"bodeguero-inventory");
        var batches=documents.all("batches");
        var shipments=documents.all("shipments");
        var losses=documents.all("losses");
        var telemetry=documents.all("telemetry");
        long activeBatches=StreamSupport.stream(batches.spliterator(),false)
           .filter(b->audience.equals(b.path("audience").asText("Supervisor"))&&b.path("quantity").asInt()>0).count();
        var accessibleShipments=StreamSupport.stream(shipments.spliterator(),false)
           .filter(s->supervisor||recipientId.equals(s.path("recipientId").asText())).toList();
        long delivered=accessibleShipments.stream().filter(s->"Delivered".equals(s.path("status").asText())).count();
        long lossUnits=StreamSupport.stream(losses.spliterator(),false).filter(l->audience.equals(l.path("audience").asText()))
           .mapToLong(l->l.path("quantity").asLong()).sum();
        long readings=StreamSupport.stream(telemetry.spliterator(),false).filter(t->audience.equals(t.path("audience").asText())).count();
        return new OperationalMetrics(products.size(),activeBatches,accessibleShipments.size(),delivered,lossUnits,readings);
    }
}
