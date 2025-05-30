package com.razvanbaboiu.cloudmonitoring.protocol;

import lombok.Setter;
import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.core.Protocol;

public class LoadBalancerProtocol implements Protocol {
    // CONFIGURATION PROPERTIES
    private final int linkableProtocolId;
    private final int monitoringAgentProtocolId;

    // CONTROLLABLE PROPERTIES
    private int roundRobinIndex = 0;
    @Setter
    private boolean isRunning = false;

    public LoadBalancerProtocol(String prefix) {
        linkableProtocolId = Configuration.getPid(prefix + ".linkable_proto");
        monitoringAgentProtocolId = Configuration.getPid(prefix + ".monitoring_agent_proto");
    }

    public void receiveMetric(Node cspNode, Node lbNode, int projectId, double cpu, double memory) {
        if (!isRunning) return;

        // Get Linkable protocol to access Monitoring Agents
        Linkable linkable = (Linkable) lbNode.getProtocol(linkableProtocolId);
        if (linkable.degree() == 0) return;

        // Round-robin selection of Monitoring Agents
        Node targetMap = null;
        int attempts = 0;
        while (attempts < linkable.degree()) {
            targetMap = linkable.getNeighbor(roundRobinIndex % linkable.degree());
            roundRobinIndex++;
            if (targetMap.isUp()) break;
            attempts++;
        }

        if (targetMap == null || !targetMap.isUp()) return;

        MonitoringAgentProtocol map = (MonitoringAgentProtocol) targetMap.getProtocol(monitoringAgentProtocolId);
        map.pushData(cspNode.getID(), projectId, cpu, memory);
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
