package kr.co.moneybridge.core.util;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import kr.co.moneybridge.core.exception.Exception500;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class S3Util {
    private AmazonS3 s3Client;
    private String cloudFrontDomain = "https://d23znr2pczcvf6.cloudfront.net";

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct // 스프링 Bean이 초기화될 때 이 어노테이션이 붙은 메소드가 자동으로 호출되어 초기화
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(MultipartFile file) {
        try{
            UUID uuid = UUID.randomUUID();
            String originalFilename = file.getOriginalFilename();
            String uuidFilename = uuid + "_" + originalFilename;

            InputStream inputStream = file.getInputStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            s3Client.putObject(new PutObjectRequest(bucket, uuidFilename, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
                    .withMetadata(objectMetadata));
            return cloudFrontDomain + "/"+ uuidFilename;
        } catch (IOException e) {
            throw new Exception500("s3에 저장 실패: " + e.getMessage());
        }
    }

    public void delete(String profile) throws IOException {
        int index = profile.lastIndexOf("/");
        String fileName = profile.substring(index + 1);

        s3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
