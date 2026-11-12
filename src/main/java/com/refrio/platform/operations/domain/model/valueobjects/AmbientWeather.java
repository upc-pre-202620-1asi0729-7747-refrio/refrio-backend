package com.refrio.platform.operations.domain.model.valueobjects;

import com.refrio.platform.shared.domain.DomainException;
import java.time.Instant;

/** Ambient temperature is NOT refrigeration-compartment telemetry. */
public record AmbientWeather(GeoCoordinates location, double temperatureCelsius,
                             int humidityPercent, Instant observedAt, String provider) {
    public AmbientWeather {
        if (location==null || !Double.isFinite(temperatureCelsius)
            || humidityPercent<0 || humidityPercent>100 || observedAt==null
            || provider==null || provider.isBlank())
            throw new DomainException("Invalid external weather observation");
    }
}
