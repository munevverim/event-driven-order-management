package com.munevver.inventory_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OutboxRecord(
        Long id,
        @JsonProperty("aggregate_type") String aggregateType,
        @JsonProperty("aggregate_id") String aggregateId,
        @JsonProperty("event_type") String eventType,
        String payload,
        @JsonProperty("created_at") Long createdAt
) {
}
