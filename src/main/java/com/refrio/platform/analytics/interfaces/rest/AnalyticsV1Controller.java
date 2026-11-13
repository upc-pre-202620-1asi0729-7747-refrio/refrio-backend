package com.refrio.platform.analytics.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.analytics.application.AnalyticsReportService;
import com.refrio.platform.analytics.domain.model.aggregates.AnalyticsReportAggregate;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="Analytics")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/v1/analytics")
public class AnalyticsV1Controller {
    private final AnalyticsReportService reports;
    public AnalyticsV1Controller(AnalyticsReportService reports){this.reports=reports;}
    @GetMapping("/reports/operational") public AnalyticsReportAggregate report(@AuthenticationPrincipal AuthenticatedUser user){return reports.generate(user);}
}
