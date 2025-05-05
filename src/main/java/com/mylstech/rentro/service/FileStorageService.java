package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    /**
     * Stores any image file and converts it to WebP format
     * @param file The image file to store
     * @param quality The quality of the WebP image (0-100)
     * @param fallbackToJpeg Whether to fall back to JPEG if WebP conversion fails
     * @return The URL of the stored image
     */
    String storeImageAsWebP(MultipartFile file, int quality, boolean fallbackToJpeg) throws IOException;

    /**
     * Stores a PDF file
     * @param file The PDF file to store
     * @return The URL of the stored PDF
     */
    FileUploadResponse storePdf(MultipartFile file) throws IOException;

    /**
     * Deletes a file from storage
     * @param filePath The path of the file to delete
     */
    void deleteFile(String filePath) throws IOException;

    String getUploadDir();

    String getBaseUrl();
}
