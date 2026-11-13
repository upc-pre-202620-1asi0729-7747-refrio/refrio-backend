package com.refrio.platform.alerting.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.alerting.application.AlertingApplicationService;
import com.refrio.platform.alerting.domain.model.aggregates.*;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="Alerting")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/v1/alerting")
public class AlertingV1Controller {
    private final AlertingApplicationService app;
    public AlertingV1Controller(AlertingApplicationService app){this.app=app;}
    @GetMapping("/alerts") public List<AlertAggregate> alerts(@AuthenticationPrincipal AuthenticatedUser user){return app.alerts(user);}
    @GetMapping("/incidents") public List<IncidentAggregate> incidents(@AuthenticationPrincipal AuthenticatedUser user){return app.incidents(user);}
    public record IncidentTransition(String stage,String cause,String action,String decision,
        String verification,String subjectId,String audience){}
    @PatchMapping("/incidents/{id}") public IncidentAggregate update(@PathVariable String id,
          @RequestBody IncidentTransition request,@AuthenticationPrincipal AuthenticatedUser user){
        return app.update(id,request.stage(),request.cause(),request.action(),request.decision(),
            request.verification(),request.subjectId(),request.audience()==null?user.role():request.audience(),user);
    }
}
