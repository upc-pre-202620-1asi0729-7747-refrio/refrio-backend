package com.refrio.platform.traceability.domain.services;

import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.traceability.domain.model.TemperatureEvidence;
import java.time.Instant;
import java.util.*;

/** Never certify a delivery from a reading measured before it was dispatched. */
public final class ShipmentTemperaturePolicy {
    private ShipmentTemperaturePolicy(){}
    public record Sample(String productId, String audience, double celsius, Instant at) {}
    public static TemperatureEvidence forShipment(Set<String> productIds,Instant start,Instant end,List<Sample> samples){
        if(productIds==null || productIds.isEmpty() || start==null || end==null || end.isBefore(start))
            throw new DomainException("Invalid shipment evidence interval");
        List<Double> values=new ArrayList<>();
        Set<String> observed=new HashSet<>();
        for(Sample sample:samples){
            if(sample==null || sample.at()==null || sample.productId()==null)continue;
            if(!productIds.contains(sample.productId()) || !"Supervisor".equals(sample.audience()))continue;
            if(sample.at().isBefore(start)||sample.at().isAfter(end))continue;
            values.add(sample.celsius());observed.add(sample.productId());
        }
        if(!observed.containsAll(productIds))
            throw new DomainException("Missing shipment-period temperature readings for some products");
        return TemperatureEvidence.from(values);
    }
}
