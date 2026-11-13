package com.refrio.platform.traceability;
import com.refrio.platform.traceability.domain.model.TemperatureEvidence;
import com.refrio.platform.traceability.domain.model.valueobjects.CertificateFingerprint;
import com.refrio.platform.shared.domain.DomainException;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TemperatureEvidenceTest {
 @Test void evidenceCalculatesOnlyFinitePhysicalReadings(){
  var evidence=TemperatureEvidence.from(List.of(2.0,4.0,6.0));
  assertEquals(2.0,evidence.min());assertEquals(6.0,evidence.max());assertEquals(4.0,evidence.average());
  assertEquals(3,evidence.samples());
  assertThrows(DomainException.class,()->TemperatureEvidence.from(List.of()));
  assertThrows(DomainException.class,()->TemperatureEvidence.from(List.of(1.0,Double.NaN)));
  assertThrows(DomainException.class,()->TemperatureEvidence.from(List.of(Double.POSITIVE_INFINITY)));
 }
 @Test void fingerprintIsDeterministicSha256Checksum(){
  String a=CertificateFingerprint.of("shipment:S1|temp:4.0").hex();
  assertEquals(64,a.length());assertEquals(a,CertificateFingerprint.of("shipment:S1|temp:4.0").hex());
  assertNotEquals(a,CertificateFingerprint.of("shipment:S2|temp:4.0").hex());
 }
}
