package com.razvanbaboiu.cloudmonitoring.init;

import com.razvanbaboiu.cloudmonitoring.protocol.LoadBalancerProtocol;
import com.razvanbaboiu.cloudmonitoring.utils.LinkableUtils;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.config.Configuration;
import peersim.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadBalancerInitializer implements Control {
    // CONFIGURATION PROPERTIES
    private final int loadBalancerProtocolId;
    private final int linkableProtoId;

    public LoadBalancerInitializer(String prefix) {
        loadBalancerProtocolId = Configuration.getPid(prefix + ".load_balancer_proto");
        linkableProtoId = Configuration.getPid(prefix + ".linkable_proto");
    }

    @Override
    public boolean execute() {
        int loadBalancerStart = NodeTypeRatio.getLoadBalancerNodeStartIndex();
        int loadBalancerEnd = NodeTypeRatio.getLoadBalancerNodeEndIndex();

        List<Integer> monitoringAgentNodeIds = getMonitoringAgentNodeIds();

        var monitoringAgentNodeIndex = 0;
        int monitoringAgentNodesPerLoadBalancer = NodeTypeRatio.getTotalMonitoringAgentNodes() / NodeTypeRatio.getTotalLoadBalancerNodes();

        for (int i = loadBalancerStart; i < loadBalancerEnd; i++) {
            Node lbNode = Network.get(i);
            if (!lbNode.isUp()) continue;

            LoadBalancerProtocol lbp = (LoadBalancerProtocol) lbNode.getProtocol(loadBalancerProtocolId);
            if (lbp == null) continue;
            lbp.setRunning(true);

            Linkable linkable = (Linkable) lbNode.getProtocol(linkableProtoId);
            if (linkable == null) continue;

            for (int j = 0; j < monitoringAgentNodesPerLoadBalancer; j++) {
                Node serviceNode = Network.get(monitoringAgentNodeIds.get(monitoringAgentNodeIndex));
                monitoringAgentNodeIndex++;
                if (serviceNode.isUp() && !LinkableUtils.containsNode(linkable, serviceNode)) {
                    linkable.addNeighbor(serviceNode);
                }
            }
        }
        return false;
    }

    private static List<Integer> getMonitoringAgentNodeIds() {
        int monitoringAgentStart = NodeTypeRatio.getMonitoringAgentNodeStartIndex();
        int monitoringAgentEnd = NodeTypeRatio.getMonitoringAgentNodeEndIndex();
        List<Integer> monitoringAgentNodeIds = new ArrayList<>();
        for (int i = monitoringAgentStart; i < monitoringAgentEnd; i++) {
            monitoringAgentNodeIds.add(i);
        }
        Collections.shuffle(monitoringAgentNodeIds, CommonState.r);
        return monitoringAgentNodeIds;
    }
}
