package com.razvanbaboiu.cloudmonitoring.utils;

import peersim.core.Network;

public class NodeTypeRatio {
    public static final double CLOUD_SERVICE_NODE_RATIO = 0.7;
    public static final double LOAD_BALANCER_NODE_RATIO = 0.05;
    public static final double MONITORING_AGENT_NODE_RATIO = 0.2;
    public static final double METRIC_AGGREGATOR_NODE_RATIO = 0.05;

    public static int getCloudServiceNodeStartIndex() {
        return 0;
    }

    public static int getCloudServiceNodeEndIndex() {
        return (int) (Network.size() * CLOUD_SERVICE_NODE_RATIO);
    }

    public static int getLoadBalancerNodeStartIndex() {
        return (int) (Network.size() * CLOUD_SERVICE_NODE_RATIO);
    }

    public static int getLoadBalancerNodeEndIndex() {
        return (int) (Network.size() * (CLOUD_SERVICE_NODE_RATIO + LOAD_BALANCER_NODE_RATIO));
    }

    public static int getMonitoringAgentNodeStartIndex() {
        return (int) (Network.size() * (CLOUD_SERVICE_NODE_RATIO + LOAD_BALANCER_NODE_RATIO));
    }

    public static int getMonitoringAgentNodeEndIndex() {
        return (int) (Network.size() * (CLOUD_SERVICE_NODE_RATIO + LOAD_BALANCER_NODE_RATIO + MONITORING_AGENT_NODE_RATIO));
    }

    public static int getMetricAggregatorNodeStartIndex() {
        return (int) (Network.size() * (CLOUD_SERVICE_NODE_RATIO + LOAD_BALANCER_NODE_RATIO + MONITORING_AGENT_NODE_RATIO));
    }

    public static int getMetricAggregatorNodeEndIndex() {
        return Network.size();
    }

    public static int getTotalMetricAggregatorNodes() {
        return (int) (Network.size() * METRIC_AGGREGATOR_NODE_RATIO);
    }

    public static int getTotalMonitoringAgentNodes() {
        return (int) (Network.size() * MONITORING_AGENT_NODE_RATIO);
    }

    public static int getTotalLoadBalancerNodes() {
        return (int) (Network.size() * LOAD_BALANCER_NODE_RATIO);
    }
}
