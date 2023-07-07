package com.webshopproject.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //path photo user
        String directoryName = "user-photos";
        Path userPhotosDirectory = Paths.get(directoryName);
        String userPhotoPath = userPhotosDirectory.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + directoryName + "/**")
                .addResourceLocations("file:" + userPhotoPath + "/");

        //path images category
        String categoryImagesDirName = "category-images";
        Path categoryImagesDir = Paths.get(categoryImagesDirName);
        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + categoryImagesDirName + "/**")
                .addResourceLocations("file:" + categoryImagesPath + "/");
    }
}
