package com.razvanbaboiu.cloudmonitoring.init;

import com.razvanbaboiu.cloudmonitoring.protocol.CloudServiceProtocol;
import com.razvanbaboiu.cloudmonitoring.utils.LinkableUtils;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.config.Configuration;
import peersim.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CloudServiceNodeInitializer implements Control {
    // CONFIGURATION PROPERTIES
    private final int cloudServiceProtoId;
    private final int linkableProtocolId;
    private final int totalProjects;

    public CloudServiceNodeInitializer(String prefix) {
        cloudServiceProtoId = Configuration.getPid(prefix + ".cloud_service_proto");
        linkableProtocolId = Configuration.getPid(prefix + ".linkable_proto");
        totalProjects = Configuration.getInt("total_projects", 10);
    }

    @Override
    public boolean execute() {
        int cloudServiceStart = NodeTypeRatio.getCloudServiceNodeStartIndex();
        int cloudServiceEnd = NodeTypeRatio.getCloudServiceNodeEndIndex();

        List<Integer> projects = getProjects();

        int loadBalancerStart = NodeTypeRatio.getLoadBalancerNodeStartIndex();
        int loadBalancerEnd = NodeTypeRatio.getLoadBalancerNodeEndIndex();

        // Initialize Cloud Service Nodes
        int projectIndex = 0;
        for (int i = cloudServiceStart; i < cloudServiceEnd; i++) {
            Node node = Network.get(i);
            CloudServiceProtocol csp = (CloudServiceProtocol) node.getProtocol(cloudServiceProtoId);
            csp.setRunning(true);

            // Assign project from shuffled list
            csp.setProjectId(projects.get(projectIndex % projects.size()));
            projectIndex++;

            // Initialize metrics
            csp.setCpu(20.0 + (CommonState.r.nextDouble() * 30)); // 20-50% initial load
            csp.setMemory(30.0 + (CommonState.r.nextDouble() * 40)); // 30-70% initial usage

            // Link to all load balancers
            Linkable linkable = (Linkable) node.getProtocol(linkableProtocolId);
            if (linkable == null) continue;

            for (int j = loadBalancerStart; j < loadBalancerEnd; j++) {
                Node lbNode = Network.get(j);
                if (lbNode.isUp() && !LinkableUtils.containsNode(linkable, lbNode)) {
                    linkable.addNeighbor(lbNode);
                }
            }
        }

        return false;
    }

    private List<Integer> getProjects() {
        List<Integer> projects = new ArrayList<>();
        for (int p = 1; p <= totalProjects; p++) {
            projects.add(p);
        }
        Collections.shuffle(projects, CommonState.r);
        return projects;
    }
}