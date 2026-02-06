package uk.ac.ed.inf.acp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import uk.ac.ed.inf.acp.model.Drone;

import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {
    public final String SID = "s2347484";
    public final AmazonS3 s3Client; // This injects the AmazonS3 bean into this service now the client talks to localstack

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public List<String> getAllObjectsFromBucket(String bucket){
        List<String> objectKeys = new ArrayList<>();

        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(bucket);

        ListObjectsV2Result result;

        // The way that S3 returns it's files is by pages of around 1000 entries at a time, to not exhaust resources
        do {
            result = s3Client.listObjectsV2(request);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()){
                objectKeys.add(objectSummary.getKey());
            }
            request.setContinuationToken(result.getNextContinuationToken()); // if there are pages you get a token, to continue from
        } while (result.isTruncated());

        return objectKeys;
    }

    public String getObjectFromBucket(String bucket, String key){

        ListObjectsV2Request request = new ListObjectsV2Request();

        ListObjectsV2Result result;

        do {
            result = s3Client.listObjectsV2(request);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()){
                if (objectSummary.getKey().equals(key)){
                    return objectSummary.getKey();
                }
            }
            request.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return null;
    }

    public void saveDroneToS3(Drone drone) throws JsonProcessingException {
        s3Client.putObject(SID,drone.getName(),drone.toJson());
    }
}
