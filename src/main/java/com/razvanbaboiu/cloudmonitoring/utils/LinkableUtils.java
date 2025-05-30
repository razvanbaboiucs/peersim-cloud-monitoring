package com.razvanbaboiu.cloudmonitoring.utils;

import peersim.core.Linkable;
import peersim.core.Node;

public class LinkableUtils {
    public static boolean containsNode(Linkable linkable, Node node) {
        for (int i = 0; i < linkable.degree(); i++) {
            if (linkable.getNeighbor(i) == node) return true;
        }
        return false;
    }
}
