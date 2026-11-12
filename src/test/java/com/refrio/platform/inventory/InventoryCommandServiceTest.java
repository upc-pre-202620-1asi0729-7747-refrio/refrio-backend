package com.refrio.platform.inventory;
import com.refrio.platform.inventory.application.*;
import com.refrio.platform.inventory.application.commands.*;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import com.refrio.platform.inventory.domain.model.repositories.*;
import com.refrio.platform.events.application.OperationalEventPublisher;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import org.junit.jupiter.api.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class InventoryCommandServiceTest {
 InventoryProductRepository products=mock(InventoryProductRepository.class);
 StockBatchRepository batches=mock(StockBatchRepository.class);
 InventoryLossRepository losses=mock(InventoryLossRepository.class);
 TemperatureReadingRepository readings=mock(TemperatureReadingRepository.class);
 UnitOfWorkLock lock=mock(UnitOfWorkLock.class);
 OperationalEventPublisher events=mock(OperationalEventPublisher.class);
 InventoryCommandService app=new InventoryCommandService(products,batches,losses,readings,lock,events);
 AuthenticatedUser actor=new AuthenticatedUser(2L,"bodega","Minorista");
 InventoryProduct product=new InventoryProduct("P1","Yogurt","Dairy","20","4°C","2099-12-31","Lima",null,null);
 @Test void registeringBatchAtomicallyUpdatesCorrectAudience(){
  when(products.findById("P1","Minorista")).thenReturn(Optional.of(product));
  StockBatch saved=app.register(new RegisterBatchCommand("P1",6,"2026-11-09","2099-12-31","Finca","Rack",null,null),actor);
  assertEquals("Minorista",saved.audience());
  verify(batches).save(argThat(b->b.quantity()==6));
  verify(products).save(argThat(p->p.availableUnits()==26),eq("Minorista"));
  verify(events).publish(eq("Lote registrado"),anyString(),anyString(),eq(actor),eq("SUCCESS"),anyString(),eq("Minorista"));
 }
 @Test void temperatureDoesNotCrossAudienceBoundary(){
  assertThrows(DomainException.class,()->app.recordTemperature(new RecordTemperatureCommand("MISSING",5),actor));
  verifyNoInteractions(readings);
 }
 @Test void lossRequiresStockAndCorrectAudience(){
  StockBatch batch=new StockBatch("B1","P1",2,null,"2026-11-09","2099-12-31","Finca","Rack","Disponible","Supervisor",null);
  when(batches.findById("B1")).thenReturn(Optional.of(batch));
  assertThrows(DomainException.class,()->app.recordLoss(new RecordLossCommand("B1",1,"Caducidad"),actor));
  verifyNoInteractions(losses);
 }
}
