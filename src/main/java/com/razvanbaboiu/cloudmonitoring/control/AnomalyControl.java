package com.razvanbaboiu.cloudmonitoring.control;


import com.razvanbaboiu.cloudmonitoring.protocol.CloudServiceProtocol;
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
        // Randomly spike metrics for 5% of CloudServiceNodes
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            if (CommonState.r.nextDouble() < 0.1) {
                CloudServiceProtocol csp = (CloudServiceProtocol) node.getProtocol(protocolID);
                csp.setCpu(90.0 + (CommonState.r.nextDouble() * 10)); // Spike to 100%
                csp.setMemory(85.0 + (CommonState.r.nextDouble() * 15));
            } else {
                CloudServiceProtocol csp = (CloudServiceProtocol) node.getProtocol(protocolID);
                csp.setCpu(20.0 + (CommonState.r.nextDouble() * 30)); // 20-50% initial load
                csp.setMemory(30.0 + (CommonState.r.nextDouble() * 40)); // 30-70% initial usage
            }
        }
        return false;
    }
}