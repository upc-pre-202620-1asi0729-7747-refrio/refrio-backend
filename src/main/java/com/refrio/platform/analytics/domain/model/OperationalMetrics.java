package com.refrio.platform.analytics.domain.model;

import com.refrio.platform.shared.domain.DomainException;

/** Read-side measurements derived from persisted facts, never made-up KPI values. */
public record OperationalMetrics(long productCount,long activeBatchCount,long shipmentCount,
                                 long deliveredCount,long recordedLossUnits,long telemetryCount) {
    public OperationalMetrics {
        if(productCount<0||activeBatchCount<0||shipmentCount<0||deliveredCount<0
                ||recordedLossUnits<0||telemetryCount<0||deliveredCount>shipmentCount) {
            throw new DomainException("Métricas operativas inconsistentes");
        }
    }
}
