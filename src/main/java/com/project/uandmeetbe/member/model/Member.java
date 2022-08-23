package com.project.uandmeetbe.member.model;

import com.project.uandmeetbe.area.ActiveAreas;
import com.project.uandmeetbe.interest.Interests;
import com.project.uandmeetbe.member.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "MEMBER")
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_SEQUENCE_ID")
    private Long memberSequenceId;

    @Column(name = "MEMBER_EMAIL")
    private String email;

    @Column(name = "MEMBER_NICKNAME")
    private String nickname;

    // TODO: 2022-08-23 이메일 + 소셜 이좋을까, 소셜만 하는게 좋을까?
//    @Column(name = "MEMBER_PASSWORD")
//    private String password;

    @Column(name = "MEMBER_BIRTH_YEAR", length = 4)
    private String memberBirthYear;

    @Column(name = "MEMBER_BIRTH_MONTH", length = 2)
    private String memberBirthMonth;

    @Column(name = "MEMBER_BIRTH_DAY", length = 2)
    private String memberBirthDay;

    @Column(name = "MEMBER_STATE", length = 5)
    private String memberState;

    @Column(name = "MANNER_POINT")
    private int mannerPoint;

    @Column(name = "LOCATION_X")
    private Double locationX;

    @Column(name = "LOCATION_Y")
    private Double locationY;

    @Column(name = "MEMBER_GENDER", length = 1)
    private Gender gender;

//    @Column(name = "MEMBER_PROFILE_IMG_URL")
//    private String profileImgUrl;

    @Column(name = "MEMBER_ROLE")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "PROVIDER", length = 20)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @OneToMany(mappedBy = "member")
    private List<Interests> interestsList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ActiveAreas> activeAreas = new ArrayList<>();

    // TODO: 2022-08-23 프로필 이미지 추가해야함!


    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    //UserDetails 상속 메소드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword(){
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void update(MemberDTO memberDTO){

        this.email = memberDTO.getEmail();
        this.nickname = memberDTO.getNickname();
        this.memberBirthYear = memberDTO.getMemberBirthYear();
        this.memberBirthMonth = memberDTO.getMemberBirthMonth();
        this.memberBirthDay = memberDTO.getMemberBirthDay();
        this.memberState = memberDTO.getMemberState();
        this.locationX = memberDTO.getLocationX();
        this.locationY = memberDTO.getLocationY();
        this.mannerPoint = memberDTO.getMannerPoint();
    }
}
