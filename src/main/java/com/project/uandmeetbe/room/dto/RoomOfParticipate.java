package com.project.uandmeetbe.room.dto;

import com.project.uandmeetbe.user.dto.UserOfParticipantInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RoomOfParticipate {

    RoomOfInfo roomOfInfo;

    List<UserOfParticipantInfo> participants;
}
