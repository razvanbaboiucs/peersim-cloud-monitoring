package com.razvanbaboiu.cloudmonitoring.protocol;

import lombok.Getter;
import lombok.Setter;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Node;

@Setter
@Getter
public class CloudServiceProtocol implements CDProtocol {
    // CONFIGURATION PROPERTIES
    private final int linkableProtocolId;
    private final int loadBalancerProtocolId;

    // CONTROLLABLE PROPERTIES
    private int projectId;
    private double cpu;
    private double memory;
    private boolean isRunning = false;
    private int roundRobinIndex = 0;

    public CloudServiceProtocol(String prefix) {
        linkableProtocolId = Configuration.getPid(prefix + ".linkable_proto");
        loadBalancerProtocolId = Configuration.getPid(prefix + ".load_balancer_proto");
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if (!isRunning) return;

        // Get Linkable protocol to access Load Balancers
        Linkable linkable = (Linkable) node.getProtocol(linkableProtocolId);
        if (linkable.degree() == 0) return;

        // Round-robin selection of Load Balancers
        Node targetLb = null;
        int attempts = 0;
        while (attempts < linkable.degree()) {
            targetLb = linkable.getNeighbor(roundRobinIndex % linkable.degree());
            roundRobinIndex++;
            if (targetLb.isUp()) break;
            attempts++;
        }

        if (targetLb == null || !targetLb.isUp()) return;

        LoadBalancerProtocol lbp = (LoadBalancerProtocol) targetLb.getProtocol(loadBalancerProtocolId);
        lbp.receiveMetric(node, targetLb, projectId, cpu, memory);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
