package com.project.uandmeetbe.security.oauth2.model;

import java.util.Map;

/**
 * <h1>KakaoUser</h1>
 * <p>카카오 로그인 사용자의 계정 정보 클래스</p>
 *
 * @see com.project.uandmeetbe.security.oauth2.model.OAuth2UserInfo
 */
public class KakaoUser extends OAuth2UserInfo{

    private Map<String, Object> kakaoAccountAttributes;

    public KakaoUser(Map<String, Object> attributes) {
        super(attributes);
        kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getOAuth2Id() {
        return String.valueOf(super.attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccountAttributes.get("email");
    }

    @Override
    public OAuth2Provider getOAuth2Provider() {
        return OAuth2Provider.KAKAO;
    }
}
