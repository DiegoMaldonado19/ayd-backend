package com.ayd.sie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @PostConstruct
    public void initializeUploadDirectory() {
        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Create subdirectories for different types of files
            createSubdirectory(uploadDir, "evidence");
            createSubdirectory(uploadDir, "signatures");
            createSubdirectory(uploadDir, "documents");
            createSubdirectory(uploadDir, "profiles");

        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory: " + uploadPath, e);
        }
    }

    private void createSubdirectory(Path parent, String subdirName) throws IOException {
        Path subdir = parent.resolve(subdirName);
        if (!Files.exists(subdir)) {
            Files.createDirectories(subdir);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose upload directory as static resources
        String absolutePath = new File(uploadPath).getAbsolutePath();

        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/")
                .setCachePeriod(3600); // Cache for 1 hour
    }

    public String getUploadPath() {
        return uploadPath;
    }
}