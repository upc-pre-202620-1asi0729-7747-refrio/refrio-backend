package com.refrio.platform.alerting;
import com.refrio.platform.alerting.application.AlertingApplicationService;
import com.refrio.platform.alerting.domain.model.aggregates.IncidentAggregate;
import com.refrio.platform.alerting.domain.model.repositories.*;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class AlertingApplicationServiceTest {
 @Test void registersIncidentsThroughRepositoryPortAndRejectsOtherAudience(){
  IncidentRepository incidents=mock(IncidentRepository.class);AlertRepository alerts=mock(AlertRepository.class);UnitOfWorkLock mutex=mock(UnitOfWorkLock.class);
  var app=new AlertingApplicationService(alerts,incidents,mutex);
  AuthenticatedUser bodega=new AuthenticatedUser(2L,"bodega","Minorista");
  assertThrows(DomainException.class,()->app.update("ALT-1","action",null,"Inspeccionar",null,null,"P1","Supervisor",bodega));
  verifyNoInteractions(incidents);
  when(incidents.findById("ALT-1")).thenReturn(Optional.empty());
  when(incidents.save(any())).thenAnswer(inv->inv.getArgument(0));
  IncidentAggregate saved=app.update("ALT-1","action","Falla","Verificar cámara","inspect",null,"P1","Minorista",bodega);
  assertEquals("ACTION_RECORDED",saved.status());
  verify(mutex).acquire();verify(incidents).save(any(IncidentAggregate.class));
 }
}
