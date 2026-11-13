package com.refrio.platform.shared.domain;

/** A domain aggregate root is the only entry point for changing its consistency boundary. */
public abstract class AbstractDomainAggregateRoot<ID> {
    public abstract ID id();
    protected static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new DomainException(field + " es obligatorio");
        return value.trim();
    }
}
