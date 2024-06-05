package org.zerock.asps3.util;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class LocalUploader {

    @Value("${org.zerock.upload.path}")// import시에 springframework, C:\\upload 폴더
    private String uploadPath;

    //파일을 C:\\upload 폴더에 저장하는 메서드
    public List<String> uploadLocal(MultipartFile multipartFile) {
        //파일이 존재하는지 확인하는 IF문
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        //파일이름이 겹치지 않도록 UUID를 생성
        String uuid = UUID.randomUUID().toString();
        //UUID_파일이름.확장자 방식으로 파일이름 생성
        String saveFileName = uuid + "_" + multipartFile.getOriginalFilename();
        //파일이 저장될 위치 설정
        Path savePath = Paths.get(uploadPath, saveFileName);
        //List로 설정하여 원본 파일과 썸네일 파일의 이름을 반환할 수 있도록 설정
        List<String> savePathList = new ArrayList<>();

        try {
            //transferTo 메서드를 이용하여 savePath에 파일을 저장
            multipartFile.transferTo(savePath);
            //savePathList에 원본 파일의 전체 경로를 저장
            savePathList.add(savePath.toFile().getAbsolutePath());
            //원본 파일이 이미지일 경우 썸네일을 생성하는 IF문
            if (Files.probeContentType(savePath).startsWith("image")) {
                //썸네일 파일을 위치 생성
                File thumbFile = new File(uploadPath, "s_" + saveFileName);
                //savePathList에 썸네일 파일의 전체 경로를 저장
                savePathList.add(thumbFile.getAbsolutePath());
                //썸네일 파일 생성 후 저장
                Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
            }
        } catch (Exception e) {
            log.error("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return savePathList;
    }
}
