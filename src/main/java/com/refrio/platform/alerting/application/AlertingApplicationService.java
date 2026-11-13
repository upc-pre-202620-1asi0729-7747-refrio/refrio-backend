package com.refrio.platform.alerting.application;
import com.refrio.platform.alerting.domain.model.aggregates.*;
import com.refrio.platform.alerting.domain.model.repositories.*;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class AlertingApplicationService {
    private final AlertRepository alerts;
    private final IncidentRepository incidents;
    private final UnitOfWorkLock mutex;
    public AlertingApplicationService(AlertRepository alerts,IncidentRepository incidents,UnitOfWorkLock mutex){
        this.alerts=alerts;this.incidents=incidents;this.mutex=mutex;
    }
    @Transactional(readOnly=true) public List<AlertAggregate> alerts(AuthenticatedUser user){
        return alerts.findAll().stream().filter(a->a.visibleTo(user.role())).toList();
    }
    @Transactional(readOnly=true) public List<IncidentAggregate> incidents(AuthenticatedUser user){
        return incidents.findAll().stream().filter(i->user.supervisor()||user.role().equals(i.audience())
            ||user.username().equals(i.actor())).toList();
    }
    @Transactional public IncidentAggregate update(String id,String stage,String cause,String action,String decision,
                   String verification,String subjectId,String audience,AuthenticatedUser actor){
        if(!actor.supervisor()&&!actor.role().equals(audience))throw new ForbiddenOperationException("No autorizado para la audiencia");
        mutex.acquire();
        IncidentAggregate current=incidents.findById(id).orElseGet(()->IncidentAggregate.open(id,actor.username(),audience,subjectId));
        if(current.audience()!=null&&!current.audience().equals(audience))throw new DomainException("La audiencia no coincide");
        return incidents.save(current.transition(stage,cause,action,decision,verification,actor.username()));
    }
}
