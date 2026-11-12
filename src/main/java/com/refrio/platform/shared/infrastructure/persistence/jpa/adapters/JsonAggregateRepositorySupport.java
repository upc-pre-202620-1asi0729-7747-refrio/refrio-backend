package com.refrio.platform.shared.infrastructure.persistence.jpa.adapters;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JPA-backed aggregate mapping. Every bounded context owns an adapter and a typed repository port.
 * This adapter uses resource_records as a compatibility storage model for the existing Angular API.
 * The document store is infrastructure only and never appears inside the domain models.
 */
public abstract class JsonAggregateRepositorySupport<T> {
    private final String resource;
    private final Class<T> modelType;
    protected final DocumentRepository documents;
    private final ObjectMapper mapper;

    protected JsonAggregateRepositorySupport(String resource, Class<T> modelType,
                                             DocumentRepository documents, ObjectMapper mapper) {
        this.resource = resource;
        this.modelType = modelType;
        this.documents = documents;
        this.mapper = mapper.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<T> findAll() {
        ArrayNode all = documents.all(resource);
        List<T> result = new ArrayList<>(all.size());
        all.forEach(node -> result.add(mapper.convertValue(node, modelType)));
        return List.copyOf(result);
    }

    public Optional<T> findById(String id) {
        return documents.find(resource, id).map(node -> mapper.convertValue(node, modelType));
    }

    protected T save(String id, T aggregate) {
        ObjectNode serialized = mapper.valueToTree(aggregate);
        // Preserve frontend-only fields not modeled by this aggregate.
        ObjectNode previous = documents.find(resource, id).orElseGet(documents::object);
        previous.setAll(serialized);
        previous.put("id", id);
        documents.put(resource, previous);
        return aggregate;
    }

    protected void remove(String id) { documents.delete(resource, id); }
}
