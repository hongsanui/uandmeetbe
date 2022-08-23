package com.project.uandmeetbe.response;

import com.project.uandmeetbe.code.Code;
import com.project.uandmeetbe.jwt.Token;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TokenResponse extends DefaultResponse{

    private String accessToken;

    private String refreshToken;


    public TokenResponse(Code code, Token token){
        super(code);
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
