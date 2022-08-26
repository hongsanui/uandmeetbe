package com.project.uandmeetbe.common.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileOfImageSource {

    private String imageSource;
    private String extension;
}
