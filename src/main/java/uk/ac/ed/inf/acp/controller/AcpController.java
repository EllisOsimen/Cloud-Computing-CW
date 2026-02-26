package uk.ac.ed.inf.acp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import uk.ac.ed.inf.acp.model.Drone;
import uk.ac.ed.inf.acp.model.UrlRequest;
import uk.ac.ed.inf.acp.service.DroneService;
import uk.ac.ed.inf.acp.service.DynamoDBService;
import uk.ac.ed.inf.acp.service.PostgresDBClient;
import uk.ac.ed.inf.acp.service.S3Service;

import java.util.List;
import java.util.Map;

@RestController
public class AcpController {

    private final S3Service s3Service; //constants which will never change
    private final DroneService droneService;
    private final DynamoDBService dynamoDBService;
    private final PostgresDBClient postgresDBClient;

    public AcpController(S3Service s3Service, DroneService droneService, DynamoDBService dynamoDBService, PostgresDBClient postgresDBClient) {
        this.s3Service = s3Service;
        this.droneService = droneService;
        this.dynamoDBService = dynamoDBService;
        this.postgresDBClient = postgresDBClient;
    }

    @GetMapping("/api/v1/acp/all/s3/{bucket}")
    public ResponseEntity<List<Object>> listAllS3Buckets(@PathVariable String bucket){
        try {
            List<Object> objects = s3Service.getAllObjectsFromBucket(bucket);
            return ResponseEntity.ok().body(objects);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/v1/acp/single/s3/{bucket}/{key}")
    public ResponseEntity<String> listS3Object(@PathVariable String bucket, @PathVariable String key){
        try {
            String object = s3Service.getObjectFromBucket(bucket, key);
            if (object == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(object);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/v1/acp/process/s3")
    public ResponseEntity<Void> processS3Object(@RequestBody UrlRequest urlRequest) throws JsonProcessingException {
        String url = urlRequest.getUrlPath();
        List<Drone> drones = droneService.fetchDronesFromUrl(url);
        for (Drone drone : drones) {
            drone.setCostPer100Moves();
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
        try {
            Map<String, Object> item = dynamoDBService.getItem(table, key);
            if (item == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(item);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/v1/acp/all/dynamo/{table}")
    public ResponseEntity<List<Map<String,Object>>> listAllDynamo(@PathVariable String table){
        try {
            List<Map<String, Object>> items = dynamoDBService.getAllItems(table);
            return ResponseEntity.ok().body(items);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/v1/acp/process/dynamo")
    public ResponseEntity<Void> processDynamo(@RequestBody UrlRequest urlRequest) throws JsonProcessingException {
        String url = urlRequest.getUrlPath();
        List<Drone> drones = droneService.fetchDronesFromUrl(url);
        for (Drone drone : drones) {
            drone.setCostPer100Moves();
            dynamoDBService.saveDroneToDynamo(drone);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/v1/acp/all/postgres/{table}")
    public ResponseEntity<List<Map<String,Object>>> listAllPostgres(@PathVariable String table){
        try {
            List<Map<String, Object>> items = postgresDBClient.getItems(table);
            return ResponseEntity.ok().body(items);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/v1/acp/single/postgres/{table}/{key}")
    public ResponseEntity<Map<String,Object>> listPostgresObject(@PathVariable String table, @PathVariable String key){
        try {
            Map<String, Object> item = postgresDBClient.getItem(table, key);
            if (item == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(item);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/v1/acp/process/postgres/{table}")
    public ResponseEntity<Void> processPostgres(@PathVariable String table, @RequestBody UrlRequest urlRequest) throws JsonProcessingException {
        String url = urlRequest.getUrlPath();
        List<Drone> drones = droneService.fetchDronesFromUrl(url);
        for (Drone drone : drones) {
            drone.setCostPer100Moves();
            postgresDBClient.saveDroneToPostgres(drone, table);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/acp/copy-content/dynamo/{table}")
    public ResponseEntity<Void> copyPostgresToDynamo(@PathVariable String table) {
        List<Map<String, Object>> rows = postgresDBClient.getItems(table);
        for (Map<String, Object> row : rows) {
            String uuid = java.util.UUID.randomUUID().toString();
            dynamoDBService.saveMapToDynamo(uuid, row);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/acp/copy-content/S3/{table}")
    public ResponseEntity<Void> copyPostgresToS3(@PathVariable String table) {
        List<Map<String, Object>> rows = postgresDBClient.getItems(table);
        for (Map<String, Object> row : rows) {
            String uuid = java.util.UUID.randomUUID().toString();
            s3Service.saveMapToS3(uuid, row);
        }
        return ResponseEntity.ok().build();
    }
}
