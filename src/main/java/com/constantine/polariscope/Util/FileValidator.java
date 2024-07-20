package com.constantine.polariscope.Util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;

public class FileValidator {
    public static String getImageFileType(MultipartFile file){
        String fileType = "";
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                fileType = fileName.substring(dotIndex + 1).toLowerCase();
            }
        }

        if(fileType.equalsIgnoreCase("png") || fileType.equals("jpg") || fileType.equals("jpeg") || fileType.equals("gif") || fileType.equals("bmp") || fileType.equals("wbmp") || fileType.equals("tiff") || fileType.equals("tif") || fileType.equals("ico") || fileType.equals("pnm")){
            //file types that are able to be validated with java
            try {
                ImageIO.read(file.getInputStream()).toString();
                //File is valid and not null:
                return fileType;

            } catch (Exception e) {
                // file could not be opened as an image
                return "";
            }

        }else if(fileType.toLowerCase().equals("webp") || fileType.toLowerCase().equals("avif")){
            // Special file types that do not currently require validation
            return fileType;
        }else{
            //unknown type
            return "";
        }
    }
}
