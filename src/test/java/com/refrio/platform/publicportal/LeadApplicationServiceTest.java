package com.refrio.platform.publicportal;
import com.refrio.platform.publicportal.application.*;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.publicportal.domain.model.LeadStatus;
import com.refrio.platform.publicportal.domain.model.repositories.LeadRepository;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class LeadApplicationServiceTest {
 @Test void publicIntakePersistsThroughOwnedPort(){
  LeadRepository leads=mock(LeadRepository.class);DocumentRepository docs=mock(DocumentRepository.class);UnitOfWorkLock mutex=mock(UnitOfWorkLock.class);
  when(leads.save(any())).thenAnswer(inv->inv.getArgument(0));
  var app=new LeadApplicationService(leads,docs,mutex,new ObjectMapper());
  var json=app.submit("Empresa","Ana","ana@example.org","999999999","Basico","Cotizar");
  assertEquals("NEW",json.path("status").asText());verify(leads).save(any(LeadAggregate.class));verify(mutex).acquire();
 }
 @Test void statusUpdateRejectsUnknownLead(){
  LeadRepository repo=mock(LeadRepository.class);UnitOfWorkLock mutex=mock(UnitOfWorkLock.class);
  var app=new LeadQueryService(repo,mutex);
  when(repo.findById("MISSING")).thenReturn(Optional.empty());
  assertThrows(DomainException.class,()->app.updateStatus("MISSING",LeadStatus.CONTACTED));
 }
}
