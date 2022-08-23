package com.project.uandmeetbe.member.controller;

import com.project.uandmeetbe.code.Code;
import com.project.uandmeetbe.member.dto.MemberDTO;
import com.project.uandmeetbe.member.model.Member;
import com.project.uandmeetbe.member.repository.MemberRepository;
import com.project.uandmeetbe.member.service.MemberService;
import com.project.uandmeetbe.jwt.JwtService;
import com.project.uandmeetbe.jwt.JwtTokenProvider;
import com.project.uandmeetbe.jwt.Token;
import com.project.uandmeetbe.response.DefaultResponse;
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
    public ResponseEntity<DefaultResponse> userSignup(@RequestBody MemberDTO memberDTO) {

        log.info("/member 요청됨");
        log.info("memberDTO ----> {}", memberDTO);

        memberService.memberSignup(memberDTO);

        DefaultResponse response = new DefaultResponse(Code.GOOD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);


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


    // 회원 유무 확인 (이메일로 회원 조회)
    @GetMapping("/user/check")
    public ResponseEntity userCheck(String email, String userName, String provider) {


        if(memberService.sinUpCheck(email)){
            DefaultResponse userResponse = new DefaultResponse(Code.GOOD_REQUEST);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        }


        DefaultResponse userResponse = new DefaultResponse(Code.GOOD_REQUEST);

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    // 닉네임 중복 확인 (닉네임으로 회원 조회)
    @GetMapping("/duplication")
    public ResponseEntity findByUserNickname(String nickname) {

        if(memberService.duplicationCheck(nickname)){
            DefaultResponse response = new DefaultResponse(Code.GOOD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }


        DefaultResponse response = new DefaultResponse(Code.DUPLICATED_NICKNAME);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
