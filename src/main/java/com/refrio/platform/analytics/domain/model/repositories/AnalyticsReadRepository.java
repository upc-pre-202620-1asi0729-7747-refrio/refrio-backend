package com.refrio.platform.analytics.domain.model.repositories;
import com.refrio.platform.analytics.domain.model.OperationalMetrics;
public interface AnalyticsReadRepository {
    OperationalMetrics calculate(String audience,String recipientId);
}
