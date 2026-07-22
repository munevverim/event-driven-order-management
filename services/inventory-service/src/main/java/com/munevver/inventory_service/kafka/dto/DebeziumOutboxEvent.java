package com.munevver.inventory_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DebeziumOutboxEvent(Payload payload) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Payload(OutboxRecord after) {}
}
