package com.ecommerce.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "apple")
@Data
public class AppleProperties {

    /** iOS app bundle identifier, e.g. com.example.ecommerce */
    private String bundleId;

    /** App Store Connect API key ID */
    private String keyId;

    /** App Store Connect issuer UUID */
    private String issuerId;

    /** Filesystem path to the .p8 private key file */
    private String privateKeyPath;

    /** SANDBOX or PRODUCTION */
    private String environment = "SANDBOX";

    public boolean isSandbox() {
        return "SANDBOX".equalsIgnoreCase(environment);
    }
}
