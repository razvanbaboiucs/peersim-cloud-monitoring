package com.razvanbaboiu.cloudmonitoring.protocol;

import com.razvanbaboiu.cloudmonitoring.utils.Metric;
import com.razvanbaboiu.cloudmonitoring.utils.MetricDatabase;
import lombok.Getter;
import lombok.Setter;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;

@Getter
@Setter
public class MonitoringAgentProtocol implements CDProtocol {

    private final int cloudServiceProtoId;
    private final int metricAggregatorProtoId;
    private boolean isRunning;

    private final MetricDatabase db;

    public MonitoringAgentProtocol(String prefix) {
        cloudServiceProtoId = Configuration.getPid(prefix + ".cloudservice_proto");
        metricAggregatorProtoId = Configuration.getPid(prefix + ".aggregator_proto");

        db = MetricDatabase.getInstance();
    }

    @Override
    public void nextCycle(Node monitoringNode, int protocolID) {
        if (!isRunning) {
            return;
        }
        // Get Linkable protocol to access monitored CloudServiceNodes
        int linkableId = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) monitoringNode.getProtocol(linkableId);

        // Iterate through all connected CloudServiceNodes
        for (int i = 0; i < linkable.degree(); i++) {
            Node cloudServiceNode = linkable.getNeighbor(i);

            // Skip inactive nodes
            if (!cloudServiceNode.isUp()) continue;

            // Get metrics from CloudServiceProtocol
            CloudServiceProtocol csp = (CloudServiceProtocol)
                    cloudServiceNode.getProtocol(cloudServiceProtoId);

            db.insert(
                    Metric.builder()
                            .projectId(csp.getProjectId())
                            .nodeId(cloudServiceNode.getID())
                            .cpu(csp.getCpu())
                            .memory(csp.getMemory())
                            .build()
            );
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}