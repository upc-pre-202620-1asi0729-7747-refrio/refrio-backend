package com.refrio.platform.operations.domain.services;
import com.refrio.platform.inventory.domain.model.Allocation;
import com.refrio.platform.shared.domain.DomainException;
import java.util.*;
public final class ShipmentPolicy {
 private ShipmentPolicy(){}
 public static String advance(String current){return switch(current){case "Preparing"->"In Transit";case "In Transit","Delayed"->"Awaiting Receipt";default->throw new DomainException("El despacho no permite avanzar desde "+current);};}
 public static void validateReceipt(String status,boolean alreadyReceived,List<Allocation> expected,List<Allocation> actual,String decision,String notes){
  if(!"Awaiting Receipt".equals(status)||alreadyReceived)throw new DomainException("Este envío no está pendiente de recepción.");
  if(!Set.of("accepted","observed","rejected").contains(decision))throw new DomainException("Selecciona una decisión de recepción.");
  if(expected.isEmpty()||expected.size()!=actual.size())throw new DomainException("Falta el detalle de cantidades por lote.");
  for(int i=0;i<expected.size();i++){Allocation e=expected.get(i),a=actual.get(i);
   if(!e.batchId().equals(a.batchId())||!e.productId().equals(a.productId())||a.quantity()<0||a.quantity()>e.quantity())throw new DomainException("La cantidad recibida debe estar entre cero y la cantidad enviada por lote.");
   if("accepted".equals(decision)&&a.quantity()!=e.quantity())throw new DomainException("Para cantidades parciales selecciona Aceptar con observaciones.");
   if("rejected".equals(decision)&&a.quantity()!=0)throw new DomainException("Un rechazo no incorpora unidades al inventario.");
  }
  if(!"accepted".equals(decision)&&(notes==null||notes.isBlank()))throw new DomainException("Describe el motivo de la observación o rechazo.");
  if("observed".equals(decision)&&actual.stream().noneMatch(a->a.quantity()>0))throw new DomainException("Si no recibiste unidades, selecciona Rechazar.");
 }
}
