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
        int totalNodes = Network.size();
        int cloudServiceEnd = (int) (totalNodes * NodeTypeRatio.CLOUD_SERVICE_NODE_RATIO);
        for (int i = 0; i < cloudServiceEnd; i++) {
            var nodeId = Network.get(i).getID();
            Long numberOfMetricsByNode = db.getNumberOfMetricsByNode(nodeId);
            if (numberOfMetricsByNode < CDState.getCycle()) {
                System.out.println("Metric was not added in db!node id: " + nodeId);
                return true;
            }
        }
        return false;
    }
}
