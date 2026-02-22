error id: file:///C:/Users/ellis/OneDrive/Documents/ACP/Rest_Service/src/main/java/uk/ac/ed/inf/acp/model/Drone.java:software/amazon/awssdk/thirdparty/jackson/core/JsonProcessingException#
file:///C:/Users/ellis/OneDrive/Documents/ACP/Rest_Service/src/main/java/uk/ac/ed/inf/acp/model/Drone.java
empty definition using pc, found symbol in pc: software/amazon/awssdk/thirdparty/jackson/core/JsonProcessingException#
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 143
uri: file:///C:/Users/ellis/OneDrive/Documents/ACP/Rest_Service/src/main/java/uk/ac/ed/inf/acp/model/Drone.java
text:
```scala
package uk.ac.ed.inf.acp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import software.amazon.awssdk.thirdparty.jackson.core.@@JsonProcessingException;
import tools.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL) // avoids serialising null fields
public class Drone {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    String name;
    String id;
    Capability capability;
    double costPer100Moves;

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
    public double getCostPer100Moves() {
        return costPer100Moves;
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: software/amazon/awssdk/thirdparty/jackson/core/JsonProcessingException#