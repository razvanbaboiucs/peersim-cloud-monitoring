package com.razvanbaboiu.cloudmonitoring.init;

import com.razvanbaboiu.cloudmonitoring.protocol.MonitoringAgentProtocol;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class MonitoringAgentsInitializer implements Control {
    // CONFIGURATION PROPERTIES
    private final int monitoringAgentProtoId;

    public MonitoringAgentsInitializer(String prefix) {
        monitoringAgentProtoId = Configuration.getPid(prefix + ".monitoring_agent_proto");
    }

    @Override
    public boolean execute() {
        int monitoringAgentStart = NodeTypeRatio.getMonitoringAgentNodeStartIndex();
        int monitoringAgentEnd = NodeTypeRatio.getMonitoringAgentNodeEndIndex();

        for (int i = monitoringAgentStart; i < monitoringAgentEnd; i++) {
            Node agentNode = Network.get(i);
            if (!agentNode.isUp()) continue;

            MonitoringAgentProtocol map = (MonitoringAgentProtocol) agentNode.getProtocol(monitoringAgentProtoId);
            if (map == null) continue;
            map.setRunning(true);
        }
        return false;
    }
}