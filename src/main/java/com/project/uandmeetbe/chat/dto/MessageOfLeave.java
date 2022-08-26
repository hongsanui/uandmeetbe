
package com.project.uandmeetbe.chat.dto;


import com.project.uandmeetbe.user.Gender;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageOfLeave {

    private Long id;
    private String userNickname;
    private int mannerPoint;
    private Gender gender;
}
