package com.project.uandmeetbe.member.controller;

import com.project.uandmeetbe.code.Code;
import com.project.uandmeetbe.member.dto.MemberDTO;
import com.project.uandmeetbe.member.model.Member;
import com.project.uandmeetbe.member.repository.MemberRepository;
import com.project.uandmeetbe.member.service.MemberService;
import com.project.uandmeetbe.jwt.JwtService;
import com.project.uandmeetbe.jwt.JwtTokenProvider;
import com.project.uandmeetbe.jwt.Token;
import com.project.uandmeetbe.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final MemberService memberService;




    // 회원가입 요청
    @PostMapping("/member")
    public Map<String, String> memberSignup(@RequestBody MemberDTO memberDTO) {

        log.info("/member 요청됨");
        Map<String, String> map = new HashMap<>();
        map.put("memberSignup", "true");

        // 잘못된 인수가 메서드에 요청될 경우 exception 발생
        /*Optional.of(memberService.memberSignup(memberDTO)).orElseThrow(
                () -> new IllegalArgumentException("부적절한 파라미터가 요청되었습니다. -> "));*/

        log.info("memberDTO -------------------> " + memberDTO);

        try {
            memberService.memberSignup(memberDTO);
        } catch (IllegalArgumentException e) {
            e.getMessage();
            map.put("memberSignup", "false");
        }

        return map;
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody Map<String, String> member, @RequestHeader("User-Agent") String memberAgent){
        log.info("member email = {}", member.get("email"));
        Member member1 = memberRepository.findByEmail(member.get("email"))
                .orElseThrow(
                        () -> new UsernameNotFoundException("가입되지 않은 이메일 입니다."));

        Token tokenDto = jwtTokenProvider.createAccessToken(member1.getUsername(),member1.getRoles());
        log.info("getRole = {}", member1.getRoles());
        jwtService.login(tokenDto, memberAgent);
        TokenResponse tokenResponse = new TokenResponse(Code.GOOD_REQUEST,tokenDto);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @GetMapping("/member/check")
    public Map<String, String> memberCheck(@RequestBody MemberDTO memberDTO) {

        Map<String, String> map = new HashMap<>();

        try {
            Optional<Member> member = memberService.getMemberFindByEmail(memberDTO.getEmail());

            // 회원 조회에 성공했을 경우. (데이터가 있을 경우)
            if (member.isPresent()) {
                map.put("memberCheck", "true");
                return map;

            }
            map.put("memberCheck", "false");
            return map;

        } catch (NoSuchElementException e) {
            log.info("요청 데이터의 값이 없습니다. -> " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("잘못된 파라미터 요청 입니다. -> " + e.getMessage());
        }

        return map;
    }


}
