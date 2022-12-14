package com.project.uandmeetbe.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <h1>ChatOfMessage</h1>
 * <p>
 *     채팅 내역 DTO에 들어갈 payload DTO
 * </p>
 */
@Getter
@Builder
public class ChatOfMessage {

    private String message;
    private LocalDateTime sendAt;
}
