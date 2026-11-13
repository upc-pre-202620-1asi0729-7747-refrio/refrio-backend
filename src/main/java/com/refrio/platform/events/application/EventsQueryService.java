package com.refrio.platform.events.application;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import com.refrio.platform.events.domain.model.repositories.OperationalEventRepository;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class EventsQueryService {
    private final OperationalEventRepository repo;
    public EventsQueryService(OperationalEventRepository repo){this.repo=repo;}
    @Transactional(readOnly=true) public List<OperationalEventAggregate> latest(AuthenticatedUser user){
        return repo.findAll().stream().filter(e->e.visibleTo(user.role(),user.supervisor()))
            .sorted(Comparator.comparing(OperationalEventAggregate::occurredAt,Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
    }
}
