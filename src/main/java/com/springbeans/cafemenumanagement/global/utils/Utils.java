package com.springbeans.cafemenumanagement.global.utils;

import java.nio.file.Paths;

public class Utils {
    // 원본 파일의 확장자만 분리하는 메서드 (coffee.jpg -> .jpg)
    public static String getExtension(String originalFilename){
        if (originalFilename == null || originalFilename.isBlank()) throw new IllegalArgumentException("파일명이 존재하지 않습니다.");
        String fileName = Paths.get(originalFilename).getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) throw new IllegalArgumentException("파일 확장자가 존재하지 않습니다.");
        return fileName.substring(dotIndex).toLowerCase();
    }
}
