package com.refrio.platform.operations;
import com.refrio.platform.operations.application.OperationsQueryService;
import com.refrio.platform.operations.domain.model.aggregates.ShipmentAggregate;
import com.refrio.platform.operations.domain.model.repositories.*;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.shared.domain.DomainException;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class OperationsQueryServiceTest {
 @Test void retailerSeesOnlyAssignedShipments(){
  ShipmentRepository shipments=mock(ShipmentRepository.class);BusinessPartnerRepository partners=mock(BusinessPartnerRepository.class);
  var app=new OperationsQueryService(shipments,partners);
  when(shipments.findAll()).thenReturn(List.of(new ShipmentAggregate("S1","Preparing","2","C1",List.of(),List.of(),null),
          new ShipmentAggregate("S2","Preparing","3","C2",List.of(),List.of(),null)));
  AuthenticatedUser retailer=new AuthenticatedUser(2L,"bodega","Minorista");
  assertEquals(List.of("S1"),app.shipments(retailer).stream().map(ShipmentAggregate::id).toList());
  assertThrows(DomainException.class,()->app.clients(retailer));
 }
}
