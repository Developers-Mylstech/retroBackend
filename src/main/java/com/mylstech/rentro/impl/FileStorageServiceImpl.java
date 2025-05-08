package com.mylstech.rentro.impl;

import com.luciad.imageio.webp.WebPWriteParam;
import com.mylstech.rentro.dto.response.FileUploadResponse;
import com.mylstech.rentro.service.FileStorageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    @Value("${file.base-url}")
    private String baseUrl;

    @Override
    public String storeImageAsWebP(MultipartFile file, int quality, boolean fallbackToJpeg) throws IOException {
        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        
        // Generate a unique file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String baseName = originalFileName;
        if (originalFileName.contains(".")) {
            baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        }
        
        // Read the original image
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("Failed to read image from input file: " + originalFileName);
        }
        
        // Ensure quality is within valid range
        if (quality < 0) quality = 0;
        if (quality > 100) quality = 100;
        
        // Try WebP conversion first
        try {
            String uniqueFileName = UUID.randomUUID().toString() + "_" + baseName + ".webp";
            Path targetLocation = uploadPath.resolve(uniqueFileName);
            
            // Create WebP encoder
            WebPWriteParam writeParam = new WebPWriteParam(Locale.getDefault());
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            writeParam.setCompressionQuality(quality / 100f); // Convert 0-100 to 0.0-1.0
            
            // Write WebP file
            try (ImageOutputStream output = ImageIO.createImageOutputStream(Files.newOutputStream(targetLocation))) {
                ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
                writer.setOutput(output);
                writer.write(null, new IIOImage(originalImage, null, null), writeParam);
                writer.dispose();
            }
            
            // Return the URL path to the file
            return baseUrl + uniqueFileName;
            
        } catch (Exception e) {
            // If WebP conversion fails and fallback is enabled, try JPEG
            if (fallbackToJpeg) {
                String uniqueFileName = UUID.randomUUID().toString() + "_" + baseName + ".jpg";
                Path targetLocation = uploadPath.resolve(uniqueFileName);
                
                float jpegQuality = quality / 100f; // Convert 0-100 to 0.0-1.0
                
                // Use Thumbnailator to save as JPEG with quality setting
                net.coobird.thumbnailator.Thumbnails.of(originalImage)
                    .scale(1.0) // Keep original size
                    .outputQuality(jpegQuality)
                    .outputFormat("jpg")
                    .toFile(targetLocation.toFile());
                
                // Return the URL path to the file
                return baseUrl + uniqueFileName;
            } else {
                // Re-throw the exception if fallback is disabled
                throw new IOException("Failed to convert image to WebP format: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        if (filePath != null && filePath.startsWith(baseUrl)) {
            String fileName = filePath.substring(baseUrl.length());
            Path targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            Files.deleteIfExists(targetLocation);
        } else {
            throw new IOException("Invalid file path: " + filePath);
        }
    }

    @Override
    public String getUploadDir() {
        return uploadDir;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public FileUploadResponse storePdf(MultipartFile file) throws IOException {
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IOException("Only PDF files are allowed");
        }

        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, "pdfs").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        
        // Generate a unique file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename()==null?"noName":file.getOriginalFilename());
        String baseName = originalFileName;
        if (originalFileName.contains(".")) {
            baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        }
        
        String uniqueFileName = UUID.randomUUID().toString() + "_" + baseName + ".pdf";
        Path targetLocation = uploadPath.resolve(uniqueFileName);
        
        // Copy file to target location
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return new FileUploadResponse(
                originalFileName,
                baseUrl + "pdfs/" + uniqueFileName,
                "application/pdf",
                file.getSize()
        );
    }

    @Override
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        
        // Extract the file name from the URL
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        
        // Create the file path
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        
        // Check if file exists
        if (Files.exists(filePath)) {
            // Delete the file
            Files.delete(filePath);
        } else {
            throw new IOException("File not found: " + fileName);
        }
    }
}
