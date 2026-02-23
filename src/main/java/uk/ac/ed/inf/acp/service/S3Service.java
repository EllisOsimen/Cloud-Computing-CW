package uk.ac.ed.inf.acp.service;


import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.acp.model.Drone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class S3Service {
    public final String SID = "s2347484";
    public final S3Client s3Client; // This injects the AmazonS3 bean into this service now the client talks to localstack

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<String> getAllObjectsFromBucket(String bucket){
        List<String> objectKeys = new ArrayList<>();

        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucket).build();

        ListObjectsV2Response response;

        do{
            response = s3Client.listObjectsV2(request);

            for (S3Object obj : response.contents()) {
                String content = getObjectFromBucket(bucket, obj.key());
                objectKeys.add(content);
            }
            request = ListObjectsV2Request.builder().bucket(bucket).continuationToken(response.nextContinuationToken()).build();
        } while (response.isTruncated());

        return  objectKeys;
    }

    public String getObjectFromBucket(String bucket, String key){

        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();
        try(ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request)){
            return new String(response.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMapToS3(String uuid, Map<String, Object> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(SID)
                    .key(uuid)
                    .contentType("application/json")
                    .build();
            s3Client.putObject(request, RequestBody.fromString(json));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDroneToS3(Drone drone) throws JsonProcessingException {
        String json = drone.toJson();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(SID)
                .key(drone.getName())
                .contentType("application/json")
                .build();

        // V2 needs an explicit RequestBody wrapper around the content
        s3Client.putObject(request, RequestBody.fromString(json));
    }

}
