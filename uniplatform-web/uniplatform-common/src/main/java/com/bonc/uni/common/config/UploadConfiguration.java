package com.bonc.uni.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UploadProperties.class)
public class UploadConfiguration {

    private final UploadProperties uploadProperties;

    public UploadConfiguration(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    public String getCommom() {
        return uploadProperties.getCommom();
    }

    public String getCorpus() {
        return uploadProperties.getCorpus();
    }

    public String getDcci() {
        return uploadProperties.getDcci();
    }

    public String getNlp() {
        return uploadProperties.getNlp();
    }

    public String getUsou() {
        return uploadProperties.getUsou();
    }

}
