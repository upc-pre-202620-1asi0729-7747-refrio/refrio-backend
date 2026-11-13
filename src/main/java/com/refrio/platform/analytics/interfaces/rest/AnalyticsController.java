package com.refrio.platform.analytics.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.analytics.domain.model.OperationalMetrics;
import com.refrio.platform.analytics.application.AnalyticsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name="Analytics")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/analytics")
public class AnalyticsController {
 private final AnalyticsService service;
 public AnalyticsController(AnalyticsService service){this.service=service;}
 @GetMapping("/summary") public OperationalMetrics summary(@AuthenticationPrincipal AuthenticatedUser user){return service.summary(user);}
}
