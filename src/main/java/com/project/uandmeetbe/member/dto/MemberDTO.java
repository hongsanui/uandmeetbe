package com.project.uandmeetbe.member.dto;

import com.project.uandmeetbe.member.model.Gender;
import com.project.uandmeetbe.member.model.Provider;
import com.project.uandmeetbe.member.model.Role;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class MemberDTO {

    private String email;
    private String nickname;
    private String memberBirthYear;
    private String memberBirthMonth;
    private String memberBirthDay;
    private String memberState;
    private int mannerPoint;
    private Double locationX;
    private Double locationY;
    private Gender gender;
    private Role role;
    private Provider provider;

    // TODO: 2022-08-23 프로필 이미지 추가해야함! 
}

//스프링부트 / 스프링 시큐리티 -> 스프링 시큐리티
//스프링 시큐리티 (필터체인) -> 각종 필터 작동 -> 스프링 부트(컨트롤러)
// login url -> id pw 받게끔 <- 스프링까지 안감 /login
