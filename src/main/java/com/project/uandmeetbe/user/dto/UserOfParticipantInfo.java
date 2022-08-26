package com.project.uandmeetbe.user.dto;

import com.project.uandmeetbe.area.ActiveArea;
import com.project.uandmeetbe.participant.Status;
import com.project.uandmeetbe.user.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserOfParticipantInfo {
    private Long id;
    private String userNickname;
    private int mannerPoint;
    private Gender gender;
    private List<ActiveArea> activeAreas;
    private List<String> interests;
    private String userProfileImagePath;
    private Status status;

}