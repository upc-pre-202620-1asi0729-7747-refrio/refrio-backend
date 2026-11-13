package com.refrio.platform.analytics.application;

import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.analytics.domain.model.OperationalMetrics;
import com.refrio.platform.analytics.domain.model.repositories.AnalyticsReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {
    private final AnalyticsReadRepository metrics;
    public AnalyticsService(AnalyticsReadRepository metrics){this.metrics=metrics;}
    @Transactional(readOnly=true) public OperationalMetrics summary(AuthenticatedUser user){
        return metrics.calculate(user.role(),String.valueOf(user.id()));
    }
}
