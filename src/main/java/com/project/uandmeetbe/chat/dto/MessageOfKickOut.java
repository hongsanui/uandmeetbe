package com.project.uandmeetbe.chat.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * <h1>MessageOfKickOut</h1>
 * 강퇴 기능에 대한 응답 메세지를 위한 DTO 클래스
 */

@Getter
@Builder
public class MessageOfKickOut {

    private Long id;
    private String userNickname;
}
