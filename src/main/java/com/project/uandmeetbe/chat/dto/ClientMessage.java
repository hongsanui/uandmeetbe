package com.project.uandmeetbe.chat.dto;

import com.project.uandmeetbe.chat.SystemMessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <h1>ClientMessage</h1>
 * <p>
 *     사용자가 보낸 메세지를 받기위한 DTO
 * </p>
 */
@Setter
@Builder
@Getter
public class ClientMessage {
    private SystemMessageType messageType;
    private Long roomId;
    private String target;
    private String message;
    private String userNickname;
    private Long userId;
}
