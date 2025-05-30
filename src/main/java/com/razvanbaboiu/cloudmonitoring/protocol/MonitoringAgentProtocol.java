package com.razvanbaboiu.cloudmonitoring.protocol;

import com.razvanbaboiu.cloudmonitoring.data.Metric;
import com.razvanbaboiu.cloudmonitoring.data.MetricDatabase;
import lombok.Getter;
import lombok.Setter;
import peersim.core.Protocol;

@Getter
@Setter
public class MonitoringAgentProtocol implements Protocol {
    // CONTROLLABLE PROPERTIES
    private final MetricDatabase db;
    private boolean isRunning;

    public MonitoringAgentProtocol(String prefix) {
        db = MetricDatabase.getInstance();
    }

    public void pushData(long nodeId, int projectId, double cpu, double memory) {
        if (!isRunning) return;

        db.insert(Metric.builder()
                .projectId(projectId)
                .nodeId(nodeId)
                .cpu(cpu)
                .memory(memory)
                .build()
        );
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}