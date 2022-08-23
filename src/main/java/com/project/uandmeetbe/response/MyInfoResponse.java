package com.project.uandmeetbe.response;

import com.project.uandmeetbe.code.Code;
import com.project.uandmeetbe.member.model.Member;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@ToString
@Getter
public class MyInfoResponse extends DefaultResponse{


    private Optional<Member> member;

    public MyInfoResponse(Code code, Optional<Member> member){
        super(code);
        this.member = member;
    }
}
