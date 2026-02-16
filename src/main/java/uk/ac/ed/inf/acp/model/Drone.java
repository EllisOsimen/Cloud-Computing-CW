package uk.ac.ed.inf.acp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL) // avoids serialising null fields
public class Drone {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    String name;
    String id;
    Capability capability;
    int costPer100Moves;

    public Drone(){}

    public Drone(String name, String id, Capability capability) {
        this.name = name;
        this.id = id;
        this.capability = capability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Capability getCapability() {
        return capability;
    }

    public void setCapability(Capability capability) {
        this.capability = capability;
    }

    public String toJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }

    public void setCostPer100Moves() {
        costPer100Moves = capability.costInitial + capability.costFinal + capability.costPerMove * 100;
    }
    public int getCostPer100Moves() {
        return costPer100Moves;
    }
}
