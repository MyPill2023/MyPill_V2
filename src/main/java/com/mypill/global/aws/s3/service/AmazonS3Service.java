package com.mypill.global.aws.s3.service;

import com.mypill.global.AppConfig;
import com.mypill.global.aws.s3.dto.AmazonS3Dto;
import com.mypill.global.aws.s3.properties.AmazonS3Properties;
import com.mypill.global.aws.s3.repository.AmazonS3Repository;
import com.mypill.global.util.MimeTypeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final static String IMAGE_FOLDER_NAME = "image/";
    private final AmazonS3Properties amazonS3Properties;
    private final AmazonS3Repository amazonS3Repository;

    public AmazonS3Dto imageUpload(MultipartFile file, String name) {
        String mimeType = MimeTypeUtils.getMimeType(file);
        name = AppConfig.getActiveProfile() + "/" + name;
        if (!MimeTypeUtils.isImage(mimeType)) {
            throw new IllegalArgumentException("잘못된 경로입니다.");
        }
        String fileExtension = MimeTypeUtils.extractFileExtension(mimeType);
        String objectName = IMAGE_FOLDER_NAME + name + "." + fileExtension;
        amazonS3Repository.upload(amazonS3Properties.getBucketName(), objectName, file, mimeType);
        String cndUrl = amazonS3Properties.getCdnEndPoint() + objectName;
        String originUrl = amazonS3Properties.getEndPoint() + "/" + amazonS3Properties.getBucketName() + "/" + objectName;
        return AmazonS3Dto.builder()
                .cdnUrl(cndUrl)
                .originUrl(originUrl)
                .build();
    }

    public String extractObjectNameFromImageUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();
            String[] pathSegments = path.split("/");
            return pathSegments[pathSegments.length - 3]
                    + "/" + pathSegments[pathSegments.length - 2]
                    + "/" + pathSegments[pathSegments.length - 1];
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("잘못된 이미지 URL 입니다.", e);
        }
    }

    public void deleteImage(String imageUrl) {
        String objectName = extractObjectNameFromImageUrl(imageUrl);
        String bucketName = amazonS3Properties.getBucketName();
        amazonS3Repository.deleteObject(bucketName, objectName);
    }
}