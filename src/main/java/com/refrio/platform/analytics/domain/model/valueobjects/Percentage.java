package com.refrio.platform.analytics.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;
public record Percentage(double value) {
    public Percentage {
        if(!Double.isFinite(value)||value<0||value>100)throw new DomainException("Porcentaje inválido");
    }
    public static Percentage ratio(long numerator,long denominator){
        if(denominator<0||numerator<0||numerator>denominator)throw new DomainException("Métricas inconsistentes");
        return new Percentage(denominator==0?0:(100.0*numerator/denominator));
    }
}
