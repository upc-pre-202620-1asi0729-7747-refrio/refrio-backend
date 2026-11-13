package com.refrio.platform.traceability.domain.model.aggregates;
import com.refrio.platform.shared.domain.DomainException;

public record ColdChainCertificateAggregate(String id,String shipmentId,String batchCode,
    String productName,Double minTemp,Double maxTemp,Double avgTemp,Integer samples,
    String startDate,String endDate,String warehouseName,String qrCode,String digitalSignature,
    String signatureType,Integer losses) {
    public ColdChainCertificateAggregate {
        if(id==null||id.isBlank()||shipmentId==null||shipmentId.isBlank())
            throw new DomainException("Certificado requiere ID y despacho");
        if(samples!=null&&samples<1)throw new DomainException("El certificado requiere lecturas");
        if(minTemp!=null&&maxTemp!=null&&minTemp>maxTemp)
            throw new DomainException("El rango térmico es inconsistente");
    }
    public boolean hasReadings(){return samples!=null&&samples>0;}
    public boolean isGenericRangeCompliant(){return hasReadings()&&maxTemp!=null&&maxTemp<=8;}
}
