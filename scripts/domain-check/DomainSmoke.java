import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.iam.domain.model.valueobjects.*;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import com.refrio.platform.inventory.domain.services.FefoPolicy;
import com.refrio.platform.inventory.domain.model.Allocation;
import com.refrio.platform.operations.domain.model.aggregates.ShipmentAggregate;
import com.refrio.platform.alerting.domain.model.aggregates.IncidentAggregate;
import com.refrio.platform.traceability.domain.model.TemperatureEvidence;
import com.refrio.platform.traceability.domain.services.ShipmentTemperaturePolicy;
import com.refrio.platform.operations.domain.model.valueobjects.GeoCoordinates;
import com.refrio.platform.operations.domain.model.valueobjects.AmbientWeather;
import java.time.Instant;
import java.util.Set;
import com.refrio.platform.traceability.domain.model.valueobjects.CertificateFingerprint;
import com.refrio.platform.analytics.domain.model.OperationalMetrics;
import com.refrio.platform.analytics.domain.model.aggregates.AnalyticsReportAggregate;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.publicportal.domain.model.LeadStatus;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import com.refrio.platform.shared.domain.DomainException;
import java.time.LocalDate;
import java.util.List;

/** Runs with the JDK alone, proving all eight DDD domain models are free from frameworks. */
public final class DomainSmoke {
    static int checks = 0;
    static void valid(boolean ok) {
        checks++;
        if (!ok) throw new AssertionError("Domain check " + checks + " failed");
    }
    static void rejects(Runnable task) {
        try { task.run(); throw new AssertionError("Business rule should have rejected operation"); }
        catch (DomainException expected) { checks++; }
    }
    public static void main(String[] args) {
        UserAccount user = new UserAccount(1L,"admin","ADMIN@REFRIO.COM","hashed","Ana","Rios","Supervisor","Pro","Lima");
        valid(user.email().equals("admin@refrio.com"));
        rejects(() -> new Username("a"));
        valid(UserRole.parse("Minorista")==UserRole.MINORISTA);

        InventoryProduct product = new InventoryProduct("P1","Leche","Dairy","20","4°C","2099-12-31","Lima",null,null);
        valid(product.withStockAdjustment(-4).availableUnits()==16);
        rejects(() -> product.withStockAdjustment(-21));
        StockBatch batch = new StockBatch("B1","P1",10,null,"2026-11-09","2099-12-31","Finca","Rack","Disponible","Supervisor",null);
        valid(batch.canDispatch(LocalDate.of(2026,11,9)));
        valid(batch.debit(3).quantity()==7);
        var plans = FefoPolicy.plan(List.of(
            new FefoPolicy.Batch("B2","P1",20,LocalDate.of(2099,11,1),"Disponible","Supervisor"),
            new FefoPolicy.Batch("B1","P1",10,LocalDate.of(2099,1,1),"Disponible","Supervisor")),"P1",11,LocalDate.of(2026,11,9));
        valid(plans.equals(List.of(new Allocation("B1","P1",10),new Allocation("B2","P1",1))));
        valid(InventoryLoss.record("B1","P1",2,"Caducidad","Supervisor").quantity()==2);
        rejects(() -> TemperatureReading.capture("P1","Minorista",Double.NaN));

        ShipmentAggregate shipment = new ShipmentAggregate("S1","Preparing","2","C1",List.of(new Allocation("B1","P1",5)),List.of(),null);
        valid(shipment.advance().status().equals("In Transit"));
        var arrived=shipment.advance().advance();
        valid(arrived.receive("accepted",List.of(new Allocation("B1","P1",5)),"","bodega").status().equals("Delivered"));
        rejects(() -> shipment.receive("accepted",List.of(new Allocation("B1","P1",5)),"","bodega"));

        IncidentAggregate opened=IncidentAggregate.open("ALT-1","admin","Supervisor","P1");
        rejects(() -> opened.transition("resolve",null,null,null,"ok","admin"));
        valid(opened.transition("action","Problema","Ajustar cámara","inspect",null,"admin")
            .transition("resolve",null,null,null,"Verificado","admin").status().equals("RESOLVED"));

        valid(TemperatureEvidence.from(List.of(2.0,4.0,6.0)).average()==4.0);
        rejects(() -> TemperatureEvidence.from(List.of(Double.POSITIVE_INFINITY)));
        valid(CertificateFingerprint.of("shipment-S1").hex().length()==64);
        var start=Instant.parse("2026-11-11T12:00:00Z");
        var end=Instant.parse("2026-11-11T14:00:00Z");
        var readings=List.of(new ShipmentTemperaturePolicy.Sample("P1","Supervisor",4,start.plusSeconds(60)),
             new ShipmentTemperaturePolicy.Sample("P1","Supervisor",99,start.minusSeconds(60)));
        valid(ShipmentTemperaturePolicy.forShipment(Set.of("P1"),start,end,readings).average()==4);
        rejects(()->ShipmentTemperaturePolicy.forShipment(Set.of("P1","P2"),start,end,readings));
        valid(new AmbientWeather(new GeoCoordinates(-12.05,-77.04),23,60,start,"Fake").temperatureCelsius()==23);
        rejects(()->new GeoCoordinates(120,-77.04));

        var analytics=AnalyticsReportAggregate.create("Supervisor",new OperationalMetrics(2,3,4,3,5,6));
        valid(analytics.deliveryRate().value()==75.0);
        rejects(() -> AnalyticsReportAggregate.create("Supervisor",new OperationalMetrics(2,3,4,5,5,6)));

        LeadAggregate lead=LeadAggregate.submit("Empresa","Ana","ana@example.org","999","Basico","");
        valid(lead.moveTo(LeadStatus.CONTACTED).status().equals("CONTACTED"));
        rejects(() -> lead.moveTo(LeadStatus.CLOSED).moveTo(LeadStatus.CONTACTED));

        OperationalEventAggregate event=OperationalEventAggregate.userAction("Lote creado","B1","10 unidades","Inventory","admin","SUCCESS","Supervisor");
        valid(event.source().equals("USER") && event.id().startsWith("EVT-"));
        System.out.println("OK: eight bounded contexts, " + checks + " framework-independent domain checks");
    }
}
