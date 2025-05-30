package com.razvanbaboiu.cloudmonitoring.control;


import com.razvanbaboiu.cloudmonitoring.protocol.CloudServiceProtocol;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class CloudServiceMetricAnomalyControl implements Control {
    private final int protocolID;

    public CloudServiceMetricAnomalyControl(String prefix) {
        protocolID = Configuration.getPid(prefix + ".protocol");
    }

    @Override
    public boolean execute() {
        if (CDState.getCycle() == 0) {
            System.out.println("Skip first cycle");
            return false;
        }

        int cloudServiceEnd = NodeTypeRatio.getCloudServiceNodeEndIndex();
        int cloudServiceStart = NodeTypeRatio.getCloudServiceNodeStartIndex();

        for (int i = cloudServiceStart; i < cloudServiceEnd; i++) {
            Node node = Network.get(i);
            CloudServiceProtocol csp = (CloudServiceProtocol) node.getProtocol(protocolID);
            if (CommonState.r.nextDouble() < 0.3) {
                csp.setCpu(90.0 + (CommonState.r.nextDouble() * 10));
                csp.setMemory(85.0 + (CommonState.r.nextDouble() * 15));
            } else {
                csp.setCpu(20.0 + (CommonState.r.nextDouble() * 30)); // 20-50% initial load
                csp.setMemory(30.0 + (CommonState.r.nextDouble() * 40)); // 30-70% initial usage
            }
        }
        return false;
    }
}