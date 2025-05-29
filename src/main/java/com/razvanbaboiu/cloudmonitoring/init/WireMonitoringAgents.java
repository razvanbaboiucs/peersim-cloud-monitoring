package com.razvanbaboiu.cloudmonitoring.init;

import com.razvanbaboiu.cloudmonitoring.protocol.MonitoringAgentProtocol;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.config.Configuration;
import peersim.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WireMonitoringAgents implements Control {

    private final int monitoringAgentProtoId;
    private final int linkableProtoId;

    // Data structures for efficient lookup

    public WireMonitoringAgents(String prefix) {
        monitoringAgentProtoId = Configuration.getPid(prefix + ".monitoring_agent_proto");
        linkableProtoId = Configuration.getPid(prefix + ".linkable_proto");
    }

    @Override
    public boolean execute() {
        int totalNodes = Network.size();
        int monitoringAgentStart = (int) (totalNodes * NodeTypeRatio.CLOUD_SERVICE_NODE_RATIO);
        int monitoringAgentEnd = (int) (totalNodes * (NodeTypeRatio.CLOUD_SERVICE_NODE_RATIO + NodeTypeRatio.MONITORING_AGENT_NODE_RATIO));

        List<Integer> cloudServiceNodeIds = new ArrayList<>();
        for (int i = 0; i < monitoringAgentStart; i++) {
            cloudServiceNodeIds.add(i);
        }
        Collections.shuffle(cloudServiceNodeIds, CommonState.r);
        var cloudServiceNodeIndex = 0;

        int cloudServiceNodesPerAgent = (int) (monitoringAgentStart / (totalNodes * NodeTypeRatio.MONITORING_AGENT_NODE_RATIO));

        for (int i = monitoringAgentStart; i < monitoringAgentEnd; i++) {
            Node agentNode = Network.get(i);
            if (!agentNode.isUp()) continue;

            MonitoringAgentProtocol map = (MonitoringAgentProtocol)
                    agentNode.getProtocol(monitoringAgentProtoId);
            if (map == null) continue;
            map.setRunning(true);

            Linkable linkable = (Linkable) agentNode.getProtocol(linkableProtoId);
            if (linkable == null) continue;

            for (int j = 0; j < cloudServiceNodesPerAgent; j++) {
                Node serviceNode = Network.get(cloudServiceNodeIds.get(cloudServiceNodeIndex));
                cloudServiceNodeIndex++;
                if (serviceNode.isUp() && !containsNode(linkable, serviceNode)) {
                    linkable.addNeighbor(serviceNode);
                }
            }
        }
        return false;
    }

    private boolean containsNode(Linkable linkable, Node node) {
        for (int i = 0; i < linkable.degree(); i++) {
            if (linkable.getNeighbor(i) == node) return true;
        }
        return false;
    }
}