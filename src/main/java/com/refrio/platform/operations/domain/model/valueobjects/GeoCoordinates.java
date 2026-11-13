package com.refrio.platform.operations.domain.model.valueobjects;

import com.refrio.platform.shared.domain.DomainException;

/** Explicit latitude and longitude value object; validation is domain-owned. */
public record GeoCoordinates(double latitude, double longitude) {
    public GeoCoordinates {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
            || !Double.isFinite(longitude) || longitude < -180 || longitude > 180)
            throw new DomainException("Invalid geospatial coordinates");
    }
}
