package com.squireofsoftware.peopleproject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "people-service")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class ProjectConfiguration {
    private final int maxHashRegenCount;
    private final int defaultQrCodeHeight;
    private final int defaultQrCodeWidth;
}
