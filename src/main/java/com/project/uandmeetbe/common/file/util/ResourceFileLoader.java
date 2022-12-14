package com.project.uandmeetbe.common.file.util;

import com.project.uandmeetbe.common.file.exception.ResourceFileNotFoundException;
import lombok.Getter;

import java.io.File;
import java.net.URL;
import java.util.Objects;

/**
 * <h1>ResourceFileLoader</h1>
 * <p>
 * 리소스 경로의 파일을 읽어오는 역할을 담당한다.
 * </p>
 *
 */
public class ResourceFileLoader {

    @Getter
    private File file;

    public ResourceFileLoader(String fileName) {
        URL resource = getClass().getResource(fileName);

        if (Objects.isNull(resource)) {
            throw new ResourceFileNotFoundException();
        }

        this.file = new File(resource.getPath());
    }
}
