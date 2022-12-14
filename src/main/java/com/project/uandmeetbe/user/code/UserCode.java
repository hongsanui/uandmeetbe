package com.project.uandmeetbe.user.code;

import com.project.uandmeetbe.common.code.ResponseCode;

/**
 * <h1>UserCode</h1>
 * <p>
 *     User CRUD 및 기타 로직과 관련된 응답 Code
 * </p>
 *
 * @author younghoCha
 */
public enum UserCode implements ResponseCode {

    DUPLICATED_NICKNAME(1103, "중복된 닉네임입니다."),
    USER_NOT_FOUNT(1100, "유저를 찾을 수 없습니다."),
    NOT_ENTERED_INFORMATION(1102, "추가 정보가 입력되지 않았습니다."),
    DEFAULT_PROFILE_IMAGE(1111, "사용자의 이미지가 기본값으로 설정되어있습니다.");
    private final int code;
    private final String message;

    UserCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

