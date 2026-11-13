package com.refrio.platform.publicportal;
import com.refrio.platform.publicportal.domain.model.LeadStatus;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class LeadAggregateTest {
 @Test void leadRejectsInvalidContactAndBackwardStage(){
  var lead=LeadAggregate.submit("MiniMarket","Ana","ana@example.org","988887777","Basico","Quiero una demo");
  assertEquals("NEW",lead.status());
  var qualified=lead.moveTo(LeadStatus.CONTACTED).moveTo(LeadStatus.QUALIFIED);
  assertThrows(DomainException.class,()->qualified.moveTo(LeadStatus.NEW));
  assertThrows(DomainException.class,()->qualified.moveTo(LeadStatus.CLOSED).moveTo(LeadStatus.QUALIFIED));
  assertThrows(DomainException.class,()->LeadAggregate.submit("MiniMarket","Ana","correo-inválido","","Basico",""));
 }
}
