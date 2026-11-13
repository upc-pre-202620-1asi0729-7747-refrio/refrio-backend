package com.refrio.platform.events.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.events.application.EventsQueryService;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="Events")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/v1/events")
public class EventsV1Controller {
    private final EventsQueryService events;
    public EventsV1Controller(EventsQueryService events){this.events=events;}
    @GetMapping public List<OperationalEventAggregate> list(@AuthenticationPrincipal AuthenticatedUser user){return events.latest(user);}
}
