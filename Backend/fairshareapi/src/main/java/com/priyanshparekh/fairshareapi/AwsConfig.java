package com.priyanshparekh.fairshareapi;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
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

    @Bean
    public AmazonS3 s3Client() {
        // Creating AWS credentials using access key and secret key
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);

        // Building Amazon S3 client with specified credentials and region
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }


    @Bean
    public AmazonSNS snsClient() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
        return AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

}
