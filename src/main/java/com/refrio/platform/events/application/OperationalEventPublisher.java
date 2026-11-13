package com.refrio.platform.events.application;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import com.refrio.platform.events.domain.model.repositories.OperationalEventRepository;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationalEventPublisher {
    private final OperationalEventRepository events;
    public OperationalEventPublisher(OperationalEventRepository events){this.events=events;}
    /** Must execute in the caller's transaction, so events roll back with business changes. */
    @Transactional(propagation=org.springframework.transaction.annotation.Propagation.MANDATORY)
    public OperationalEventAggregate publish(String name,String aggregateId,String context,
                    AuthenticatedUser actor,String outcome,String description,String audience){
        return events.append(OperationalEventAggregate.userAction(name,aggregateId,description,context,
            actor.username(),outcome,audience));
    }
}
