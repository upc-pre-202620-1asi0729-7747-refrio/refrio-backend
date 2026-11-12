package com.refrio.platform.operations;
import com.refrio.platform.operations.domain.model.aggregates.ShipmentAggregate;
import com.refrio.platform.inventory.domain.model.Allocation;
import com.refrio.platform.shared.domain.DomainException;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ShipmentAggregateTest {
 @Test void aggregateControlsAllShipmentTransitionsAndReceipt(){
  var draft=new ShipmentAggregate("S1","Preparing","2","C1",List.of(new Allocation("B1","P1",5)),List.of(),null);
  assertFalse(draft.canReceive(3L,false));assertTrue(draft.canReceive(2L,false));
  var transit=draft.advance();assertEquals("In Transit",transit.status());
  var awaiting=transit.advance();assertEquals("Awaiting Receipt",awaiting.status());
  var delivered=awaiting.receive("accepted",List.of(new Allocation("B1","P1",5)),"","bodega");
  assertEquals("Delivered",delivered.status());assertEquals(3,delivered.timeline().size());
  assertThrows(DomainException.class,()->delivered.receive("accepted",List.of(new Allocation("B1","P1",5)),"","bodega"));
 }
 @Test void cannotAcceptPartialShipmentWithoutObservation(){
  var awaiting=new ShipmentAggregate("S1","Awaiting Receipt","2","C1",List.of(new Allocation("B1","P1",5)),List.of(),null);
  assertThrows(DomainException.class,()->awaiting.receive("accepted",List.of(new Allocation("B1","P1",3)),"","bodega"));
 }
}
