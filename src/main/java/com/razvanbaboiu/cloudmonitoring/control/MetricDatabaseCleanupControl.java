package com.razvanbaboiu.cloudmonitoring.control;

import com.razvanbaboiu.cloudmonitoring.data.MetricDatabase;
import peersim.cdsim.CDState;
import peersim.core.Control;

public class MetricDatabaseCleanupControl implements Control {

    private final MetricDatabase db;

    public MetricDatabaseCleanupControl(String prefix) {
        db = MetricDatabase.getInstance();
    }

    @Override
    public boolean execute() {
        if (CDState.getPhase() != CDState.POST_SIMULATION) return false;

        db.cleanup();

        return false;
    }
}
