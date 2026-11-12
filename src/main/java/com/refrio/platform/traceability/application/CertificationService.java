package com.refrio.platform.traceability.application;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.traceability.domain.model.TemperatureEvidence;
import com.refrio.platform.traceability.domain.services.CertificateIssuancePolicy;
import com.refrio.platform.traceability.domain.services.ShipmentTemperaturePolicy;
import com.refrio.platform.traceability.domain.model.valueobjects.CertificateFingerprint;
import com.refrio.platform.traceability.domain.model.aggregates.ColdChainCertificateAggregate;
import com.refrio.platform.traceability.domain.model.repositories.CertificateRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
@Service
public class CertificationService {
 private final DocumentRepository store;private final UnitOfWorkLock mutex;private final CertificateRepository certificates;
 public CertificationService(DocumentRepository store,UnitOfWorkLock mutex,CertificateRepository certificates){this.store=store;this.mutex=mutex;this.certificates=certificates;}
 @Transactional public ObjectNode certify(String shipmentId,AuthenticatedUser user){
  mutex.acquire();ObjectNode shipment=store.get("shipments",shipmentId);
  CertificateIssuancePolicy.validate(shipment.path("status").asText(),
      user.supervisor() || String.valueOf(user.id()).equals(shipment.path("recipientId").asText()));
  var previous=certificates.findByShipmentId(shipmentId);if(previous.isPresent())return store.get("certificates",previous.get().id());
  Set<String> productIds=new HashSet<>();
  shipment.path("allocations").forEach(x->productIds.add(x.path("productId").asText()));
  Instant start, end;
  try {
   start=Instant.parse(shipment.path("createdAt").asText());
   end=Instant.parse(shipment.path("receipt").path("at").asText());
  }catch(Exception e){throw new DomainException("Shipment lacks a valid dispatch-to-receipt interval");}
  List<ShipmentTemperaturePolicy.Sample> samples=new ArrayList<>();
  for(JsonNode reading:store.all("telemetry")) {
   if(!reading.path("value").isNumber())continue;
   try {samples.add(new ShipmentTemperaturePolicy.Sample(
    reading.path("productId").asText(),reading.path("audience").asText(),
    reading.path("value").asDouble(),Instant.parse(reading.path("at").asText())));}
   catch(Exception ignored) { /* Legacy malformed telemetry cannot be certified. */ }
  }
  TemperatureEvidence evidence=ShipmentTemperaturePolicy.forShipment(productIds,start,end,samples);
  ObjectNode certificate=store.object();
  String id=UUID.randomUUID().toString();certificate.put("id",id);certificate.put("shipmentId",shipmentId);
  certificate.put("batchCode",shipment.path("allocations").path(0).path("batchId").asText());certificate.put("productName",shipment.path("product").asText());
  certificate.put("minTemp",evidence.min());certificate.put("maxTemp",evidence.max());certificate.put("avgTemp",evidence.average());certificate.put("samples",evidence.samples());
  certificate.put("startDate",shipment.path("createdAt").asText());certificate.put("endDate",Instant.now().toString());certificate.put("warehouseName","Refrio");
  certificate.put("losses",0);certificate.put("qrCode","REFRIO-CERT-"+id);
  // Integrity digest, not a digitally signed certificate.
  certificate.put("digitalSignature",checksum(shipmentId+"|"+id+"|"+evidence.min()+"|"+evidence.max()+"|"+evidence.average()));
  certificate.put("signatureType","SHA-256-CHECKSUM-NOT-A-DIGITAL-SIGNATURE");
  ColdChainCertificateAggregate aggregate=new ColdChainCertificateAggregate(id,shipmentId,
      certificate.path("batchCode").asText(),certificate.path("productName").asText(),
      evidence.min(),evidence.max(),evidence.average(),evidence.samples(),
      certificate.path("startDate").asText(),certificate.path("endDate").asText(),
      certificate.path("warehouseName").asText(),certificate.path("qrCode").asText(),
      certificate.path("digitalSignature").asText(),certificate.path("signatureType").asText(),0);
  certificates.save(aggregate);return store.get("certificates",id);
 }
 @Transactional(readOnly=true) public ArrayNode all(AuthenticatedUser user){ArrayNode result=store.array();for(JsonNode x:store.all("certificates")){
  ObjectNode shipment=store.get("shipments",x.path("shipmentId").asText());if(user.supervisor()||String.valueOf(user.id()).equals(shipment.path("recipientId").asText()))result.add(x);
 }return result;}
 @Transactional(readOnly=true) public ObjectNode qr(String qr,AuthenticatedUser user){for(JsonNode certificate:all(user))if(qr.equals(certificate.path("qrCode").asText()))return (ObjectNode)certificate;throw new DomainException("Certificado inexistente");}
 private String checksum(String input){return CertificateFingerprint.of(input).hex();}
}
