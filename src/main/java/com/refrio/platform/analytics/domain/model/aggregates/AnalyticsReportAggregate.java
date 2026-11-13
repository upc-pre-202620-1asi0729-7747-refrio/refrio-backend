package com.refrio.platform.analytics.domain.model.aggregates;
import com.refrio.platform.analytics.domain.model.OperationalMetrics;
import com.refrio.platform.analytics.domain.model.valueobjects.Percentage;
import java.time.Instant;

/** A read-side report calculated from transactional facts, not fabricated KPIs. */
public record AnalyticsReportAggregate(String audience,String generatedAt,OperationalMetrics metrics,
                                      Percentage deliveryRate) {
    public static AnalyticsReportAggregate create(String audience,OperationalMetrics metrics){
        return new AnalyticsReportAggregate(audience,Instant.now().toString(),metrics,
            Percentage.ratio(metrics.deliveredCount(),metrics.shipmentCount()));
    }
}
