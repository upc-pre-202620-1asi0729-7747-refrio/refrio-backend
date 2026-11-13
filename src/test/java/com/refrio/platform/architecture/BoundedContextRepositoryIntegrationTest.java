package com.refrio.platform.architecture;

import com.refrio.platform.alerting.application.AlertingApplicationService;
import com.refrio.platform.alerting.domain.model.repositories.IncidentRepository;
import com.refrio.platform.analytics.domain.model.repositories.AnalyticsReadRepository;
import com.refrio.platform.events.application.OperationalEventPublisher;
import com.refrio.platform.events.domain.model.repositories.OperationalEventRepository;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import com.refrio.platform.inventory.domain.model.repositories.*;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.publicportal.domain.model.repositories.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @ActiveProfiles("test") @Transactional
class BoundedContextRepositoryIntegrationTest {
    @Autowired InventoryProductRepository products;
    @Autowired StockBatchRepository batches;
    @Autowired LeadRepository leads;
    @Autowired IncidentRepository incidents;
    @Autowired AlertingApplicationService alerting;
    @Autowired OperationalEventRepository events;
    @Autowired OperationalEventPublisher publisher;
    @Autowired AnalyticsReadRepository analytics;
    @Autowired JdbcTemplate jdbc;
    private final AuthenticatedUser actor=new AuthenticatedUser(1L,"admin","Supervisor");

    @BeforeEach void mutex(){jdbc.update("MERGE INTO operation_mutex (id) KEY(id) VALUES (1)");}

    @Test void inventoryRoundTripAndMetrics(){
        String productId="P-"+UUID.randomUUID();
        products.save(new InventoryProduct(productId,"Yogurt","Dairy","15","3°C","2099-12-31","Lima",null,null),"Supervisor");
        assertEquals(15,products.findById(productId,"Supervisor").orElseThrow().availableUnits());
        String batchId="B-"+UUID.randomUUID();
        batches.save(new StockBatch(batchId,productId,5,null,"2026-11-09","2099-12-31","Milk Farm","A1","Disponible","Supervisor",null));
        assertTrue(batches.findById(batchId).isPresent());
        assertTrue(analytics.calculate("Supervisor","1").productCount()>=1);
    }

    @Test void leadIncidentAndEventsAreStoredThroughOwnedPorts(){
        LeadAggregate lead=LeadAggregate.submit("Distribuidora","Ana","ana@example.org","999","Basico","Prueba");
        leads.save(lead);
        assertEquals("NEW",leads.findById(lead.id()).orElseThrow().status());
        String alertId="ALT-"+UUID.randomUUID();
        alerting.update(alertId,"action","Temperatura","Verificar cámara","inspect",null,"P1","Supervisor",actor);
        assertEquals("ACTION_RECORDED",incidents.findById(alertId).orElseThrow().status());
        var event=publisher.publish("Prueba de auditoría",alertId,"Alerting",actor,"SUCCESS","Verificación","Supervisor");
        assertEquals(event.id(),events.findById(event.id()).orElseThrow().id());
    }
}
