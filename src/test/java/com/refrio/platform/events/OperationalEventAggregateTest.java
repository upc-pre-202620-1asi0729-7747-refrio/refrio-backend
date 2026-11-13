package com.refrio.platform.events;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class OperationalEventAggregateTest {
 @Test void auditRecordHasImmutableIdentityAndValidatedOutcome(){
  var event=OperationalEventAggregate.userAction("Lote registrado","B1","10 unidades","Inventory & FEFO","admin","SUCCESS","Supervisor");
  assertTrue(event.id().startsWith("EVT-"));assertEquals("USER",event.source());
  assertNotNull(event.occurredAt());
  assertThrows(DomainException.class,()->new OperationalEventAggregate("","Name","B1","D","CTX","now","admin","USER","SUCCESS","Supervisor",null));
  assertThrows(IllegalArgumentException.class,()->new OperationalEventAggregate("E1","Name","B1","D","CTX","now","admin","USER","FAKE","Supervisor",null));
 }
}
