package com.refrio.platform.inventory.domain.services;
import com.refrio.platform.inventory.domain.model.Allocation;
import com.refrio.platform.shared.domain.DomainException;
import java.time.LocalDate;
import java.util.*;
public final class FefoPolicy {
 private FefoPolicy(){}
 public record Batch(String id,String productId,int quantity,LocalDate expirationDate,String status,String audience){}
 public static boolean eligible(Batch batch,LocalDate today){return batch.quantity()>0&&batch.expirationDate()!=null&&batch.expirationDate().isAfter(today)&&!batch.status().toLowerCase(Locale.ROOT).matches(".*(inmovilizado|bloqueado|descartado|vencido|hold).*");}
 public static List<Allocation> plan(List<Batch> all,String productId,int quantity,LocalDate today){
  if(quantity<=0)throw new DomainException("Ingresa una cantidad entera mayor que cero.");
  int remaining=quantity;List<Allocation> result=new ArrayList<>();
  List<Batch> candidates=all.stream().filter(b->"Supervisor".equals(b.audience())&&productId.equals(b.productId())&&eligible(b,today)).sorted(Comparator.comparing(Batch::expirationDate).thenComparing(Batch::id)).toList();
  for(Batch b:candidates){int take=Math.min(remaining,b.quantity());if(take>0)result.add(new Allocation(b.id(),b.productId(),take));remaining-=take;if(remaining==0)break;}
  if(remaining>0)throw new DomainException("Stock apto insuficiente: faltan "+remaining+" unidades. Registra un lote o reduce la cantidad.");
  return List.copyOf(result);
 }
 public static void validatePicking(List<Allocation> plan,List<String> scanned){List<String> wanted=plan.stream().map(Allocation::batchId).distinct().toList();if(!wanted.equals(scanned))throw new DomainException("El código no coincide con la secuencia FEFO. Verifica los lotes y su orden.");}
}
