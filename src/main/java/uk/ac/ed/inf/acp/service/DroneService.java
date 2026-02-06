package uk.ac.ed.inf.acp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.acp.model.Drone;

import java.util.Arrays;
import java.util.List;

@Service
public class DroneService {
    private final RestTemplate restTemplate;
    public DroneService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Drone> fetchDronesFromUrl(String url){
        Drone[] drones = restTemplate.getForObject(url, Drone[].class);
        return Arrays.asList(drones);
    }
}
