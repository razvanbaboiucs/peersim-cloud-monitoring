package com.razvanbaboiu.cloudmonitoring.control;


import com.razvanbaboiu.cloudmonitoring.protocol.CloudServiceProtocol;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class AnomalyControl implements Control {
    private final int protocolID;

    public AnomalyControl(String prefix) {
        protocolID = Configuration.getPid(prefix + ".protocol");
    }

    @Override
    public boolean execute() {
        int totalNodes = Network.size();
        int cloudServiceEnd = (int) (totalNodes * NodeTypeRatio.CLOUD_SERVICE_NODE_RATIO);
        // Randomly spike metrics for 10% of CloudServiceNodes
        for (int i = 0; i < cloudServiceEnd; i++) {
            Node node = Network.get(i);
            CloudServiceProtocol csp = (CloudServiceProtocol) node.getProtocol(protocolID);
            if (CommonState.r.nextDouble() < 0.1) {
                csp.setCpu(90.0 + (CommonState.r.nextDouble() * 10)); // Spike to 100%
                csp.setMemory(85.0 + (CommonState.r.nextDouble() * 15));
            } else {
                csp.setCpu(20.0 + (CommonState.r.nextDouble() * 30)); // 20-50% initial load
                csp.setMemory(30.0 + (CommonState.r.nextDouble() * 40)); // 30-70% initial usage
            }
        }
        return false;
    }
}