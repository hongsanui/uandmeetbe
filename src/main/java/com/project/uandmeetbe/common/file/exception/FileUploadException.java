package com.project.uandmeetbe.common.file.exception;

/**
 * <h1>FileUploadException</h1>
 * <p>
 *  파일 저장관련 오류 시 발생하는 예외
 * </p>
 *
 */
public class FileUploadException extends RuntimeException {

    public FileUploadException() {
    }

    public FileUploadException(String message) {
        super(message);
    }
}
