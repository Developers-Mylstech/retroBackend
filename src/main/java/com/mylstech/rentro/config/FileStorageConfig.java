package com.mylstech.rentro.config;

import com.mylstech.rentro.exception.DirectoryCreationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;


@Configuration
@Slf4j
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            Files.createDirectories(uploadPath.resolve("pdfs"));
            
            log.info("Created directories at: " + uploadPath.toAbsolutePath());
        } catch (IOException e) {
            throw new DirectoryCreationException ("Could not initialize storage directories", e);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        // Serve image files
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");
        
        // Serve PDF files
        registry.addResourceHandler("/uploads/pdfs/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/pdfs/");
    }
}