package uk.ac.ed.inf.acp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.acp.model.Drone;
import uk.ac.ed.inf.acp.model.UrlRequest;
import uk.ac.ed.inf.acp.service.DroneService;
import uk.ac.ed.inf.acp.service.S3Service;

import java.util.List;

@RestController
public class AcpController {

    private final S3Service s3Service; //constants which will never change
    private final DroneService droneService;

    public AcpController(S3Service s3Service, DroneService droneService) {
        this.s3Service = s3Service;
        this.droneService = droneService;
    }

    @GetMapping("/api/v1/acp/all/s3/{bucket}")
    public ResponseEntity<List<String>> listAllS3Buckets(@PathVariable String bucket){
        return ResponseEntity.ok().body(s3Service.getAllObjectsFromBucket(bucket));
    }

    @GetMapping("/api/v1/acp/single/s3/{bucket}/{key}")
    public ResponseEntity<String> listS3Object(@PathVariable String bucket, @PathVariable String key){
        return ResponseEntity.ok().body(s3Service.getObjectFromBucket(bucket, key));
    }

    @PostMapping("/api/v1/acp/process/s3")
    public ResponseEntity<Void> processS3Object(@RequestBody UrlRequest urlRequest) throws JsonProcessingException {
        String url = urlRequest.getUrlPath();
        List<Drone> drones = droneService.fetchDronesFromUrl(url);
        for (Drone drone : drones) {
            s3Service.saveDroneToS3(drone);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/acp/process/dump")
    public ResponseEntity<List<Drone>> processDump(@RequestBody UrlRequest urlRequest) throws JsonProcessingException {
        String url = urlRequest.getUrlPath();
        List<Drone> drones = droneService.fetchDronesFromUrl(url);
        for (Drone drone : drones) {
            drone.setCostPer100Moves();
        }
        return ResponseEntity.ok(drones);
    }
}
