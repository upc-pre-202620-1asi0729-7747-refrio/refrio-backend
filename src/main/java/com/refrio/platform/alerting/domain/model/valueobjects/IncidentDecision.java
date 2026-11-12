package com.refrio.platform.alerting.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;
public enum IncidentDecision {
    INSPECT("inspect"), HOLD("hold");
    private final String wire;
    IncidentDecision(String wire){this.wire=wire;}
    public String wire(){return wire;}
    public static IncidentDecision from(String text){
        for(var decision:values())if(decision.wire.equals(text))return decision;
        throw new DomainException("Decisión de incidencia inválida");
    }
}
