package org.zerock.asps3.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket; //S3 버킷이름
    //S3으로 파일 업로드 하기
    public String upload(String filePath) throws RuntimeException {

        File targetFile = new File(filePath);

        String uploadImageUrl = putS3(targetFile, targetFile.getName()); //S3으로 업로드
        // C:\\upload 에 저장되있는 파일을 삭제
        removeOriginalFile(targetFile);

        return uploadImageUrl;
    }
    //S3으로 업로드
    private String putS3(File uploadFile, String fileName) throws RuntimeException{
        //putObject 메서드를 이용하여 S3스토리지에 파일 저장
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        //S3에 저장된 파일을 불러올 수 있는 주소를 반환
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
    //S3 업로드 후 원본 파일 삭제
    private void removeOriginalFile(File targetFile) {
        // targetFile 객체 안에 파일이 존재하는지 확인
        // && 파일을 삭제한 후 정상적으로 삭제가 됬으면 true 에러가 발생했으면 false
        if(targetFile.exists() && targetFile.delete()) {
            log.info("File Delete Success");
            return;
        }
        log.info("fail to remove");
    }

    public void removeS3File(String fileName){
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
