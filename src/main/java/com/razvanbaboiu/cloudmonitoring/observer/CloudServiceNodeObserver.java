package com.razvanbaboiu.cloudmonitoring.observer;

import com.razvanbaboiu.cloudmonitoring.utils.MetricDatabase;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.cdsim.CDState;
import peersim.core.Control;
import peersim.core.Network;

public class CloudServiceNodeObserver implements Control {

    private final MetricDatabase db;

    public CloudServiceNodeObserver(String prefix) {
        db = MetricDatabase.getInstance();
    }

    @Override
    public boolean execute() {
        if (CDState.getCycle() == 0) {
            System.out.println("Skip first cycle observe");
            return false;
        }

        int cloudServiceEnd = NodeTypeRatio.getCloudServiceNodeEndIndex();
        int cloudServiceStart = NodeTypeRatio.getCloudServiceNodeStartIndex();
        for (int i = cloudServiceStart; i < cloudServiceEnd; i++) {
            var nodeId = Network.get(i).getID();
            Long numberOfMetricsByNode = db.getNumberOfMetricsByNode(nodeId);
            if (numberOfMetricsByNode < CDState.getCycle()) {
                System.out.println("[FAILURE] Metric was not added in database for NodeID: " + nodeId);
                return true;
            }
        }

        return false;
    }
}
