package com.razvanbaboiu.cloudmonitoring;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java example.BasicSimulator config_file");
            // If no arguments provided, use the default config
            String[] defaultArgs = {"/Users/razvanbaboiu/master/sem-2/big-data/cloud-monitoring-peersim/cloud-monitoring-peersim-project/src/main/resources/config.txt"};
            System.out.println("Using default configuration file: config.txt");
            peersim.Simulator.main(defaultArgs);
        } else {
            System.out.println("Starting PeerSim with configuration: " + args[0]);
            // Run the simulation with the given configuration file
            peersim.Simulator.main(args);
        }

        System.out.println("Simulation completed!");
    }
}