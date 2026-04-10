package com.ecommerce.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google")
@Data
public class GoogleProperties {

    /** Android app package name, e.g. com.example.ecommerce */
    private String packageName;

    /** Filesystem path to the service account JSON key file */
    private String serviceAccountPath;
}
