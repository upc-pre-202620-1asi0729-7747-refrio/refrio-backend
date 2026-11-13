package com.refrio.platform.inventory;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import com.refrio.platform.shared.domain.DomainException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class InventoryAggregatesTest {
 @Test void batchCannotOversellAndExpiredBatchCannotDispatch(){
  StockBatch batch=new StockBatch("B1","P1",5,"10kg","2026-01-01","2099-01-01","Finca","Rack","Disponible","Supervisor",null);
  assertEquals(2,batch.debit(3).quantity());
  assertThrows(DomainException.class,()->batch.debit(6));
  assertFalse(batch.canDispatch(LocalDate.of(2100,1,1)));
 }
 @Test void temperatureMustBeFiniteAndLossMustBePositive(){
  assertThrows(DomainException.class,()->TemperatureReading.capture("P1","Minorista",Double.NaN));
  assertThrows(DomainException.class,()->TemperatureReading.capture("P1","Minorista",101));
  assertThrows(DomainException.class,()->InventoryLoss.record("B1","P1",0,"Caducidad","Supervisor"));
  assertThrows(DomainException.class,()->InventoryLoss.record("B1","P1",2,"","Supervisor"));
  assertEquals(2,InventoryLoss.record("B1","P1",2,"Caducidad","Supervisor").quantity());
 }
}
