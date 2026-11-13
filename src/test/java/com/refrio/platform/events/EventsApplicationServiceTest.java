package com.refrio.platform.events;
import com.refrio.platform.events.application.*;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import com.refrio.platform.events.domain.model.repositories.OperationalEventRepository;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class EventsApplicationServiceTest {
 @Test void retailerOnlySeesItsEventsAndEventsAreSortedNewestFirst(){
  OperationalEventRepository repo=mock(OperationalEventRepository.class);
  var old=new OperationalEventAggregate("E1","Alerta","P1","","Inventory","2026-11-09T10:00:00Z","admin","SYSTEM","WARNING","Minorista",null);
  var newest=new OperationalEventAggregate("E2","Lote registrado","P2","","Inventory","2026-11-09T12:00:00Z","admin","USER","SUCCESS","Minorista",null);
  var hidden=new OperationalEventAggregate("E3","Privado","P3","","Inventory","2026-11-09T13:00:00Z","admin","USER","SUCCESS","Supervisor",null);
  when(repo.findAll()).thenReturn(List.of(old,hidden,newest));
  var service=new EventsQueryService(repo);
  assertEquals(List.of("E2","E1"),service.latest(new AuthenticatedUser(2L,"bodega","Minorista")).stream().map(OperationalEventAggregate::id).toList());
 }
 @Test void publisherAppendsWithinCallingTransaction(){
  OperationalEventRepository repo=mock(OperationalEventRepository.class);
  when(repo.append(any())).thenAnswer(inv->inv.getArgument(0));
  var publisher=new OperationalEventPublisher(repo);
  var saved=publisher.publish("Alta de lote","B1","Inventory & FEFO",new AuthenticatedUser(1L,"admin","Supervisor"),"SUCCESS","10 unidades","Supervisor");
  assertEquals("Supervisor",saved.audience());verify(repo).append(any(OperationalEventAggregate.class));
 }
}
