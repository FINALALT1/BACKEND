package kr.co.moneybridge.core.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import kr.co.moneybridge.core.exception.Exception500;
import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class S3Util {
    private AmazonS3 s3Client;
    private String cloudFrontDomain = "https://d2ky5wm6akosox.cloudfront.net";

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

    // s3에 파일 업로드
    public String upload(MultipartFile file, String folderName) {
        try{
            UUID uuid = UUID.randomUUID();
            String originalFilename = file.getOriginalFilename();
            String uuidFilename = folderName + "/" + uuid + "_" + originalFilename;

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

    // s3에서 파일 삭제
    public void delete(String fileName) {
        // 디폴트 폴더에 있는 파일은 삭제 안함
        if(fileName.startsWith(cloudFrontDomain+"/default")) return;

        int index = cloudFrontDomain.length();
        if(fileName.length() < index) return;
        String key = fileName.substring(index + 1);

        s3Client.deleteObject(new DeleteObjectRequest(bucket, key));
    }

    // 이미지 리사이징
    public MultipartFile resize(MultipartFile multipartFile, int targetWidth, int targetHeight) {
        try (InputStream is = multipartFile.getInputStream()){
            // MultipartFile -> BufferedImage Convert
            BufferedImage image = ImageIO.read(is);
            // 원하는 px로 Width와 Height 수정
            int originWidth = image.getWidth();
            int originHeight = image.getHeight();
            // origin 이미지가 resizing될 사이즈보다 작을 경우 resizing 작업 안 함
            if (originWidth < targetWidth && originHeight < targetHeight)
                return multipartFile;
            MarvinImage imageMarvin = new MarvinImage(image);
            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", targetWidth);
            scale.setAttribute("newHeight", targetHeight);
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);
            return toMultipartFile(imageMarvin.getBufferedImageNoAlpha(), multipartFile);
        } catch (IOException e) {
            // 파일 리사이징 실패시 예외 처리
            throw new Exception500("파일 리사이징에 실패" + e.getMessage());
        }
    }

    // MultipartFile로 바꾸기
    private MultipartFile toMultipartFile(BufferedImage image, MultipartFile origin) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String fileExtension = origin.getContentType().split("/")[1];
        ImageIO.write(image, fileExtension, baos); // use JPEG or PNG depending on your image

        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return new MultipartFile() {
            @Override
            public String getName() {
                return origin.getName();
            }

            @Override
            public String getOriginalFilename() {
                return origin.getOriginalFilename();
            }

            @Override
            public String getContentType() {
                return origin.getContentType();
            }

            @Override
            public boolean isEmpty() {
                return imageInByte.length == 0;
            }

            @Override
            public long getSize() {
                return imageInByte.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return imageInByte;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(imageInByte);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                Files.write(dest.toPath(), imageInByte);
            }

            @Override
            public void transferTo(Path dest) throws IOException, IllegalStateException {
                Files.write(dest, imageInByte);
            }
        };
    }

    public void deleteLatestFileWithSuffixFromS3Bucket(String suffix) {
        ObjectListing objectListing = s3Client.listObjects(bucket);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();

        Comparator<S3ObjectSummary> lastModifiedComparator = Comparator.comparing(S3ObjectSummary::getLastModified).reversed();
        Optional<S3ObjectSummary> latestFileOptional = objectSummaries.stream()
                .filter(objectSummary -> objectSummary.getKey().endsWith(suffix))
                .max(lastModifiedComparator);

        if (latestFileOptional.isPresent()) {
            S3ObjectSummary latestFile = latestFileOptional.get();
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, latestFile.getKey());
            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}
