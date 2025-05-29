package com.razvanbaboiu.cloudmonitoring.init;

import com.razvanbaboiu.cloudmonitoring.protocol.CloudServiceProtocol;
import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CloudServiceNodeInitializer implements Control {

    private final int cloudServiceProtoId;
    private final int totalProjects;

    public CloudServiceNodeInitializer(String prefix) {
        cloudServiceProtoId = Configuration.getPid(prefix + ".cloud_service_proto");
        totalProjects = Configuration.getInt("total_projects", 10);
    }

    @Override
    public boolean execute() {
        int totalNodes = Network.size();
        int cloudServiceEnd = (int) (totalNodes * NodeTypeRatio.CLOUD_SERVICE_NODE_RATIO);

        List<Integer> projects = new ArrayList<>();
        for (int p = 1; p <= totalProjects; p++) {
            projects.add(p);
        }
        Collections.shuffle(projects, CommonState.r);

        // Initialize Cloud Service Nodes
        int projectIndex = 0;
        for (int i = 0; i < cloudServiceEnd; i++) {
            Node node = Network.get(i);
            CloudServiceProtocol csp = (CloudServiceProtocol) node.getProtocol(cloudServiceProtoId);

            // Assign project from shuffled list
            csp.setProjectId(projects.get(projectIndex % projects.size()));
            projectIndex++;

            // Initialize metrics
            csp.setCpu(20.0 + (CommonState.r.nextDouble() * 30)); // 20-50% initial load
            csp.setMemory(30.0 + (CommonState.r.nextDouble() * 40)); // 30-70% initial usage
        }

        return false;
    }
}