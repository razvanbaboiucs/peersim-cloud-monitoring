package com.razvanbaboiu.cloudmonitoring.utils;

import lombok.Builder;

@Builder
public record Metric(String id, Long modifiedAt, Integer projectId, Long nodeId, Double cpu, Double memory) {
}
