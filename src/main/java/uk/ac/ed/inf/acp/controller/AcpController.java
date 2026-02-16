package uk.ac.ed.inf.acp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import uk.ac.ed.inf.acp.model.Drone;
import uk.ac.ed.inf.acp.model.UrlRequest;
import uk.ac.ed.inf.acp.service.DroneService;
import uk.ac.ed.inf.acp.service.DynamoDBService;
import uk.ac.ed.inf.acp.service.S3Service;

import java.util.List;
import java.util.Map;

@RestController
public class AcpController {

    private final S3Service s3Service; //constants which will never change
    private final DroneService droneService;
    private final DynamoDBService dynamoDBService;

    public AcpController(S3Service s3Service, DroneService droneService, DynamoDBService dynamoDBService) {
        this.s3Service = s3Service;
        this.droneService = droneService;
        this.dynamoDBService = dynamoDBService;
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

    @GetMapping("/api/v1/acp/single/dynamo/{table}/{key}")
    public ResponseEntity<Map<String,Object>> listDynamoObject(@PathVariable String table, @PathVariable String key){
        return ResponseEntity.ok().body(dynamoDBService.getItem(table,key));
    }

    @GetMapping("/api/v1/acp/all/dynamo/{table}")
    public ResponseEntity<List<Map<String,Object>>> listAllDynamo(@PathVariable String table){
        return ResponseEntity.ok().body(dynamoDBService.getAllItems(table));
    }

    @PostMapping("/api/v1/acp/process/dynamo")
    public ResponseEntity<Void> processDynamo(@RequestBody UrlRequest urlRequest) throws JsonProcessingException {
        String url = urlRequest.getUrlPath();
        List<Drone> drones = droneService.fetchDronesFromUrl(url);
        for (Drone drone : drones) {
            dynamoDBService.saveDroneToDynamo(drone);
        }
        return ResponseEntity.ok().build();
    }
}
