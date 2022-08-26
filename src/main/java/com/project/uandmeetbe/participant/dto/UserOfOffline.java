package com.project.uandmeetbe.participant.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserOfOffline {

    private Long userId;
    private String userNickname;
}
