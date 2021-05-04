package io.thundra.petclinicnotificationapp.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "io.thundra.config.aws")
@Getter
@Setter
public class ThundraConfig {
    private String queueUrl;

    private String profile;

    private String region;
}
