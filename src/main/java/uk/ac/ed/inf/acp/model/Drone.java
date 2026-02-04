package uk.ac.ed.inf.acp.model;

public class Drone {
    String name;
    String id;
    Capability capability;
    Drone(String name, String id, Capability capability) {
        this.name = name;
        this.id = id;
        this.capability = capability;
    }
}
