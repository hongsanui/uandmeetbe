package com.project.uandmeetbe.user.dto;

import com.project.uandmeetbe.mannerpoint.MannerPointStatus;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class UserOfMannerPoint {

    @NotNull
    @NotBlank
    private Long targetUserId;

    @NotNull
    @NotBlank
    private MannerPointStatus mannerPointStatus;

}
