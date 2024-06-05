package org.zerock.asps3.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.asps3.dto.SampleDTO;
import org.zerock.asps3.util.LocalUploader;
import org.zerock.asps3.util.S3Uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/sample")
public class SampleController {

    private final LocalUploader localUploader;
    private final S3Uploader s3Uploader;

    @PostMapping("/upload")
    public List<String> upload(SampleDTO sampleDTO) {
        //여러개의 파일을 받아온후 저장하는 변수
        MultipartFile[] files = sampleDTO.getFiles();
        //파일이 존재하는지 확인하고 존재하지 않으면 null 반환
        if (files == null || files.length <= 0) {
            return null;
        }
        //파일 경로들을 저장하는 변수
        List<String> uploadedFilePaths = new ArrayList<>();
        //MultipartFile 객체로 파일 하나하나를 분리하는 처리
        for (MultipartFile file : files) {
            uploadedFilePaths.addAll(localUploader.uploadLocal(file));
        }
        log.info("--------------------------------------");
        log.info(uploadedFilePaths);
        //S3 스토리지에 파일을 저장
        List<String> s3paths = uploadedFilePaths.stream().map(fileName -> s3Uploader.upload(fileName)).collect(Collectors.toList());

        return s3paths;
    }
}
