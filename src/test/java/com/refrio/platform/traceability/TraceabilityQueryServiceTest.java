package com.refrio.platform.traceability;
import com.refrio.platform.traceability.application.TraceabilityQueryService;
import com.refrio.platform.traceability.domain.model.aggregates.ColdChainCertificateAggregate;
import com.refrio.platform.traceability.domain.model.repositories.CertificateRepository;
import com.refrio.platform.operations.domain.model.aggregates.ShipmentAggregate;
import com.refrio.platform.operations.domain.model.repositories.ShipmentRepository;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.shared.domain.DomainException;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class TraceabilityQueryServiceTest {
 @Test void qrLookupIsRestrictedToShipmentRecipient(){
  CertificateRepository certs=mock(CertificateRepository.class);ShipmentRepository shipments=mock(ShipmentRepository.class);
  var query=new TraceabilityQueryService(certs,shipments);
  var cert=new ColdChainCertificateAggregate("C1","S1","B1","Leche",2.0,6.0,4.0,3,"2026-11-09","2026-11-09","Lima","QR1","sha256","CHECKSUM",0);
  when(certs.findByQrCode("QR1")).thenReturn(Optional.of(cert));
  when(shipments.findById("S1")).thenReturn(Optional.of(new ShipmentAggregate("S1","Delivered","2","C1",List.of(),List.of(),null)));
  assertEquals("C1",query.byQr("QR1",new AuthenticatedUser(2L,"bodega","Minorista")).id());
  assertThrows(DomainException.class,()->query.byQr("QR1",new AuthenticatedUser(3L,"intruso","Minorista")));
 }
}
