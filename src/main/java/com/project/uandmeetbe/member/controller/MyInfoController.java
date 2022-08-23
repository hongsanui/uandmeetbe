package com.project.uandmeetbe.member.controller;

import com.project.uandmeetbe.code.Code;
import com.project.uandmeetbe.member.dto.MemberDTO;
import com.project.uandmeetbe.member.model.Member;
import com.project.uandmeetbe.member.service.MemberService;
import com.project.uandmeetbe.exception.CustomSignatureException;
import com.project.uandmeetbe.response.DefaultResponse;
import com.project.uandmeetbe.response.MyInfoResponse;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MyInfoController {

    private final MemberService memberService;

    @GetMapping("/user")
    public ResponseEntity<MyInfoResponse> getMyInformation(@RequestHeader(value="Authorization") String accessToken) {
        log.info("받아온 토큰 = {}", accessToken);
        try {
            MyInfoResponse myInfoResponse = new MyInfoResponse(Code.GOOD_REQUEST, memberService.getMyInfo(accessToken));

            return new ResponseEntity<>(myInfoResponse, HttpStatus.OK);
        }
        catch (SignatureException e){
            throw new CustomSignatureException();
        }
    }


    @PutMapping("/user")
    public ResponseEntity<DefaultResponse> modifyMyInformation(@RequestHeader(value="Authorization") String accessToken, @RequestBody MemberDTO memberDTO){

        Optional<Member> member =  memberService.updateMember(accessToken, memberDTO);

        DefaultResponse defaultResponse = new DefaultResponse(Code.GOOD_REQUEST);
        return new ResponseEntity<>(defaultResponse, HttpStatus.OK);
    }
}
