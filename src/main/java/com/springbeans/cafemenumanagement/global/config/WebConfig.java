package com.springbeans.cafemenumanagement.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Path PRODUCT_IMAGE_ROOT =
            Paths.get("./uploads/products")
                    .toAbsolutePath()
                    .normalize();
    private static final String PRODUCT_IMAGE_URL =
            "/images/products/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(PRODUCT_IMAGE_URL)
                .addResourceLocations(
                        PRODUCT_IMAGE_ROOT.toUri().toString()
                );
    }
}
