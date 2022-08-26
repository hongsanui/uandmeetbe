package com.project.uandmeetbe.user.dto;


import com.project.uandmeetbe.area.ActiveArea;
import com.project.uandmeetbe.mannerpoint.MannerPointStatus;
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
public class UserOfOtherInfo {
    private Long id;
    private String userNickname;
    private int mannerPoint;
    private Gender gender;
    private List<ActiveArea> activeAreas;
    private List<String> interests;
    private String userProfileImagePath;
    private MannerPointStatus mannerType;
}