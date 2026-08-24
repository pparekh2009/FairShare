package com.priyanshparekh.fairshareapi;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    // Injecting secret key from application.properties
    @Value("${cloud.aws.credentials.secretKey}")
    private String accessSecret;

    // Injecting region from application.properties
    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.endpoint:}")
    private String s3Endpoint;

    @Bean
    public AmazonS3 s3Client() {
        // Creating AWS credentials using access key and secret key
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);

        // Building Amazon S3 client with specified credentials and region
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3Endpoint, region))
                .withPathStyleAccessEnabled(true)
                .build();
    }

}
