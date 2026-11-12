package com.refrio.platform.architecture;

import com.refrio.platform.iam.domain.model.valueobjects.*;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import com.refrio.platform.operations.domain.model.aggregates.*;
import com.refrio.platform.operations.domain.model.valueobjects.*;
import com.refrio.platform.alerting.domain.model.aggregates.*;
import com.refrio.platform.traceability.domain.model.TemperatureEvidence;
import com.refrio.platform.traceability.domain.model.valueobjects.CertificateFingerprint;
import com.refrio.platform.analytics.domain.model.valueobjects.Percentage;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.publicportal.domain.model.LeadStatus;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BoundedContextDomainTest {
    @Test void iamValidatesIdentityAndRoles(){
        assertEquals("admin",new Username("admin").value());
        assertEquals("bodega@example.com",new EmailAddress("BODEGA@EXAMPLE.COM").value());
        assertThrows(DomainException.class,()->new Username("a"));
        assertThrows(DomainException.class,()->new EmailAddress("incorrect"));
        assertThrows(DomainException.class,()->UserRole.parse("Admin"));
    }

    @Test void inventoryEnforcesStockAndExpiry(){
        var product=new InventoryProduct("P1","Leche","Lácteos","10","4°C","2099-01-01","Lima",null,null);
        assertEquals(6,product.withStockAdjustment(-4).availableUnits());
        assertThrows(DomainException.class,()->product.withStockAdjustment(-20));
        var batch=new StockBatch("B1","P1",5,null,"2026-01-01","2099-01-01","Supplier","Rack","Disponible","Supervisor",null);
        assertTrue(batch.canDispatch(LocalDate.of(2026,11,10)));
        assertEquals(3,batch.debit(2).quantity());
        assertFalse(batch.canDispatch(LocalDate.of(2100,1,1)));
    }

    @Test void operationsEnforcesStatusTransitionsAndRoute(){
        var draft=new ShipmentAggregate("S1","Preparing","2","C1",List.of(),List.of(),null);
        assertEquals("In Transit",draft.advance().status());
        assertThrows(DomainException.class,()->draft.receive("accepted",List.of(),"","bodega"));
        assertEquals("Lima → Surco",new ShipmentRoute("Lima","Surco").label());
        assertThrows(DomainException.class,()->new ShipmentRoute("Lima","lima"));
    }

    @Test void alertingRequiresCorrectiveActionBeforeClosure(){
        var incident=IncidentAggregate.open("ALT-1","supervisor","Supervisor","P1");
        assertThrows(DomainException.class,()->incident.transition("resolve",null,null,null,"ok","supervisor"));
        var updated=incident.transition("action","Sensor","Ajustar compresor","inspect",null,"supervisor");
        assertEquals("ACTION_RECORDED",updated.status());
        assertEquals("RESOLVED",updated.transition("resolve",null,null,null,"Verificado","supervisor").status());
    }

    @Test void traceabilityUsesMeasurementsAndIntegrityChecks(){
        var evidence=TemperatureEvidence.from(List.of(2.0,4.0,6.0));
        assertEquals(4,evidence.average());
        assertEquals(3,evidence.samples());
        assertEquals(64,CertificateFingerprint.of("shipment-1").hex().length());
        assertThrows(DomainException.class,()->TemperatureEvidence.from(List.of()));
    }

    @Test void analyticsCannotClaimOverOneHundredPercent(){
        assertEquals(75.0,Percentage.ratio(3,4).value());
        assertThrows(DomainException.class,()->Percentage.ratio(5,4));
    }

    @Test void publicPortalControlsLeadLifecycle(){
        var lead=LeadAggregate.submit("Bodega","Ana","ana@example.org","9999","Basico","Requiere demo");
        assertEquals("NEW",lead.status());
        assertEquals("CONTACTED",lead.moveTo(LeadStatus.CONTACTED).status());
        assertThrows(DomainException.class,()->lead.moveTo(LeadStatus.CLOSED).moveTo(LeadStatus.NEW));
    }

    @Test void eventsProduceUnambiguousAuditRecords(){
        var event=OperationalEventAggregate.userAction("Merma registrada","B1","5 unidades","Inventory & FEFO","admin","SUCCESS","Supervisor");
        assertTrue(event.id().startsWith("EVT-"));
        assertEquals("USER",event.source());
        assertEquals("Supervisor",event.audience());
    }
}
