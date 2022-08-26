package com.project.uandmeetbe.user;

import com.project.uandmeetbe.area.ActiveArea;
import com.project.uandmeetbe.chat.ChatMessage;
import com.project.uandmeetbe.interest.Interest;
import com.project.uandmeetbe.participant.Participant;
import com.project.uandmeetbe.room.Room;
import com.project.uandmeetbe.security.oauth2.model.OAuth2Provider;
import com.project.uandmeetbe.user.dto.UserOfModifyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_OAUTH2_ID")
    private String oauth2Id;

    @Column(name = "USER_EMAIL")
    private String email;

    @Column(name = "USER_NICKNAME")
    private String nickname;

    @Column(name = "USER_OAUTH2_PROVIDER")
    private OAuth2Provider provider;

    @Column(name = "USER_ROLE")
    @Enumerated(EnumType.STRING)
    private Role role;

    //fixme yml에서 파일 끌고와야함
    //설정 안할 시 기본 이미지 등록
    @Column(name = "USER_PROFILE_IMAGE_PATH")
    private String userProfileImage; // 프로필 이미지 저장 경로

    @Column(name = "USER_GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "USER_BIRTH_DAY")
    private LocalDate userBirth;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID")
    private List<Interest> interests;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ActiveArea> activeAreas;

    @Column(name = "MANNER_POINT", columnDefinition = "int default 10")
    private int mannerPoint;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<Room> hostingRooms;

    @OneToMany(mappedBy = "createUser", cascade = CascadeType.ALL)
    private List<Room> madeRooms;

    @Column(name = "INFORMATION_REQUIRED")
    private boolean informationRequired;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Participant> participateRooms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages;


    /**
     * 신규 사용자 계정 엔티티를 생성후 반환한다.
     * 신규 사용자를 추가할때 사용한다.
     * @param oauth2Id - 소셜 로그인 후 받은 해당 OAuth2 계정 식별자
     * @param email - 소셜 로그인 후 받은 해당 OAuth2 프로필 정보상의 이메일
     * @param provider - OAuth2 사업자 (카카오,구글,페이스북 등)
     * @return account - 신규 계정 엔티티, 권한은 일반 사용자, 추가정보 입력 여부 미입력으로 자동설정
     */
    public static User createUser(String oauth2Id, String email, OAuth2Provider provider) {
        User newUser = new User();
        newUser.oauth2Id = oauth2Id;
        newUser.email = email;
        newUser.provider = provider;
        newUser.role = Role.ROLE_USER;
        newUser.informationRequired = true;
        newUser.userProfileImage = "/images/default_user_profile.jpeg";
        //fixme : 익명 수정
        newUser.nickname = "익명";
        return newUser;
    }

    /**
     * 기존 계정 정보를 바탕으로 계정 엔티티를 생성한다.<br>
     * 신규 계정 추가가 아닌 단순 계정 엔티티로의 변화가 필요할 때 사용한다.<br>
     * 이 메소드로 생성한 엔티티는 DB에서 조회하여 얻은 상태가 아니기 때문에 다양한 논리적 문제를 발생시킬 수 있다.<br>
     * User 에 대한 DB 조작이 필요한 경우 DB 에서 읽어온 User 엔티티로 처리해야 한다.
     *
     * @param id    계정 식별자
     * @param email 계정 이메일
     * @param role  계정 권한 정보
     * @return account 객체
     */
    public static User convertUser(Long id, String email, Role role) {
        User converted = new User();
        converted.id = id;
        converted.email = email;
        converted.role = role;
        return converted;
    }

    /**
     * 기존 계정 정보를 바탕으로 계정 엔티티를 수정한다.<br>
     * @param userOfModifyInfo   수정한 계정 정보
     * @param interests  수정한 관심사
     * @param userProfileImage   수정한 프로필사진
     */
    public void updateUser(UserOfModifyInfo userOfModifyInfo, List<Interest> interests, String userProfileImage) {
        this.gender = userOfModifyInfo.getGender();
        this.userBirth = userOfModifyInfo.getUserBirth();
        this.interests = interests;
        this.activeAreas = userOfModifyInfo.getActiveAreas();
        if(userProfileImage != null){
            this.userProfileImage = userProfileImage;
        }
        this.nickname = userOfModifyInfo.getUserNickname();
        this.informationRequired = false;
    }

    /**
     * 매너포인트<br>
     * 사용자가 추가할때마다 +1 씩 올라간다.
     * @param point
     */
    public void updateMannerPoint(int point){
        this.mannerPoint = this.mannerPoint + point;
    }
    public void out(Participant participantEntity){
        int i = 0;
        for(Participant participant : getParticipateRooms()){
            if(participant.getId() == participantEntity.getId()){
                getParticipateRooms().remove(i);
                break;
            }
            i++;
        }
    }

    /**
     * 활동 지역을 삭제할 때 사용한다.
     */
    public void deleteActiveArea(){
        if(!(this.activeAreas.isEmpty())) {
            this.activeAreas.clear();
        }
    }

    /**
     * 활동 지역을 업데이트할 때 사용한다.
     * @param activeAreaList
     */
    public void updateActiveArea(ActiveArea activeAreaList){
        this.activeAreas.add(activeAreaList);

    }
}
