package com.refrio.platform.traceability.domain.model;
import com.refrio.platform.shared.domain.DomainException;
import java.util.*;
public record TemperatureEvidence(double min,double max,double average,int samples){
 public static TemperatureEvidence from(List<Double> samples){
  if(samples==null||samples.isEmpty())throw new DomainException("No hay lecturas de temperatura para certificar");
  if(samples.stream().anyMatch(x->x==null||!Double.isFinite(x)||x < -100||x > 100))
      throw new DomainException("La evidencia contiene temperaturas inválidas");
  return new TemperatureEvidence(samples.stream().mapToDouble(x->x).min().orElseThrow(),samples.stream().mapToDouble(x->x).max().orElseThrow(),samples.stream().mapToDouble(x->x).average().orElseThrow(),samples.size());
 }
}
