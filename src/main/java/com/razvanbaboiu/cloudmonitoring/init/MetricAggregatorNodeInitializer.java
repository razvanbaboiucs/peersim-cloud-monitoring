package com.razvanbaboiu.cloudmonitoring.init;

import com.razvanbaboiu.cloudmonitoring.protocol.MetricAggregatorProtocol;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.config.Configuration;
import peersim.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetricAggregatorNodeInitializer implements Control {

    private final int metricAggregatorProtoId;
    private final int totalProjects;

    public MetricAggregatorNodeInitializer(String prefix) {
        metricAggregatorProtoId = Configuration.getPid(prefix + ".metric_aggregator_proto");
        totalProjects = Configuration.getInt("total_projects", 10);
    }

    @Override
    public boolean execute() {
        int totalNodes = Network.size();
        int metricAggregatorStartNode =
                (int) (totalNodes * (NodeTypeRatio.CLOUD_SERVICE_NODE_RATIO + NodeTypeRatio.MONITORING_AGENT_NODE_RATIO));
        int totalMetricAggregatorNodes = (int) (totalNodes * NodeTypeRatio.METRIC_AGGREGATOR_NODE_RATIO);
        int totalProjectsPerNode = totalProjects / totalMetricAggregatorNodes;

        List<Integer> projects = new ArrayList<>();
        for (int p = 1; p <= totalProjects; p++) {
            projects.add(p);
        }
        Collections.shuffle(projects, CommonState.r);

        var projectIndex = 0;
        for (int i = metricAggregatorStartNode; i < totalNodes; i++) {
            Node node = Network.get(i);
            MetricAggregatorProtocol protocol = (MetricAggregatorProtocol) node.getProtocol(metricAggregatorProtoId);

            List<Integer> projectForNode = new ArrayList<>();
            for (int j = 0; j < totalProjectsPerNode; j++) {
                projectForNode.add(projects.get(projectIndex));
                ++projectIndex;
            }
            protocol.setAssignedProjects(projectForNode);
            protocol.setRunning(true);
        }

        return false;
    }
}
