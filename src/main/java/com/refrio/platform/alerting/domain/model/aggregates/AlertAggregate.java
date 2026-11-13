package com.refrio.platform.alerting.domain.model.aggregates;
import com.refrio.platform.shared.domain.DomainException;

/** Stored alert; automatic rule-based alerts are calculated by the frontend separately. */
public record AlertAggregate(String id,String type,String time,String location,String severity,
                             String aggregateId,String audience,String status,String message,
                             String recommendedAction,String generatedAt,Boolean automatic) {
    public AlertAggregate {
        if(id==null||id.isBlank()||type==null||type.isBlank())
            throw new DomainException("Alerta debe tener código y tipo");
        if(severity==null||severity.isBlank())throw new DomainException("Severidad requerida");
    }
    public boolean visibleTo(String role){return "ALL".equals(audience)||role.equals(audience)
        ||(audience==null&&"Supervisor".equals(role));}
}
