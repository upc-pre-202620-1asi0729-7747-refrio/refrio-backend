package com.refrio.platform.operations;
import com.refrio.platform.operations.domain.services.ShipmentPolicy;
import com.refrio.platform.inventory.domain.model.Allocation;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class ShipmentPolicyTest {
 @Test void allowsOnlyLegalTransitions(){
  assertEquals("In Transit",ShipmentPolicy.advance("Preparing"));
  assertEquals("Awaiting Receipt",ShipmentPolicy.advance("In Transit"));
  assertThrows(DomainException.class,()->ShipmentPolicy.advance("Delivered"));
 }
 @Test void partialReceiptRequiresObservations(){
  var expected=List.of(new Allocation("B1","P1",10));var received=List.of(new Allocation("B1","P1",7));
  assertThrows(DomainException.class,()->ShipmentPolicy.validateReceipt("Awaiting Receipt",false,expected,received,"accepted",""));
  assertThrows(DomainException.class,()->ShipmentPolicy.validateReceipt("Awaiting Receipt",false,expected,received,"observed",""));
  assertDoesNotThrow(()->ShipmentPolicy.validateReceipt("Awaiting Receipt",false,expected,received,"observed","Dos unidades dañadas"));
 }
}
