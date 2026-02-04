package uk.ac.ed.inf.acp.config;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {
    @Value("${aws.s3.endpoint}")
    private String s3Endpoint;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder() //starts building S3 client by chaining different config methods
                .endpointOverride(URI.create(s3Endpoint)) // specifies the endpoint, in our case it is our localstack
                .credentialsProvider(StaticCredentialsProvider.create(credentials)) // Adds the credentials specified before
                .region(Region.of(region)).build(); // specifies the region and builds everything
    }
}
