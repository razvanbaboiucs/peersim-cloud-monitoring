package com.razvanbaboiu.cloudmonitoring.protocol;

import com.razvanbaboiu.cloudmonitoring.utils.Metric;
import com.razvanbaboiu.cloudmonitoring.utils.MetricDatabase;
import lombok.Setter;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;

import java.util.List;

public class MetricAggregatorProtocol implements CDProtocol {

    public static final int WINDOW_SIZE = 10;
    // Configuration parameters
    private final double cpuThreshold;
    private final double memoryThreshold;
    @Setter
    private boolean isRunning = false;
    private final MetricDatabase db;
    @Setter
    private List<Integer> assignedProjects;

    public MetricAggregatorProtocol(String prefix) {
        cpuThreshold = Configuration.getDouble(prefix + ".cpu_threshold", 90.0);
        memoryThreshold = Configuration.getDouble(prefix + ".memory_threshold", 90.0);
        db = MetricDatabase.getInstance();
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if (!isRunning) {
            return;
        }
        assignedProjects.forEach(projectId -> {
            List<Metric> metricsForProject = db.getMetricsForProject(projectId, WINDOW_SIZE);
            var size = metricsForProject.isEmpty() ? 1 : metricsForProject.size();
            double averageCpu = metricsForProject.stream().map(Metric::cpu).reduce(Double::sum).orElse(0.0) / size;
            double averageMemory = metricsForProject.stream().map(Metric::memory).reduce(Double::sum).orElse(0.0) / size;

            if (averageCpu >= cpuThreshold) {
                System.out.println("ALERT [CPU] Project: " + projectId + "; Avg CPU: " + averageCpu);
            }

            if (averageMemory >= memoryThreshold) {
                System.out.println("ALERT [MEMORY] Project: " + projectId + "; Avg MEMORY: " + averageMemory);
            }
        });
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}