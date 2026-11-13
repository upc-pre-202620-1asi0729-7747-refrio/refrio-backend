package com.refrio.platform.analytics.application;
import com.refrio.platform.analytics.domain.model.aggregates.AnalyticsReportAggregate;
import com.refrio.platform.analytics.domain.model.repositories.AnalyticsReadRepository;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsReportService {
    private final AnalyticsReadRepository source;
    public AnalyticsReportService(AnalyticsReadRepository source){this.source=source;}
    @Transactional(readOnly=true) public AnalyticsReportAggregate generate(AuthenticatedUser user){
        return AnalyticsReportAggregate.create(user.role(),source.calculate(user.role(),String.valueOf(user.id())));
    }
}
