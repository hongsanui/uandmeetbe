package com.project.uandmeetbe.user;

import com.project.uandmeetbe.common.code.CommonCode;
import com.project.uandmeetbe.common.dto.Response;
import com.project.uandmeetbe.security.annotation.CurrentUser;
import com.project.uandmeetbe.user.dto.UserOfMannerPoint;
import com.project.uandmeetbe.user.dto.UserOfModifyInfo;
import com.project.uandmeetbe.user.dto.UserOfMyInfo;
import com.project.uandmeetbe.user.dto.UserOfOtherInfo;
import com.project.uandmeetbe.user.exception.NicknameDuplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <h1>UserController</h1>
 * <p>
 * 사용자 계정과 관련된 CRUD 수행
 * </p>
 */

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    // 닉네임 중복확인
    @GetMapping("/api/user/duplication/nickname")
    public ResponseEntity<Response> nicknameDuplicationCheck(@CurrentUser User user, @RequestParam String userNickname){
        log.info("user id = {}", user.getId());
        if(userService.nicknameDuplicationCheck(userNickname, user)){
            // 존재하지 않을 경우
            return ResponseEntity.ok(Response.of(CommonCode.GOOD_REQUEST, null));
        }
        //존재할 경우
        throw new NicknameDuplicationException("닉네임이 중복되었습니다.");
    }

    @GetMapping("/api/user/{id}")
    public ResponseEntity<Response> otherInfo(@CurrentUser User user, @PathVariable Long id){
        UserOfOtherInfo userOfOtherInfo = userService.getOtherInfo(user, id);

        return ResponseEntity.ok(Response.of(CommonCode.GOOD_REQUEST, userOfOtherInfo));
    }

    //내 정보 수정
    @PostMapping("/api/user")
    public ResponseEntity<Response> modifyMyInfo(@CurrentUser User user,
                                                 @RequestBody @Validated UserOfModifyInfo userOfOtherInfo){

        userService.modifyMyInfo(user, userOfOtherInfo);
        return ResponseEntity.ok(Response.of(CommonCode.GOOD_REQUEST, null));
    }

    //내 정보 조회
    @GetMapping("/api/user")
    public ResponseEntity<Response> getMyInfo(@CurrentUser User user){
        UserOfMyInfo myInfo = userService.getMyInfo(user.getId());

        return ResponseEntity.ok(Response.of(CommonCode.GOOD_REQUEST, myInfo));
    }

    @PatchMapping("/api/user/manner_point")
    public ResponseEntity<Response> doMannerPointing(@CurrentUser User user, @RequestBody UserOfMannerPoint userOfMannerPoint){
        Response response = userService.mannerPointUp(user, userOfMannerPoint);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/user/imageSource")
    public ResponseEntity<Response> getProfileImageSource(@CurrentUser User user){
        return ResponseEntity.ok(userService.getProfileImageSource(user));
    }
}
