package com.project.uandmeetbe.security.oauth2.handler;


import com.project.uandmeetbe.security.jwt.JwtProperties;
import com.project.uandmeetbe.security.jwt.service.JwtService;
import com.project.uandmeetbe.security.jwt.service.RefreshTokenService;
import com.project.uandmeetbe.security.oauth2.CustomOAuth2User;
import com.project.uandmeetbe.security.util.ClientIpUtils;
import com.project.uandmeetbe.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;


/**
 * <h1>OAuth2LoginAuthenticationSuccessHandler</h1>
 * <p>
 * OAuth2 인증 성공 이후 처리를 담당하는 클래스
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService<User> jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUrl;

    @Transactional // acl 생성을 위한 트랜잭션 처리
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 사용자 계정 ACL 적용을 위해 시큐리티 컨텍스트에 인증 객체 세팅 (추가적인 인증이나 권한 관리용으로는 사용되지 않음)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        // 로그인 된 사용자 계정
        User loggedInUser = oauth2User.getUser();

        // 추가정보 기입 여부
        boolean firstUser = oauth2User.isFirst();

        // 액세스 토큰 생성 - 만료기간은 분 단위로 설정
        String accessToken = jwtService.createToken(jwtProperties.getAccessTokenSigningKey(),
                jwtProperties.getAccessTokenExpirationTime(),
                ChronoUnit.MINUTES,
                createUserPayload(loggedInUser)
        );

        // 리프레쉬 토큰 생성 - 만료기간은 일 단위로 설정
        String refreshToken = jwtService.createToken(jwtProperties.getRefreshTokenSigningKey(),
                jwtProperties.getRefreshTokenExpirationTime(),
                ChronoUnit.DAYS,
                null
        );
        // 리프레쉬 토큰 DB 저장 (저장시 사용자의 접속 기기 정보를 고려함)
        String clientIp = ClientIpUtils.getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        refreshTokenService.saveRefreshToken(loggedInUser, refreshToken, clientIp, userAgent);

        String redirectUri = UriComponentsBuilder
                .fromUriString(redirectUrl)
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .queryParam("is_first", firstUser)
                .toUriString();


        response.sendRedirect(redirectUri);
    }

    /**
     * JWT 토큰에 들어갈 페이로드 정보를 반환한다.
     *
     * @param user 페이로드에 들어갈 사용자 정보가 있는 도메인 객체
     * @return payload
     */
    public Map<String, Object> createUserPayload(User user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", user.getId());
        payload.put("email", user.getEmail());
        payload.put("role", user.getRole());
        return payload;
    }

}
