package com.refrio.platform.inventory;
import com.refrio.platform.inventory.domain.services.FefoPolicy;
import com.refrio.platform.inventory.domain.model.Allocation;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class FefoPolicyTest {
 private static final LocalDate TODAY=LocalDate.of(2026,11,9);
 @Test void choosesOldestEligibleBatchFirst(){
  var batches=List.of(new FefoPolicy.Batch("B2","P",20,TODAY.plusDays(10),"Disponible","Supervisor"),new FefoPolicy.Batch("B1","P",15,TODAY.plusDays(2),"Disponible","Supervisor"));
  assertEquals(List.of(new Allocation("B1","P",15),new Allocation("B2","P",5)),FefoPolicy.plan(batches,"P",20,TODAY));
 }
 @Test void skipsExpiredAndHeldBatches(){
  var batches=List.of(new FefoPolicy.Batch("A","P",50,TODAY.minusDays(1),"Disponible","Supervisor"),new FefoPolicy.Batch("B","P",50,TODAY.plusDays(2),"Bloqueado","Supervisor"));
  assertThrows(DomainException.class,()->FefoPolicy.plan(batches,"P",1,TODAY));
 }
 @Test void validatesScanOrder(){
  var plan=List.of(new Allocation("B1","P",3),new Allocation("B2","P",4));
  assertThrows(DomainException.class,()->FefoPolicy.validatePicking(plan,List.of("B2","B1")));
  assertDoesNotThrow(()->FefoPolicy.validatePicking(plan,List.of("B1","B2")));
 }
}
