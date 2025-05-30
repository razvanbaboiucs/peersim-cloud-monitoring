package com.razvanbaboiu.cloudmonitoring.control;


import com.razvanbaboiu.cloudmonitoring.utils.NodeTypeRatio;
import peersim.cdsim.CDState;
import peersim.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitoringAgentAnomalyControl implements Control {
    public static final int UNRESPONSIVE_CYCLES_WINDOW = 20;
    // CONTROLLABLE PROPERTIES
    private final Map<Integer, Integer> unresponsiveMonitoringAgents = new HashMap<>();

    public MonitoringAgentAnomalyControl(String prefix) {
    }

    @Override
    public boolean execute() {
        if (CDState.getCycle() == 0) {
            System.out.println("Skip first cycle");
            return false;
        }

        System.out.println("Unresponsive monitoring agents: " + unresponsiveMonitoringAgents.entrySet());

        updateCurrentUnresponsiveAgents();

        int monitoringAgentEnd = NodeTypeRatio.getMonitoringAgentNodeEndIndex();
        int monitoringAgentStart = NodeTypeRatio.getMonitoringAgentNodeStartIndex();

        int monitoringAgentNodeId = CommonState.r.nextInt(monitoringAgentStart, monitoringAgentEnd);
        while (unresponsiveMonitoringAgents.containsKey(monitoringAgentNodeId)) {
            monitoringAgentNodeId = CommonState.r.nextInt(monitoringAgentStart, monitoringAgentEnd);
        }

        Node node = Network.get(monitoringAgentNodeId);
        node.setFailState(Fallible.DOWN);

        unresponsiveMonitoringAgents.put(monitoringAgentNodeId, 0);

        return false;
    }

    private void updateCurrentUnresponsiveAgents() {
        List<Integer> idsToRemove = unresponsiveMonitoringAgents.entrySet().stream()
                .filter(entry -> entry.getValue() > UNRESPONSIVE_CYCLES_WINDOW)
                .map(Map.Entry::getKey)
                .toList();

        idsToRemove.forEach(id -> {
            Node node = Network.get(id);
            node.setFailState(Fallible.OK);
            unresponsiveMonitoringAgents.remove(id);
        });

        List<Integer> idsToIncreaseCycleCount = unresponsiveMonitoringAgents.keySet().stream()
                .filter(integer -> !idsToRemove.contains(integer))
                .toList();

        idsToIncreaseCycleCount.forEach(id -> unresponsiveMonitoringAgents.compute(id, (_, cycleCount) -> cycleCount + 1));
    }
}