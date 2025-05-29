package com.razvanbaboiu.cloudmonitoring.protocol;

import lombok.Getter;
import lombok.Setter;
import peersim.core.Protocol;

@Setter
@Getter
public class CloudServiceProtocol implements Protocol {
    private int projectId;
    private double cpu;
    private double memory;

    public CloudServiceProtocol(String prefix) {
        super();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}