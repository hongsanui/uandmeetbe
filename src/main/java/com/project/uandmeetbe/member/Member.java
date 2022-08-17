package com.project.uandmeetbe.member;

import com.nimbusds.openid.connect.sdk.claims.Gender;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;    //회원 고유 번호

    @Column(name = "MEMBER_OAUTH2_ID")
    private String oauth2Id;    //소셜로그인(카카오,구글,네이버 등) 아이디

    @Column(name = "MEMBER_NICKNAME")
    private String nickname;    //닉네임

    @Column(name = "MEMBER_OAUTH2_PROVIDER")
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;    //oauth2공급자

    @Column(name = "MEMBER_ROLE")
    @Enumerated(EnumType.STRING)
    private Role role;  //회원 권한(관리자, 일반회원)

    @Column(name = "MEMBER_PROFILE_IMAGE_PATH")
    private String memberProfileImage;  //프로필 이미지 저장경로

    @Column(name = "MEMBER_GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender;  //성별

    @Column(name = "MEMBER_BIRTH_DAY")
    private LocalDate memberBirth;  //회원 생일

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "MEMBER_ID")
    private List<Interest> interests;   //관심목록

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<ActiveArea> activeAreas; //활동 지역

    @Column(name = "MANNER_POINT",columnDefinition = "int default 10")
    private int mannerPoint;    //매너점수

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<Room> hostingRooms;    //활성화 방 목록

    @OneToMany(mappedBy = "createMember", cascade = CascadeType.ALL)
    private List<Room> madeRooms;   //만든 방 목록

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Participant> participateRooms; //참여방 목록

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages; //채팅 메시지들


}
