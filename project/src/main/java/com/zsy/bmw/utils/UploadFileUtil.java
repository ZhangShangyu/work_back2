package com.zsy.bmw.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class UploadFileUtil {
    @Value("${upload.path}")
    private String uploadPath;

    public String saveUploadedFiles(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "";
        }
        byte[] bytes = file.getBytes();
        String fileName = DateUtil.getNowTime() + "-" + file.getOriginalFilename();
        Path path = Paths.get(uploadPath + fileName);
        Files.write(path, bytes);
        return fileName;
    }

}
