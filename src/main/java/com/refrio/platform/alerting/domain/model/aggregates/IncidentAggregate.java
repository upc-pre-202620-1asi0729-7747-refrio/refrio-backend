package com.refrio.platform.alerting.domain.model.aggregates;
import com.refrio.platform.alerting.domain.model.IncidentState;
import com.refrio.platform.alerting.domain.model.valueobjects.IncidentDecision;
import com.refrio.platform.shared.domain.DomainException;
import java.time.Instant;

public record IncidentAggregate(String id,String status,String actor,String cause,String action,
       String decision,String openedAt,String updatedAt,String verification,
       String audience,String subjectId) {
    public IncidentAggregate {
        if(id==null||id.isBlank())throw new DomainException("Código de incidencia obligatorio");
        IncidentState.valueOf(status);
    }
    public static IncidentAggregate open(String id,String actor,String audience,String subjectId){
        String now=Instant.now().toString();
        return new IncidentAggregate(id,"IN_REVIEW",actor,"","","inspect",now,now,null,audience,subjectId);
    }
    public IncidentAggregate transition(String stage,String newCause,String newAction,String newDecision,
                                        String newVerification,String username){
        IncidentState old=IncidentState.valueOf(status);
        if(old==IncidentState.RESOLVED)throw new DomainException("La incidencia ya fue resuelta");
        IncidentState target=switch(stage){
            case "review" -> IncidentState.IN_REVIEW;
            case "action" -> IncidentState.ACTION_RECORDED;
            case "resolve" -> IncidentState.RESOLVED;
            default -> throw new DomainException("Etapa de incidencia inválida");
        };
        if(target==IncidentState.IN_REVIEW&&old!=IncidentState.IN_REVIEW)
            throw new DomainException("No se puede regresar a revisión");
        String actionValue=newAction==null||newAction.isBlank()?action:newAction;
        String causeValue=newCause==null?cause:newCause;
        String decisionValue=newDecision==null||newDecision.isBlank()?decision:newDecision;
        IncidentDecision.from(decisionValue);
        if(target==IncidentState.ACTION_RECORDED&&(actionValue==null||actionValue.isBlank()))
            throw new DomainException("Describe la acción correctiva");
        if(target==IncidentState.RESOLVED&&(old!=IncidentState.ACTION_RECORDED||newVerification==null||newVerification.isBlank()))
            throw new DomainException("Primero registra la acción y verifica la solución");
        return new IncidentAggregate(id,target.name(),username,causeValue,actionValue,decisionValue,
            openedAt,Instant.now().toString(),newVerification==null?verification:newVerification,audience,subjectId);
    }
}
