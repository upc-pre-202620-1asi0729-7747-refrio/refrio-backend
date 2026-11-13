package com.refrio.platform.traceability;
import com.refrio.platform.traceability.application.CertificationService;
import com.refrio.platform.traceability.domain.model.repositories.CertificateRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class CertificationServiceTest {
 @Test void certificateRequiresDeliveryBeforeAnyPersistedEvidence(){
  DocumentRepository documents=mock(DocumentRepository.class);UnitOfWorkLock lock=mock(UnitOfWorkLock.class);CertificateRepository certs=mock(CertificateRepository.class);
  when(documents.get("shipments","S1")).thenReturn(new ObjectMapper().createObjectNode().put("id","S1").put("status","In Transit").put("recipientId","2"));
  CertificationService app=new CertificationService(documents,lock,certs);
  assertThrows(DomainException.class,()->app.certify("S1",new AuthenticatedUser(2L,"bodega","Minorista")));
  verify(certs,never()).save(any());
 }
}
