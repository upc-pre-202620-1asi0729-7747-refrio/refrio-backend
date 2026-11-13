package com.refrio.platform.traceability;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.traceability.domain.services.ShipmentTemperaturePolicy;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
class ShipmentTemperaturePolicyTest {
  private static final Instant START=Instant.parse("2026-11-11T12:00:00Z");
  private static final Instant END=Instant.parse("2026-11-11T14:00:00Z");
  @Test void rejectsTelemetryFromBeforeDispatchOrDifferentAudience(){
    var readings=List.of(
      new ShipmentTemperaturePolicy.Sample("PRD-1","Supervisor",17.0,Instant.parse("2026-11-11T11:00:00Z")),
      new ShipmentTemperaturePolicy.Sample("PRD-1","Minorista",3.0,Instant.parse("2026-11-11T13:00:00Z")));
    assertThrows(DomainException.class,()->ShipmentTemperaturePolicy.forShipment(Set.of("PRD-1"),START,END,readings));
  }
  @Test void requiresEvidenceForEachProductAndUsesOnlyShipmentInterval(){
    var readings=List.of(
      new ShipmentTemperaturePolicy.Sample("PRD-1","Supervisor",4.0,Instant.parse("2026-11-11T12:30:00Z")),
      new ShipmentTemperaturePolicy.Sample("PRD-2","Supervisor",6.0,Instant.parse("2026-11-11T13:15:00Z")),
      new ShipmentTemperaturePolicy.Sample("PRD-2","Supervisor",90.0,Instant.parse("2026-11-11T15:00:00Z")));
    var result=ShipmentTemperaturePolicy.forShipment(Set.of("PRD-1","PRD-2"),START,END,readings);
    assertEquals(2,result.samples());assertEquals(4.0,result.min());assertEquals(6.0,result.max());
    assertThrows(DomainException.class,()->ShipmentTemperaturePolicy.forShipment(Set.of("PRD-1","PRD-2","PRD-3"),START,END,readings));
  }
}
