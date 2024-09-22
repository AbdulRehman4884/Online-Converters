package com.example.demo.models;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadModel {

    private MultipartFile tiffFile;

    public MultipartFile getTiffFile() {
        return tiffFile;
    }

    public void setTiffFile(MultipartFile tiffFile) {
        this.tiffFile = tiffFile;
    }
}
