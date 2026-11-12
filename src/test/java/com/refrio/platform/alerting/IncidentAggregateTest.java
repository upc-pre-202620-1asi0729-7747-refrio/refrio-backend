package com.refrio.platform.alerting;
import com.refrio.platform.alerting.domain.model.aggregates.*;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class IncidentAggregateTest {
 @Test void incidentMustRecordActionAndVerificationBeforeResolution(){
  var opened=IncidentAggregate.open("ALT-1","ana","Minorista","P1");
  assertEquals("IN_REVIEW",opened.status());
  assertThrows(DomainException.class,()->opened.transition("resolve","Causa","Ajuste","inspect","OK","ana"));
  assertThrows(DomainException.class,()->opened.transition("action","Causa","","inspect",null,"ana"));
  var action=opened.transition("action","Termómetro defectuoso","Reemplazar termómetro","inspect",null,"ana");
  assertThrows(DomainException.class,()->action.transition("review",null,null,null,null,"ana"));
  var resolved=action.transition("resolve",null,null,null,"Equipo verificado","ana");
  assertEquals("RESOLVED",resolved.status());
  assertThrows(DomainException.class,()->resolved.transition("action",null,"Reabrir",null,null,"ana"));
 }
 @Test void alertVisibilityFollowsAudience(){
  var a=new AlertAggregate("ALT-1","Temperature","today","Lima","Critical","P1","Minorista","OPEN","Temperatura alta","Inspeccionar",null,false);
  assertTrue(a.visibleTo("Minorista"));assertFalse(a.visibleTo("Supervisor"));
 }
}
