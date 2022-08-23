package com.project.uandmeetbe.member.service;

import com.project.uandmeetbe.member.dto.MemberDTO;
import com.project.uandmeetbe.member.model.Member;
import com.project.uandmeetbe.member.repository.MemberRepository;
import com.project.uandmeetbe.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final JwtTokenProvider jwtTokenProvider;

    private final MemberRepository memberRepository;

    public Optional<Member> getMemberFindByEmail(String email){
        return memberRepository.findByEmail(email);
    }
    public Optional<Member> getMemberFindByNickname(String nickname){
        return memberRepository.findByNickname(String nickname);
    }

    /**회원가입
     *
     * @param memberDTO
     */
    public void memberSignup(MemberDTO memberDTO){

        Member member = Member.builder()
                .memberSequenceId(1L)
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .gender(memberDTO.getGender())
                .memberBirthYear(memberDTO.getMemberBirthYear())
                .memberBirthMonth(memberDTO.getMemberBirthMonth())
                .memberBirthDay(memberDTO.getMemberBirthDay())
                .mannerPoint(memberDTO.getMannerPoint())
                .locationX(memberDTO.getLocationX())
                .locationY(memberDTO.getLocationY())
                .provider(memberDTO.getProvider())
                .memberState(memberDTO.getMemberState())
                .build();

        memberRepository.save(member);
    }

    public Optional<Member> getMyInfo(String accessToken){
        return getMemberByEmailFromAccessToken(accessToken);
    }

    public Optional<Member> updateMember(String accessToken, MemberDTO memberDTO){

        Member member = getMemberByEmailFromAccessToken(accessToken).get();
        member.update(memberDTO);
        memberRepository.save(member);
        return Optional.of(member);

    }

    // 엑세스 토큰으로부터 식별자값을 얻어 DB에 해당 유저 정보값을 받아와서 persistence context에 담음
    public Optional<Member> getMemberByEmailFromAccessToken(String accessToken){
        Claims claims = jwtTokenProvider.getClaims(accessToken);

        return memberRepository.findByEmail(claims.get("sub").toString());

    }
}
