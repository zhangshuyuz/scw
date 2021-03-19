package com.yuu.scw.project.config;

import com.yuu.scw.project.component.OssTemplate;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class OssTemplateConfig {

    @Bean
    @ConfigurationProperties(prefix = "oss")
    public OssTemplate ossTemplate() {
        return new OssTemplate();
    }

}
