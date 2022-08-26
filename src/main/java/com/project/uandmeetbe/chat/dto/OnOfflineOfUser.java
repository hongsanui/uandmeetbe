package com.project.uandmeetbe.chat.dto;

import com.project.uandmeetbe.chat.SystemMessageType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OnOfflineOfUser {
    private SystemMessageType messageType;
    private String message;
    private String userNickname;
    private Long userId;
}
